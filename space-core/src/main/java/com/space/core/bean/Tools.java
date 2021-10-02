package com.space.core.bean;

import java.util.List;

/**
 * @author xulinglin
 */
public class Tools {

    public static final int zero = 0;

    public static boolean isNotNull(List records){
        return (null != records && records.size() > 0);
    }

    public static boolean isNull(List records){
        return !isNotNull(records);
    }

    public static boolean isBlank(final CharSequence cs) {
        return !hasText(cs);
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return hasText(cs);
    }

    public static boolean hasText(CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
