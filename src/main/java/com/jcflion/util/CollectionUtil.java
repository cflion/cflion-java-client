package com.jcflion.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kanner
 */
public final class CollectionUtil {

    private CollectionUtil() {}

    public static <T> boolean isNotEmpty(T[] arr) {
        if (null != arr && arr.length > 0) {
            return true;
        }
        return false;
    }

    public static <T> boolean isEmpty(T[] arr) {
        return !isNotEmpty(arr);
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        if (null != map && map.size() > 0) {
            return true;
        }
        return false;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return !isNotEmpty(map);
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        if (null != list && list.size() > 0) {
            return true;
        }
        return false;
    }

    public static <T> boolean isEmpty(List<T> list) {
        return !isNotEmpty(list);
    }

    public static <T> boolean isNotEmpty(Set<T> set) {
        if (null != set && set.size() > 0) {
            return true;
        }
        return false;
    }

    public static <T> boolean isEmpty(Set<T> set) {
        return !isNotEmpty(set);
    }

}
