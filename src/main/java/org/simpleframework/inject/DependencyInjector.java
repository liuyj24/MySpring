package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {

    private BeanContainer beanContainer;

    public DependencyInjector(){
        this.beanContainer = BeanContainer.getInstance();
    }

    public void doIoc(){
        //1.遍历Bean容器中所有的Class对象
        Set<Class<?>> classSet = this.beanContainer.getClasses();
        if(classSet == null || classSet.isEmpty()){
            log.warn("beanContainer is empty");
            return;
        }
        for (Class<?> clazz : classSet) {
            //2.遍历每个对象的成员变量
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //3.找出被@Autowired标注的成员变量
                if(field.isAnnotationPresent(Autowired.class)){
                    //获取Autowire实例
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowireValue = autowired.value();
                    //4.获取该成员变量的类型
                    Class<?> fieldType = field.getType();
                    //5.从beanContainer中获得成员变量的实例对象
                    Object instance = getFieldInstance(fieldType, autowireValue);
                    if(instance == null){
                        throw new RuntimeException("inject fail, target fieldclass: " + clazz.getName() + ", autowired class: " + fieldType);
                    }else{
                        Object targetInstance = beanContainer.getBean(clazz);
                        //6.通过反射把实例对象注入给目标对象
                        ClassUtil.setFieldInstance(field, targetInstance, instance, true);
                    }
                }
            }
        }

    }

    private Object getFieldInstance(Class<?> fieldType, String autowireValue) {
        if(fieldType == null){
            log.warn("fieldType is null");
            return null;
        }
        Object instance = this.beanContainer.getBean(fieldType);
        if(instance != null){
            return instance;
        }else{
            //获取其实现类的类型
            Class<?> implementClass = getImplementClass(fieldType, autowireValue);
            if(implementClass != null){
                return this.beanContainer.getBean(implementClass);
            }else{
                return null;
            }
        }
    }

    /**
     * 获取接口的实现类
     * @param interf
     * @return
     */
    private Class<?> getImplementClass(Class<?> interf, String autowireValue) {
        Set<Class<?>> classSet = this.beanContainer.getClassesBySuper(interf);
        if(classSet != null && classSet.size() != 0){
            //根据autowireValue判断有没有给出指定实现类
            if(autowireValue.equals("") || autowireValue == null){
                if(classSet.size() == 1){
                    return classSet.iterator().next();
                }else{
                    //多于两个实现类，用户没有明确指定
                    throw new RuntimeException(interf + " don't know which implement to choose," +
                            " please set @Autowired value to pick one");
                }
            }else{
                for (Class<?> clazz : classSet) {
                    if(clazz.getSimpleName().equals(autowireValue)){
                        return clazz;
                    }
                }
            }

        }
        return null;
    }

}
