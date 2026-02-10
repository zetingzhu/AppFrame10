package com.trade.zt_appdevicesinfo.ui;

/**
 * Created by dufangzhu on 2017/9/21.
 * Stringutil
 */

public class CLStringUtil {
    public static boolean isEmpty(String str) {
        if (null == str)
            return true;
        if (str.trim().length() == 0)
            return true;
        if (str.trim().equalsIgnoreCase("null"))
            return true;
        return false;
    }

}
