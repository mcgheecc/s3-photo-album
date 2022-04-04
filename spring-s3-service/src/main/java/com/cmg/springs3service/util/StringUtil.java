package com.cmg.springs3service.util;

public class StringUtil {

    private StringUtil() {}

    public static boolean isNotNull(String str) {
        return (str != null && !"null".equals(str) && !str.isBlank());
    }
}
