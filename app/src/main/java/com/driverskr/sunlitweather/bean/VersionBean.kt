package com.driverskr.sunlitweather.bean

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 11:47
 * @Description: $
 */
data class VersionBean(val describe: String,
                       val isForce: Boolean,
                       val urlFull: String,
                       val versionCode: Int,
                       val versionName: String)
