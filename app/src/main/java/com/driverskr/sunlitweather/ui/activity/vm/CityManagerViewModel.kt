package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.sunlitweather.logic.AppRepository
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import com.driverskr.sunlitweather.ui.base.BaseViewModel

/**
 * @Author: driverSkr
 * @Time: 2023/11/23 9:19
 * @Description: $
 */
class CityManagerViewModel(app: Application): BaseViewModel(app) {

    val cities = MutableLiveData<List<CityEntity>>()

    fun getCities() {
        launch {
            val results = AppRepository.getInstance().getCities()
            cities.postValue(results)
        }
    }

    fun removeCity(cityId: String) {
        launch {
            AppRepository.getInstance().removeCity(cityId)
        }
    }

    fun updateCities(it: List<CityEntity>) {
        launch {
            AppRepository.getInstance().removeNotLocalCity()
            it.forEach {
                AppRepository.getInstance().addCity(it)
            }
        }
    }
}