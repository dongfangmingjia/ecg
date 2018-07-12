package com.ljfth.ecgviewlib;

import android.content.Context;
import android.content.SharedPreferences;

public class EcgSharedPrefrence {

    public static final String FILE_NAME = "fill_ecg";

    private static SharedPreferences getPrefrence(Context mContext) {
        return mContext.getSharedPreferences(FILE_NAME, 0);
    }


    public static void setName(Context context, String name) {
        getPrefrence(context).edit().putString(Constant.SP_NAME, name).apply();
    }


    public static String getName(Context context) {
        return getPrefrence(context).getString(Constant.SP_NAME, "");
    }


    public static void setAge(Context context, String age) {
        getPrefrence(context).edit().putString(Constant.SP_AGE, age).apply();
    }


    public static String getAge(Context context) {
        return getPrefrence(context).getString(Constant.SP_AGE, "");
    }

    public static void setSex(Context context, int sex) {
        getPrefrence(context).edit().putInt(Constant.SP_SEX, sex).apply();
    }


    public static int getSex(Context context) {
        return getPrefrence(context).getInt(Constant.SP_SEX, PatientInfoActivity.SEX_MEN);
    }

    public static void setHospitalNum(Context context, String hospitalNum) {
        getPrefrence(context).edit().putString(Constant.SP_H_NUM, hospitalNum).apply();
    }


    public static String getHospitalNum(Context context) {
        return getPrefrence(context).getString(Constant.SP_H_NUM, "");
    }


    public static void setBedNum(Context context, String bedNum) {
        getPrefrence(context).edit().putString(Constant.SP_BED_NUM, bedNum).apply();
    }


    public static String getBedNum(Context context) {
        return getPrefrence(context).getString(Constant.SP_BED_NUM, "");
    }

    public static void setPaceMaking(Context context, boolean isMaking) {
        getPrefrence(context).edit().putBoolean(Constant.SP_PACE_MAKING, isMaking).apply();
    }


    public static boolean getPaceMaking(Context context) {
        return getPrefrence(context).getBoolean(Constant.SP_PACE_MAKING, false);
    }
}
