package com.driverskr.sunlitweather.utils

import BaseApplication.Companion.context
import android.preference.PreferenceManager

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 16:15
 * @Description: $
 */
object ContentUtil {
    //应用设置里的文字
    //    public static String SYS_LANG = "zh";
    @JvmField
    var APP_SETTING_UNIT = PreferenceManager.getDefaultSharedPreferences(context).getString("unit","she")

    @JvmField
    @Volatile
    var CITY_CHANGE = false

    @JvmField
    var visibleHeight = 0

    @JvmField
    var screenHeight = 0

    val BASE_URL = "https://fengyun.icu/"

    val TC_APP_ID = "101991873"

    /*private static void getBaseUrl(ApiType apiType) {
        switch (apiType) {
            case SEARCH:
                mBaseUrl = "https://geoapi.qweather.com";//和风天气搜索城市
                break;
            case WEATHER:
                mBaseUrl = "https://devapi.qweather.com";//和风天气API
                break;
            case BING:
                mBaseUrl = "https://cn.bing.com";//必应壁纸
                break;
            default:
                break;
        }
    }*/
}