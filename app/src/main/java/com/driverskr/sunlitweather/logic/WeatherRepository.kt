package com.driverskr.sunlitweather.logic

import android.util.Log
import com.driverskr.sunlitweather.logic.network.SunlitNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @Author: driverSkr
 * @Time: 2023/11/20 15:31
 * @Description: $
 */
class WeatherRepository private constructor(private val sunlitNetwork: SunlitNetwork){

    suspend fun searchCity(location: String, mode: String) = withContext(Dispatchers.IO) {
        val response = sunlitNetwork.fetchSearchCity(location, mode)
        Log.d("driverSkr","WeatherRepository : ${response.toString()}")
        response
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(network: SunlitNetwork): WeatherRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: WeatherRepository(network)
        }
    }
}