package org.simpleframework.core;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BeanContainer {

    private BeanContainer() {
    }

    /**
     * 存放所有被配置标记的目标对象的Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    public int size() {
        return beanMap.size();
    }

    /**
     * 存放注解的Class
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class, Aspect.class);

    /**
     * 获取Bean的单例容器
     * 线程安全，能抵御反射和序列化入侵
     *
     * @return
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 判断容器是否已经加载过bean
     */
    private boolean loaded = false;

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * 扫描加载所有Bean
     *
     * @param packageName
     */
    public synchronized void loadBeans(String packageName) {
        //获取到该包下的所有类
        if (loaded) {
            log.warn("the BeanContainer has been loaded");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("extract nothing from packageName: " + packageName);
            return;
        }
        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                if (clazz.isAnnotationPresent(annotation)) {
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
            }
        }
        loaded = true;
    }

    /**
     * 添加一个class对象，及其Bean实例
     *
     * @return
     */
    public Object addBean(Class<?> clazz, Object bean) {
        return this.beanMap.put(clazz, bean);
    }

    /**
     * 删除一个IOC容器的管理对象
     *
     * @return
     */
    public Object removeBean(Class<?> clazz) {
        return this.beanMap.remove(clazz);
    }

    /**
     * 根据class获取Bean
     *
     * @return
     */
    public Object getBean(Class clazz) {
        return this.beanMap.get(clazz);
    }

    /**
     * 获取所有的Class
     *
     * @return
     */
    public Set<Class<?>> getClasses() {
        return this.beanMap.keySet();
    }

    /**
     * 获取所有的Bean
     * @return
     */
    public Set<?> getBeans(){
        return new HashSet<>(this.beanMap.values());
    }

    /**
     * 根据注解获取对应的Class
     * @return
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation){
        //1. 获取所有的class集合
        Set<Class<?>> classSet = getClasses();
        if(classSet == null || classSet.isEmpty()){
            log.warn("annotation: " + annotation + " has no class");
            return null;
        }
        //2. 筛选出Annotation对应的
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(annotation)){
               result.add(clazz);
            }
        }
        return result.size() > 0 ? result : null;
    }

    /**
     * 根据传入的接口或父类，获取对应实现类和子类的class
     * @param interfaceOrClass
     * @return
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass){
        //1. 获取所有的class集合
        Set<Class<?>> classSet = getClasses();
        if(classSet == null || classSet.isEmpty()){
            log.warn("interfaceOrClass: " + interfaceOrClass + " has no impl or son class");
            return null;
        }
        //2. 筛选出对应实现类和子类的class
        Set<Class<?>> result = new HashSet<>();
        for (Class<?> clazz : classSet) {
            if(interfaceOrClass.isAssignableFrom(clazz)){
                result.add(clazz);
            }
        }
        return result.size() > 0 ? result : null;
    }
}
