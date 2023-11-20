package com.driverskr.sunlitweather

import android.app.Application
import com.baidu.location.LocationClient

/**
 * @Author: driverSkr
 * @Time: 2023/11/15 16:02
 * @Description: 我的application$
 */
class MyApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        //使用定位需要同意隐私合规政策
        LocationClient.setAgreePrivacy(true)
    }
}