package com.driverskr.sunlitweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Author: driverSkr
 * @Time: 2023/11/18 10:22
 * @Description: $
 */
object ServiceCreator {

    //和风天气搜索城市
    const val SEARCH_URL = "https://geoapi.qweather.com"
    //和风天气API
    const val WEATHER_URL = "https://devapi.qweather.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(SEARCH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}