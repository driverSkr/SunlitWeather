package com.driverskr.sunlitweather.utils


import com.driverskr.lib.BaseApplication
import com.tencent.tauth.Tencent

/**
 * @Author: driverSkr
 * @Time: 2023/11/21 11:17
 * @Description: $
 */
object TencentUtil {
    val sTencent by lazy {
        Tencent.createInstance(
            ContentUtil.TC_APP_ID,
            BaseApplication.context,
            "${BaseApplication.context}.fileprovider"
        )
    }
}