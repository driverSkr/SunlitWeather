package com.driverskr.lib.net

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 10:45
 * @Description: $
 */
sealed class LoadState {
    /**
     * 加载中
     */
    class Start(var tip: String = "正在加载中..."): LoadState()

    /**
     * 失败
     */
    class Error(val msg: String): LoadState()

    /**
     * 完成
     */
    object Finish: LoadState()
}