package com.driverskr.sunlitweather.logic.network

import com.driverskr.sunlitweather.logic.network.api.WeatherService

/**
 * @Author: driverSkr
 * @Time: 2023/11/20 15:13
 * @Description: $
 */
class SunlitNetwork {

    var weatherService = ServiceCreator.createService(WeatherService::class.java,ApiType.SEARCH)
        private set


}