package com.yss.fsip.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CollectionUtil
 *
 * @author jingminy
 * @date 2019/12/26 17:34
 */
public class CollectionUtil {

    /**
     * String --> List<String>
     *
     * @author jingminy
     * @date 2019/12/18 9:56
     */
    public static List<String> convertStr2List(String str) {
        String[] strArr = str.split(",");
        return Arrays.asList(strArr);
    }

    /**
     * String --> Set<String>
     *
     * @author jingminy
     * @date 2019/12/18 9:56
     */
    public static Set<String> convertStr2Set(String str) {
        List<String> list = convertStr2List(str);
        Set<String> set = new HashSet<String>();
        set.addAll(list);
        return set;
    }
}
