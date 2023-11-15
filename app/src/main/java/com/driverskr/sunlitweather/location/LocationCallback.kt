package com.driverskr.sunlitweather.location

import com.baidu.location.BDLocation

/**
 * @Author: driverSkr
 * @Time: 2023/11/15 16:16
 * @Description: 定位接口$
 */
interface LocationCallback {

    /**
     * 接收定位
     * @param bdLocation 定位数据
     */
    fun onReceiveLocation(bdLocation: BDLocation)
}