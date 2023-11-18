package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.sunlitweather.bean.Location
import com.driverskr.sunlitweather.ui.base.BaseViewModel

/**
 * @Author: driverSkr
 * @Time: 2023/11/18 10:03
 * @Description: $
 */
class SearchViewModel(private val app: Application) : BaseViewModel(app) {

    val searchResult = MutableLiveData<List<Location>>()

    val curCity = MutableLiveData<Location>()

    val choosedCity = MutableLiveData<Location>()

    val topCity = MutableLiveData<List<String>>()

    val addFinish = MutableLiveData<Boolean>()



}