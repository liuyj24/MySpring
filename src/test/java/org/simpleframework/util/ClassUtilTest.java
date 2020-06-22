package org.simpleframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ClassUtilTest {

    @Test
    @DisplayName("提取目标类方法：extractClassFileTest")
    public void extractClassFileTest(){
        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.liuyj.entity");
        System.out.println(classSet);
        Assertions.assertEquals(4, classSet.size());
    }
}
