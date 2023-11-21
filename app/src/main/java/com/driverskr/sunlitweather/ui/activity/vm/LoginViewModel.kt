package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.ContentUtil
import com.driverskr.sunlitweather.utils.TencentUtil

/**
 * @Author: driverSkr
 * @Time: 2023/11/21 11:15
 * @Description: $
 */
class LoginViewModel(private val app: Application): BaseViewModel(app) {

    /**
     * 检查用户登录状态
     */
    /*fun checkLogin(): MutableLiveData<Boolean> {
        val loginStatusBarManager = MutableLiveData<Boolean>()

        if (TencentUtil.sTencent.accessToken.isNullOrEmpty()) {
            TencentUtil.sTencent.initSessionCache(TencentUtil.sTencent.loadSession(ContentUtil.TC_APP_ID))
        }
        //TencentUtil.sTencent.checkLogin(object : BaseU)
    }*/
}