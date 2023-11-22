package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.lib.net.HttpUtils
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.bean.CityBean
import com.driverskr.sunlitweather.bean.Location
import com.driverskr.sunlitweather.bean.SearchCity
import com.driverskr.sunlitweather.logic.AppRepository
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.Constant
import com.driverskr.sunlitweather.utils.ContentUtil
import java.util.ArrayList

/**
 * @Author: driverSkr
 * @Time: 2023/11/18 10:03
 * @Description: $
 */
const val LAST_LOCATION = "LAST_LOCATION"

class SearchViewModel(private val app: Application) : BaseViewModel(app) {

    val searchResult = MutableLiveData<List<Location>>()

    val curCity = MutableLiveData<Location>()

    val choosedCity = MutableLiveData<Location>()

    val topCity = MutableLiveData<List<String>>()

    val addFinish = MutableLiveData<Boolean>()

    val curLocation = MutableLiveData<String>()

    val cacheLocation = MutableLiveData<String>()

    /**
     * 搜索城市
     */
    fun searchCity(keywords: String) {
        launchSilent {
            val url = "https://geoapi.qweather.com/v2/city/lookup"
            val param = HashMap<String, Any>()
            param["location"] = keywords
            param["key"] = Constant.HEFENG_KEY

            val result = HttpUtils.get<SearchCity>(url, param)
            result?.let {
                searchResult.postValue(it.location)
            }
        }
    }

    /**
     * 获取热门城市
     */
    fun getTopCity() {
        launch {
            val stringArray = app.resources.getStringArray(R.array.top_city)
            val cityList = stringArray.toList() as ArrayList<String>
            topCity.postValue(cityList)
        }
    }

    /**
     * 添加城市
     */
    fun addCity(it: CityBean, isLocal: Boolean = false, fromSplash: Boolean = false) {
        launch {
            // todo 排序
            if (isLocal) {
                AppRepository.getInstance().removeLocal(it.cityId)
//                AppRepo.getInstance().removeCity(it.cityId)
            }
            AppRepository.getInstance().addCity(CityEntity(it.cityId, it.cityName, isLocal))
            ContentUtil.CITY_CHANGE = true
            if (!isLocal) {
                addFinish.postValue(true)
            } else if (fromSplash) {
                addFinish.postValue(true)
            }
        }
    }

    /**
     * 获取城市数据
     */
    fun getCityInfo(cityName: String, save: Boolean = false) {
        launch {
            if (save) {
                // 缓存定位城市
                AppRepository.getInstance().saveCache(LAST_LOCATION, cityName)
            }

            val url = "https://geoapi.qweather.com/v2/city/lookup"
            val param = HashMap<String, Any>()
            param["location"] = cityName
            param["key"] = Constant.HEFENG_KEY

            val result = HttpUtils.get<SearchCity>(url, param)
            result?.let {
                if (save) {
                    curCity.postValue(it.location[0])
                } else {
                    choosedCity.postValue(it.location[0])
                }
            }
        }
    }

    fun getCacheLocation() {
        launch {
            (AppRepository.getInstance().getCache<String>(LAST_LOCATION))?.let {
                cacheLocation.postValue(it)
            }
        }
    }

}