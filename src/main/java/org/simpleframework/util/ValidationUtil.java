package org.simpleframework.util;

import java.util.Collection;

/**
 * 做判断的通用工具
 */
public class ValidationUtil {
    /**
     * 判断Collection是否为null或size是否为0
     * @return
     */
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

}
