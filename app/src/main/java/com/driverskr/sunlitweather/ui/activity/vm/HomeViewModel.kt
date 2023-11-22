package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.driverskr.lib.net.HttpUtils
import com.driverskr.sunlitweather.bean.SearchCityResponse
import com.driverskr.sunlitweather.bean.TempUnit
import com.driverskr.sunlitweather.bean.VersionBean
import com.driverskr.sunlitweather.logic.AppRepository
import com.driverskr.sunlitweather.logic.WeatherRepository
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import com.driverskr.sunlitweather.logic.network.SunlitNetwork
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.Constant
import com.driverskr.sunlitweather.utils.ContentUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 10:41
 * @Description: 对应HomeActivity$
 */
class HomeViewModel(val app: Application): BaseViewModel(app) {

    val mCities = MutableLiveData<List<CityEntity>>()

    val mCurCondCode = MutableLiveData<String>()

    val newVersion = MutableLiveData<VersionBean>()

    val searchCity = MutableLiveData<SearchCityResponse?>()

    fun setCondCode(condCode: String) {
        mCurCondCode.postValue(condCode)
    }

    fun getCities() {
        launchSilent {
            val cities = AppRepository.getInstance().getCities()
            mCities.postValue(cities)
        }
    }

    fun checkVersion() {
        launchSilent {
            val url = ContentUtil.BASE_URL + "api/check_version2"
            val param = HashMap<String, Any>()
//            param["app_code"] = CommonUtil.getVersionCode(app)
            param["key"] = Constant.HEFENG_KEY
            param["build_type"] = Constant.BAIDU_KEY

            val result = HttpUtils.post<VersionBean>(url, param)

            result?.let {
                newVersion.postValue(it)
            }
        }
    }


    fun changeUnit(unit: TempUnit) {
//        ContentUtil.UNIT_CHANGE = true
        ContentUtil.APP_SETTING_UNIT = unit.tag

        PreferenceManager.getDefaultSharedPreferences(app).edit().apply {
            putString("unit", unit.tag)
            apply()
        }
    }

    fun getSearchCity(location: String,isExact: Boolean) {
        viewModelScope.launch(Dispatchers.Default){
            val response = WeatherRepository.getInstance(SunlitNetwork.getInstance()).searchCity(location,if (isExact) "exact" else "fuzzy")
            Log.d("boge","HomeViewModel : ${response.toString()}")
            searchCity.postValue(response)
        }
    }
}