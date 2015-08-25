package com.lchpatners.shadal.util;

/**
 * Created by youngkim on 2015. 8. 20..
 */
public class LogUtils {
    public static final String PRE_FIX = "Camdal_";

    public static String makeTag(Class clazz) {
        return PRE_FIX + clazz.getSimpleName();
    }
}