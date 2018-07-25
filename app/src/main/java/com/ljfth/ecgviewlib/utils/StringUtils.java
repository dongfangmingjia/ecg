package com.ljfth.ecgviewlib.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}
