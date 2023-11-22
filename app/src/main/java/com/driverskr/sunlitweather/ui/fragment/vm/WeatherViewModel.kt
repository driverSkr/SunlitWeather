package com.driverskr.sunlitweather.ui.fragment.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.lib.net.HttpUtils
import com.driverskr.sunlitweather.bean.*
import com.driverskr.sunlitweather.logic.AppRepository
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.Constant
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.isNotEmpty
import kotlin.collections.set

/**
 * @Author: driverSkr
 * @Time: 2023/11/21 11:54
 * @Description: $
 */

const val CACHE_WEATHER_NOW = "now_"
const val CACHE_LIFE_INDICATOR = "now_life_indicator_"

class WeatherViewModel(val app: Application) : BaseViewModel(app) {

    val weatherNow = MutableLiveData<Now>()
    val warnings = MutableLiveData<List<Warning>>()
    val airNow = MutableLiveData<Air>()
    val forecast = MutableLiveData<List<Daily>>()
    val hourly = MutableLiveData<List<Hourly>>()
    val lifeIndicator = MutableLiveData<LifeIndicator>()

    fun loadCache(cityId: String) {
        launchSilent {
            var cache = AppRepository.getInstance().getCache<Now>(CACHE_WEATHER_NOW + cityId)
            cache?.let {
                weatherNow.postValue(it)
            }
        }
    }

    fun loadData(cityId: String) {
        val param = HashMap<String, Any>()
        param["location"] = cityId
        param["key"] = Constant.HEFENG_KEY

        // 实时天气
        launch {
            val url = "https://devapi.qweather.com/v7/weather/now"
            val result = HttpUtils.get<WeatherNow>(url, param)
            result?.let {
                weatherNow.postValue(it.now)
                AppRepository.getInstance().saveCache(CACHE_WEATHER_NOW + cityId, it.now)
            }
        }

        // 预警
        launch {
            val url = "https://devapi.qweather.com/v7/warning/now"
            val result = HttpUtils.get<WarningBean>(url, param)
            result?.let {
                if (it.warning.isNotEmpty()) {
                    warnings.postValue(result.warning)
                }
            }
        }

        // 实时空气
        launch {
            val url = "https://devapi.qweather.com/v7/air/now"
            val result = HttpUtils.get<AirNow>(url, param)
            result?.let {
                airNow.postValue(it.now)
            }
        }

        // 7天 天气预报
        launch {
            val url = "https://devapi.qweather.com/v7/weather/7d"
            val result = HttpUtils.get<ForestBean>(url, param, useCache = true)
            result?.let {
                forecast.postValue(it.daily)
            }
        }

        // 逐小时天气预报
        launch {
            val url = "https://devapi.qweather.com/v7/weather/24h"
            val result = HttpUtils.get<WeatherHourly>(url, param)
            result?.let {
                hourly.postValue(it.hourly)
            }
        }

        // 天气生活指数(使用缓存 3h)
        launch {
            val lifeIndicatorCacheKey = CACHE_LIFE_INDICATOR + cityId
            var cache = AppRepository.getInstance().getCache<LifeIndicator>(lifeIndicatorCacheKey)
            cache?.let {
                lifeIndicator.postValue(it)
                return@launch
            }
            val url = "https://devapi.qweather.com/v7/indices/1d?type=1,2,3,5,9,14"
            HttpUtils.get<LifeIndicator>(url, param)?.let {
                AppRepository.getInstance().saveCache(lifeIndicatorCacheKey, it, 3 * 60 * 60)
                lifeIndicator.postValue(it)
            }
        }
    }
}