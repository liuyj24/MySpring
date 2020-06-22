package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;

import java.lang.annotation.Annotation;
import java.util.*;

public class AspectWeaver {

    private BeanContainer beanContainer;

    public AspectWeaver() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        //1. 获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        //2. 将切面类按照不同的织入目标进行切分
        Map<Class<? extends Annotation>, List<AspectInfo>> categoriedMap = new HashMap<>();
        if (aspectSet == null || aspectSet.isEmpty()) {
            return;
        }
        for (Class<?> aspectClass : aspectSet) {
            //验证Aspect的合法性
            if (verifyAspect(aspectClass)) {
                //对Aspect进行分类
                categorizeAspect(categoriedMap, aspectClass);
            } else {
                throw new RuntimeException("@Aspect and @Order have not been added to the Aspect class" +
                        "or Aspect class does not extend from DefaultAspect, or the value in Aspect Tag equals @Aspect");
            }
        }
        //3. 按照不同的织入目标分别去按序织入Aspect的逻辑
        if(null == categoriedMap || categoriedMap.size() == 0){
            return;
        }
        for (Class<? extends Annotation> category : categoriedMap.keySet()) {
            weaveByCategory(category, categoriedMap.get(category));
        }
    }

    /**
     * 框架中一定要遵守给Aspect类添加@Aspect和@Order标签的规范，同时，必须继承自DefaultAspect.class
     * 此外，@Aspect的属性值不能是它本身
     *
     * @param aspectClass
     * @return
     */
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class) &&
                aspectClass.isAnnotationPresent(Order.class) &&
                DefaultAspect.class.isAssignableFrom(aspectClass) &&
                aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
    }

    private void categorizeAspect(Map<Class<? extends Annotation>, List<AspectInfo>> categoriedMap, Class<?> aspectClass) {
        Order orderTag = aspectClass.getAnnotation(Order.class);
        Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);

        DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass);
        AspectInfo aspectInfo = new AspectInfo(orderTag.value(), aspect);

        if(!categoriedMap.containsKey(aspectTag.value())){
            //如果织入的joinpoint第一次出现，则以该joinpoint为key，以创建新的List<AspectInfo>为value
            List<AspectInfo> aspectInfoList = new ArrayList<>();
            aspectInfoList.add(aspectInfo);
            categoriedMap.put(aspectTag.value(), aspectInfoList);
        }else{
            //如果织入的joinpoint不是第一次出现，则往joinpoint对应的value里添加新的Aspect逻辑
            List<AspectInfo> aspectInfoList = categoriedMap.get(aspectTag.value());
            aspectInfoList.add(aspectInfo);
        }
    }

    /**
     * 根据织入目标进行织入
     * @param category
     * @param aspectInfoList
     */
    private void weaveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfoList) {
        //1. 获取被代理类的集合
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);
        if(classSet == null || classSet.isEmpty()){
            return;
        }
        //2. 遍历被代理的类，分别为每个被代理类生成动态代理实例
        for (Class<?> targetClass : classSet) {
            AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, aspectInfoList);
            //创建动态代理对象
            Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);

            //3. 将动态代理对象实例添加到容器里，取代未被代理前的类实例
            beanContainer.addBean(targetClass, proxyBean);
        }

    }
}
