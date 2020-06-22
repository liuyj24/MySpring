package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 根据包名获取Class集合
     *
     * @param packageName
     * @return
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        //1.获取到类的加载器
        ClassLoader classLoader = getClassLoader();
        //2.通过类加载器获取指定目录下的资源（最终获取的是绝对路径的url）
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if (url == null) {
            log.warn("获取指定目录下的资源失败！");
            return null;
        }
        //3.根据不同的资源类型，采用不同的方式获取资源集合
        Set<Class<?>> classSet = null;
        //过滤出文件类型的资源
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }

    /**
     * 递归获取目标package里面的所有class文件
     *
     * @param classSet
     * @param fileSource
     * @param packageName
     */
    private static void extractClassFile(Set<Class<?>> classSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()) {
            return;
        }
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    //获取文件的绝对路径
                    String absoluteFilePath = file.getAbsolutePath();
                    if (absoluteFilePath.endsWith(".class")) {
                        //若是.class文件则直接加载
                        addToClassSet(absoluteFilePath);
                    }
                }
                return false;
            }

            private void addToClassSet(String absoluteFilePath) {
                //1.从class文件的绝对路径中提取出包含package的类名
                absoluteFilePath = absoluteFilePath.replace(File.separator, ".");
                String pathName = absoluteFilePath.substring(absoluteFilePath.indexOf(packageName));
                pathName = pathName.substring(0, pathName.lastIndexOf("."));
                //2.通过反射机制获取对应的Class对象并加入到ClassSet中
                Class<?> clazz = loadClass(pathName);
                classSet.add(clazz);
            }
        });

        if (files != null) {
            for (File f : files) {
                extractClassFile(classSet, f, packageName);
            }
        }
    }

    public static Class<?> loadClass(String pathName) {
        try {
            return Class.forName(pathName);
        } catch (ClassNotFoundException e) {
            log.error("load class error: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据类实例化对象
     * @param clazz
     * @param accessible 是否支持创建出私有class对象的实例
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            log.error("newInstance error");
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取ClassLoader
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static void main(String[] args) {
        String str = "abc123.class";
        String path = str.substring(str.indexOf("123"));
        path = path.substring(0, path.lastIndexOf("."));
        System.out.println(path);
    }

    /**
     * 给成员变量赋值
     */
    public static void setFieldInstance(Field field, Object target, Object value, boolean access){
        field.setAccessible(access);
        if(field == null){
            log.warn("field is null");
            return;
        }
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("set instance to field fail");
            throw new RuntimeException(e);
        }
    }

}
