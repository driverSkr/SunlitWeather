package com.driverskr.lib.utils

import com.driverskr.lib.R
import me.wsj.lib.utils.DateUtil.getNowHour

/**
 * @Author: driverSkr
 * @Time: 2023/11/17 16:54
 * @Description: $
 */
object IconUtils {

    /**
     * 获取白天背景
     */
    val defaultBg: Int
        get() = if (isDay()) R.drawable.bg_0_d else R.drawable.bg_0_n

    @JvmStatic
    fun isDay(): Boolean {
        val now = getNowHour()
        return now in 7..18
    }
}