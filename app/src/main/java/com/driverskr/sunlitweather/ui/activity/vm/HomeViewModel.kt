package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.sunlitweather.BuildConfig
import com.driverskr.sunlitweather.bean.VersionBean
import com.driverskr.sunlitweather.db.AppRepository
import com.driverskr.sunlitweather.db.entity.CityEntity
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.ContentUtil

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 10:41
 * @Description: 对应HomeActivity$
 */
class HomeViewModel(val app: Application): BaseViewModel(app) {

    val mCities = MutableLiveData<List<CityEntity>>()

    val mCurCondCode = MutableLiveData<String>()

    val newVersion = MutableLiveData<VersionBean>()

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
            param["key"] = BuildConfig.HeFengKey
            param["build_type"] = BuildConfig.BUILD_TYPE

            val result = HttpUtils.post<VersionBean>(url, param)

            result?.let {
                newVersion.postValue(it)
            }
        }
    }
}