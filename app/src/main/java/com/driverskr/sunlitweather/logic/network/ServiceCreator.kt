package com.driverskr.sunlitweather.logic.network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Author: driverSkr
 * @Time: 2023/11/18 10:22
 * @Description: $
 */
object ServiceCreator {

    private fun <T> create(serviceClass: Class<T>, apiType: ApiType): T {
        val baseUrl = getBaseUrl(apiType)
        Log.d("driverSkr", "ServiceCreator : $baseUrl")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }


    fun <T : Any> createService(serviceClass: Class<T>, apiType: ApiType) = create(serviceClass, apiType)

    private fun getBaseUrl(apiType: ApiType): String {
        return when (apiType) {
            ApiType.SEARCH -> "https://geoapi.qweather.com"  //和风天气搜索城市
            ApiType.WEATHER -> "https://devapi.qweather.com" //和风天气API
            ApiType.BING -> "https://cn.bing.com"    //必应壁纸
        }
    }
}