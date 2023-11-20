package com.driverskr.sunlitweather.logic.network.api

import com.driverskr.sunlitweather.bean.SearchCityResponse
import com.driverskr.sunlitweather.utils.Constant.HEFENG_KEY
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Author: driverSkr
 * @Time: 2023/11/18 10:20
 * @Description: $
 */
interface WeatherService {

    /**
     * 搜索城市  模糊搜索，国内范围 返回10条数据
     *
     * @param location 城市名
     * @param mode     exact 精准搜索  fuzzy 模糊搜索
     * @return NewSearchCityResponse 搜索城市数据返回
     */
    @GET("/v2/city/lookup?key=$HEFENG_KEY&range=cn")
    suspend fun searchCity(@Query("location") location: String,
                           @Query("mode") mode: String): SearchCityResponse

}