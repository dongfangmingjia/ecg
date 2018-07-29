package com.ljfth.ecgviewlib;

public class Constant {
    // 姓名
    public static final String SP_NAME = "sp_name";
    // 年龄
    public static final String SP_AGE = "sp_age";
    // 性别
    public static final String SP_SEX = "sp_sex";
    // 住院号
    public static final String SP_H_NUM = "sp_h_num";
    // 床号
    public static final String SP_BED_NUM = "sp_bed_num";
    // 起搏
    public static final String SP_PACE_MAKING = "sp_pace_making";
    // 血氧
    public static final String SP_SPO2_UPPER = "sp_spo2_upper";
    public static final String SP_SPO2_FLOOR = "sp_spo2_floor";
    // 心率
    public static final String SP_ECG_UPPER = "sp_ecg_upper";
    public static final String SP_ECG_FLOOR = "sp_ecg_floor";
    // 呼吸
    public static final String SP_RESP_UPPER = "sp_resp_upper";
    public static final String SP_RESP_FLOOR = "sp_resp_floor";
    // 温度
    public static final String SP_TEMP_UPPER = "sp_temp_upper";
    public static final String SP_TEMP_FLOOR = "sp_temp_floor";

    // 收缩压
    public static final String SP_SBP_UPPER = "sp_sbp_upper";
    public static final String SP_SBP_FLOOR = "sp_sbp_floor";
    // 舒张压
    public static final String SP_DBP_UPPER = "sp_Dbp_upper";
    public static final String SP_DBP_FLOOR = "sp_Dbp_floor";
    // 平均压
    public static final String SP_MAP_UPPER = "sp_map_upper";
    public static final String SP_MAP_FLOOR = "sp_map_floor";
    // 报警使能
    public static final String SP_RING = "sp_ring";



    public static final int SPO2TOP = 100;
    public static final int SPO2BOTTOM = 80;

    public static final int ECGTOP = 130;
    public static final int ECGBOTTOM = 50;

    public static final int RESPTOP = 30;
    public static final int RESPBOTTOM = 5;

    public static final int TEMPTOP = 38;
    public static final int TEMPBOTTOM = 35;

    public static final int SBPTOP = 150;
    public static final int SBPBOTTOM = 50;

    public static final int DBPTOP = 150;
    public static final int DBPBOTTOM = 50;

    public static final int MAPTOP = 40;
    public static final int MAPBOTTOM = 30;


    /**
     * 性别常量（男）
     */
    public static final int SEX_MEN = 0x001;
    /**
     * 性别常量（女）
     */
    public static final int SEX_WOMEN = 0x002;
    /**
     * 起搏常量（是）
     */
    public static final int PACE_YES = 0x003;
    /**
     * 起搏常量（否）
     */
    public static final int PACE_NO = 0x004;

    /**搜索*/
    public static final int TYPE_SEARCH = 0;
    /**连接*/
    public static final int TYPE_CONNECT = 1;
    /**检测*/
    public static final int TYPE_DETECTION = 2;
    /**断开*/
    public static final int TYPE_BREAK = 3;
    /**重置*/
    public static final int TYPE_RESTORATION = 4;


    public static final String DEVICES_TYPE_BODYSTMSPO2 = "0";    //血氧
    public static final String DEVICES_TYPE_BODYSTMECG = "1";     //心电
    public static final String DEVICES_TYPE_BODYSTMNIBP_PWV = "2";    //连续血压
    public static final String DEVICES_TYPE_BODYSTMRESP = "3";        //呼吸
    public static final String DEVICES_TYPE_BODYSTMTEMP1 = "4";       //温度
    public static final String DEVICES_TYPE_BODYSTMTEMP2 = "5";       //温度
    public static final String DEVICES_TYPE_BODYSTMNIBP_VAL = "6";    //无创血压

}
