package com.ljfth.ecgviewlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class EcgSharedPrefrence {

    public static final String FILE_NAME = "fill_ecg";

    private static SharedPreferences getPrefrence(Context mContext) {
        return mContext.getSharedPreferences(FILE_NAME, 0);
    }

    /**
     * 设置姓名
     * @param context
     * @param name
     */
    public static void setName(Context context, String name) {
        getPrefrence(context).edit().putString(Constant.SP_NAME, name).apply();
    }


    public static String getName(Context context) {
        return getPrefrence(context).getString(Constant.SP_NAME, "");
    }

    /**
     * 设置年龄
     * @param context
     * @param age
     */
    public static void setAge(Context context, String age) {
        getPrefrence(context).edit().putString(Constant.SP_AGE, age).apply();
    }


    public static String getAge(Context context) {
        return getPrefrence(context).getString(Constant.SP_AGE, "");
    }

    /**
     * 设置性别
     * @param context
     * @param sex
     */
    public static void setSex(Context context, int sex) {
        getPrefrence(context).edit().putInt(Constant.SP_SEX, sex).apply();
    }


    public static int getSex(Context context) {
        return getPrefrence(context).getInt(Constant.SP_SEX, Constant.SEX_MEN);
    }

    /**
     * 设置住院号
     * @param context
     * @param hospitalNum
     */
    public static void setHospitalNum(Context context, String hospitalNum) {
        getPrefrence(context).edit().putString(Constant.SP_H_NUM, hospitalNum).apply();
    }


    public static String getHospitalNum(Context context) {
        return getPrefrence(context).getString(Constant.SP_H_NUM, "");
    }

    /**
     * 设置床号
     * @param context
     * @param bedNum
     */
    public static void setBedNum(Context context, String bedNum) {
        getPrefrence(context).edit().putString(Constant.SP_BED_NUM, bedNum).apply();
    }


    public static String getBedNum(Context context) {
        return getPrefrence(context).getString(Constant.SP_BED_NUM, "");
    }

    /**
     * 设置是否起搏
     * @param context
     * @param isMaking
     */
    public static void setPaceMaking(Context context, boolean isMaking) {
        getPrefrence(context).edit().putBoolean(Constant.SP_PACE_MAKING, isMaking).apply();
    }


    public static boolean getPaceMaking(Context context) {
        return getPrefrence(context).getBoolean(Constant.SP_PACE_MAKING, false);
    }

    /**
     * 设置血氧上限
     * @param context
     * @param spo2
     */
    public static void setSpo2Upper(Context context, String spo2) {
        getPrefrence(context).edit().putString(Constant.SP_SPO2_UPPER, TextUtils.isEmpty(spo2) ? String.valueOf(Constant.SPO2TOP) : spo2).apply();
    }


    public static String getSpo2Upper(Context context) {
        return getPrefrence(context).getString(Constant.SP_SPO2_UPPER, String.valueOf(Constant.SPO2TOP));
    }

    /**
     * 设置血氧下限
     * @param context
     * @param spo2
     */
    public static void setSpo2Floor(Context context, String spo2) {
        getPrefrence(context).edit().putString(Constant.SP_SPO2_FLOOR, TextUtils.isEmpty(spo2) ? String.valueOf(Constant.SPO2BOTTOM) : spo2).apply();
    }


    public static String getSpo2Floor(Context context) {
        return getPrefrence(context).getString(Constant.SP_SPO2_FLOOR, String.valueOf(Constant.SPO2BOTTOM));
    }

    /**
     * 设置心率上限
     * @param context
     * @param ecg
     */
    public static void setEcgUpper(Context context, String ecg) {
        getPrefrence(context).edit().putString(Constant.SP_ECG_UPPER, TextUtils.isEmpty(ecg) ? String.valueOf(Constant.ECGTOP) : ecg).apply();
    }


    public static String getEcgUpper(Context context) {
        return getPrefrence(context).getString(Constant.SP_ECG_UPPER, String.valueOf(Constant.ECGTOP));
    }

    /**
     * 设置心率下限
     * @param context
     * @param ecg
     */
    public static void setEcgFloor(Context context, String ecg) {
        getPrefrence(context).edit().putString(Constant.SP_ECG_FLOOR, TextUtils.isEmpty(ecg) ? String.valueOf(Constant.ECGBOTTOM) : ecg).apply();
    }


    public static String getEcgFloor(Context context) {
        return getPrefrence(context).getString(Constant.SP_ECG_FLOOR, String.valueOf(Constant.ECGBOTTOM));
    }



    /**
     * 设置呼吸上限
     * @param context
     * @param resp
     */
    public static void setRespUpper(Context context, String resp) {
        getPrefrence(context).edit().putString(Constant.SP_RESP_UPPER, TextUtils.isEmpty(resp) ? String.valueOf(Constant.RESPTOP) : resp).apply();
    }


    public static String getRespUpper(Context context) {
        return getPrefrence(context).getString(Constant.SP_RESP_UPPER, String.valueOf(Constant.RESPTOP));
    }

    /**
     * 设置呼吸下限
     * @param context
     * @param resp
     */
    public static void setRespFloor(Context context, String resp) {
        getPrefrence(context).edit().putString(Constant.SP_RESP_FLOOR, TextUtils.isEmpty(resp) ? String.valueOf(Constant.RESPBOTTOM) : resp).apply();
    }


    public static String getRespFloor(Context context) {
        return getPrefrence(context).getString(Constant.SP_RESP_FLOOR, String.valueOf(Constant.RESPBOTTOM));
    }


    /**
     * 设置温度上限
     * @param context
     * @param temp
     */
    public static void setTempUpper(Context context, String temp) {
        getPrefrence(context).edit().putString(Constant.SP_TEMP_UPPER, TextUtils.isEmpty(temp) ? String.valueOf(Constant.TEMPTOP) : temp).apply();
    }


    public static String getTempUpper(Context context) {
        return getPrefrence(context).getString(Constant.SP_TEMP_UPPER, String.valueOf(Constant.TEMPTOP));
    }

    /**
     * 设置温度下限
     * @param context
     * @param temp
     */
    public static void setTempFloor(Context context, String temp) {
        getPrefrence(context).edit().putString(Constant.SP_TEMP_FLOOR, TextUtils.isEmpty(temp) ? String.valueOf(Constant.TEMPBOTTOM) : temp).apply();
    }


    public static String getTempFloor(Context context) {
        return getPrefrence(context).getString(Constant.SP_TEMP_FLOOR, String.valueOf(Constant.TEMPBOTTOM));
    }

    /**
     * 设置收缩压上限
     * @param context
     * @param sbp
     */
    public static void setSbpUpper(Context context, String sbp) {
        getPrefrence(context).edit().putString(Constant.SP_SBP_UPPER, TextUtils.isEmpty(sbp) ? String.valueOf(Constant.SBPTOP) : sbp).apply();
    }


    public static String getSbpUpper(Context context) {
        return getPrefrence(context).getString(Constant.SP_SBP_UPPER, String.valueOf(Constant.SBPTOP));
    }

    /**
     * 设置收缩压下限
     * @param context
     * @param sbp
     */
    public static void setSbpFloor(Context context, String sbp) {
        getPrefrence(context).edit().putString(Constant.SP_SBP_FLOOR, TextUtils.isEmpty(sbp) ? String.valueOf(Constant.SBPBOTTOM) : sbp).apply();
    }


    public static String getSbpFloor(Context context) {
        return getPrefrence(context).getString(Constant.SP_SBP_FLOOR, String.valueOf(Constant.SBPBOTTOM));
    }


    /**
     * 设置舒张压上限
     * @param context
     * @param dbp
     */
    public static void setDbpUpper(Context context, String dbp) {
        getPrefrence(context).edit().putString(Constant.SP_DBP_UPPER, TextUtils.isEmpty(dbp) ? String.valueOf(Constant.DBPTOP) : dbp).apply();
    }


    public static String getDbpUpper(Context context) {
        return getPrefrence(context).getString(Constant.SP_DBP_UPPER, String.valueOf(Constant.DBPTOP));
    }

    /**
     * 设置舒张压下限
     * @param context
     * @param dbp
     */
    public static void setDbpFloor(Context context, String dbp) {
        getPrefrence(context).edit().putString(Constant.SP_DBP_FLOOR, TextUtils.isEmpty(dbp) ? String.valueOf(Constant.DBPBOTTOM) : dbp).apply();
    }


    public static String getDbpFloor(Context context) {
        return getPrefrence(context).getString(Constant.SP_DBP_FLOOR, String.valueOf(Constant.DBPBOTTOM));
    }


    /**
     * 设置舒张压上限
     * @param context
     * @param map
     */
    public static void setMapUpper(Context context, String map) {
        getPrefrence(context).edit().putString(Constant.SP_MAP_UPPER, TextUtils.isEmpty(map) ? String.valueOf(Constant.MAPTOP) : map).apply();
    }


    public static String getMapUpper(Context context) {
        return getPrefrence(context).getString(Constant.SP_MAP_UPPER, String.valueOf(Constant.MAPTOP));
    }

    /**
     * 设置舒张压下限
     * @param context
     * @param map
     */
    public static void setMapFloor(Context context, String map) {
        getPrefrence(context).edit().putString(Constant.SP_MAP_FLOOR, TextUtils.isEmpty(map) ? String.valueOf(Constant.MAPBOTTOM) : map).apply();
    }


    public static String getMapFloor(Context context) {
        return getPrefrence(context).getString(Constant.SP_MAP_FLOOR, String.valueOf(Constant.MAPBOTTOM));
    }

    /**
     * 设置报警使能开关
     * @param context
     * @param isRing
     */
    public static void setRing(Context context, boolean isRing) {
        getPrefrence(context).edit().putBoolean(Constant.SP_RING, isRing).apply();
    }


    public static boolean getRing(Context context) {
        return getPrefrence(context).getBoolean(Constant.SP_RING, false);
    }
}
