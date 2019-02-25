package net.htwater.njdistrictfx.util;

/**
 * Created by 96955 on 2017/6/27.
 */

public class StringUtil {
    /**
     * 字符串为空
     */
    public static boolean isBlank(String str) {
        return (null == str || "".equals(str));
    }

    /**
     * 字符串非空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
