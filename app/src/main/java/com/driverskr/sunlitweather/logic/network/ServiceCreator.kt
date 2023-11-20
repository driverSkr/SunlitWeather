package com.driverskr.sunlitweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Author: driverSkr
 * @Time: 2023/11/18 10:22
 * @Description: $
 */
object ServiceCreator {

    //API访问地址
    private var mBaseUrl: String? = null

    private val retrofit = mBaseUrl?.let {
        Retrofit.Builder()
        .baseUrl(it)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    }

    private fun <T> create(serviceClass: Class<T>): T? = retrofit?.create(serviceClass)


    fun <T : Any> createService(serviceClass: Class<T>, apiType: ApiType): T? {
        getBaseUrl(apiType)
        return create(serviceClass)
    }

    private fun getBaseUrl(apiType: ApiType) {
        mBaseUrl = when (apiType) {
            ApiType.SEARCH -> "https://geoapi.qweather.com"  //和风天气搜索城市
            ApiType.WEATHER -> "https://devapi.qweather.com" //和风天气API
            ApiType.BING -> "https://cn.bing.com"    //必应壁纸
        }
    }
}