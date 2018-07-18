package com.ljfth.ecgviewlib.utils;

import android.text.TextUtils;

public class StringUtils {

    public static int string2Int(String id) {
        int tid = 0;
        try {
            if (!TextUtils.isEmpty(id) && !TextUtils.isDigitsOnly(id))
                return tid;
            tid = !TextUtils.isEmpty(id) ? Integer.parseInt(id) : 0;
        } catch (Exception e) {
        }
        return tid;
    }
}
