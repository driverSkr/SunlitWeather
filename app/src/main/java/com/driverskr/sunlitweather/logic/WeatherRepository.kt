package com.driverskr.sunlitweather.logic

import com.driverskr.sunlitweather.logic.network.SunlitNetwork

/**
 * @Author: driverSkr
 * @Time: 2023/11/20 15:31
 * @Description: $
 */
class WeatherRepository private constructor(private val sunlitNetwork: SunlitNetwork){



    companion object {
        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(network: SunlitNetwork): WeatherRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: WeatherRepository(network)
        }
    }
}