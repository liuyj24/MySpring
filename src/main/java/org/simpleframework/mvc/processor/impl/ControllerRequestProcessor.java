package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.render.JsonResultRender;
import org.simpleframework.mvc.render.ResourceNotFoundResultRender;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.render.ViewResultRender;
import org.simpleframework.mvc.type.ControllerMethod;
import org.simpleframework.mvc.type.RequestPathInfo;
import org.simpleframework.util.ConverterUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static jdk.nashorn.api.scripting.ScriptUtils.convert;

/**
 * Controller请求处理器
 */
@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {
    //IOC容器
    private BeanContainer beanContainer;
    //请求和controller方法的映射集合
    private Map<RequestPathInfo, ControllerMethod> pathControllerMethodMap = new ConcurrentHashMap<>();

    /**
     * 依靠容器的能力，建立起请求路径，请求方法与Controller方法实例的映射
     */
    public ControllerRequestProcessor() {
        this.beanContainer = BeanContainer.getInstance();
        Set<Class<?>> requestMappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMethodMap(requestMappingSet);
    }

    /**
     * 初始化pathControllerMethodMap
     *
     * @param requestMappingSet
     */
    private void initPathControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (requestMappingSet == null || requestMappingSet.isEmpty()) {
            return;
        }
        //1. 遍历所有被@RequestMapping标记的类，获取类上面该注解的属性值作为一级路径
        for (Class<?> requestMappingClass : requestMappingSet) {

            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);

            //获取一级路径
            String basePath = requestMapping.value();
            //为了方便匹配路径，给一级路径的开头加上/
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }

            //2. 遍历类里所有被@RequestMapping标记的方法，获取方法上面该注解的属性值，作为二级路径
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if (methods == null || methods.length == 0) {
                continue;
            }
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {

                    RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                    String methodPath = methodRequest.value();

                    if (!methodPath.startsWith("/")) {
                        methodPath = "/" + methodPath;
                    }

                    String url = basePath + methodPath;

                    //3. 解析方法里被@RequestParam标记的参数
                    //获取该注解的属性值，作为参数名
                    //获取被标记的参数的数据类型，建立参数名和参数类型的映射
                    Map<String, Class<?>> methodParams = new HashMap<>();
                    Parameter[] parameters = method.getParameters();

                    if (parameters != null && parameters.length != 0) {
                        for (Parameter parameter : parameters) {
                            RequestParam param = parameter.getAnnotation(RequestParam.class);
                            //目前暂定为Controller方法里面所有的参数都需要@RequestParam注解
                            if (param == null) {
                                throw new RuntimeException("the parameter must have @RequestParam");
                            }
                            methodParams.put(param.value(), parameter.getType());
                        }
                    }

                    //4. 将获取到的信息封装成RequestPathInfo实例和ControllerMethod实例，放到映射表里
                    //获取该方法是GET还是POST
                    String httpMethod = String.valueOf(methodRequest.method());
                    RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);

                    if (this.pathControllerMethodMap.containsKey(requestPathInfo)) {
                        log.warn("duplicate url: {} registration, current class: {}, method: {} will override the former one",
                                requestPathInfo.getPath(), requestMappingClass.getName(), method.getName());
                    }
                    ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParams);
                    //组装好存放到Map中
                    this.pathControllerMethodMap.put(requestPathInfo, controllerMethod);
                }
            }
        }

    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1. 解析HttpServletRequest的请求方法，请求路径，获取对应的ControllerMethod实例
        String method = requestProcessorChain.getRequestMethod();
        String path = requestProcessorChain.getRequestPath();
        //去Map中获取对应的ControllerMethod（已重写equals方法）
        ControllerMethod controllerMethod = this.pathControllerMethodMap.get(new RequestPathInfo(method, path));

        //若map中没有，认为资源不存在
        if (controllerMethod == null) {
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(method, path));
            return false;
        }

        //2. 解析请求参数，并传递给获取到的ControllerMethod实例去执行
        Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());

        //3. 根据执行的结果，选择对应的render进行渲染
        setResultRender(result, controllerMethod, requestProcessorChain);
        return true;
    }

    /**
     * 根据不同情况设置不同的渲染器
     *
     * @param result
     * @param controllerMethod
     * @param requestProcessorChain
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        if (result == null) {
            return;
        }
        ResultRender resultRender;
        //判断是否有ResponseBody注解，有则返回json串
        boolean isJson = controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            resultRender = new JsonResultRender(result);
        } else {
            resultRender = new ViewResultRender(result);
        }
        requestProcessorChain.setResultRender(resultRender);
    }

    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        //1. 从请求里获取GET或者POST的参数名及其对应的值
        Map<String, String> requestParamMap = new HashMap<>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length != 0) {
                requestParamMap.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        //2. 根据获取到的请求参数名及其对应的值，以及controllerMethod里的参数类型，实例化被调用的方法的参数
        //存放实例化后的参数
        List<Object> methodParams = new ArrayList<>();
        //获取解析注解后得到的参数类型
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();
        for (String paramName : methodParamMap.keySet()) {
            //获取一个参数类型
            Class<?> type = methodParamMap.get(paramName);
            //获取该参数类型对应的实际值
            String requestValue = requestParamMap.get(paramName);
            //通过反射创建该参数实例
            Object value;
            //只支持String，char，int，short，byte，double，long，float，boolean及他们的包装类
            if (null == requestValue) {
                //将请求里的参数值转换为适配的空值
                value = ConverterUtil.primitiveNull(type);
            } else {
                value = ConverterUtil.convert(type, requestValue);
            }
            methodParams.add(value);
        }
        //3. 执行Controller里面对应的方法并返回结果
        Object controller = this.beanContainer.getBean(controllerMethod.getControllerClass());
        Method invokeMethod = controllerMethod.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            //如果是调用异常，需要通过e.getTargetException()去获取方法抛出的异常
            throw new RuntimeException(e.getTargetException());
        }
        return result;
    }
}
