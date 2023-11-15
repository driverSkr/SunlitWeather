package com.driverskr.sunlitweather.ui.base

import android.content.Intent
import androidx.viewbinding.ViewBinding

/**
 * @Author: driverSkr
 * @Time: 2023/11/15 10:10
 * @Description: Created by shiju.wang on 2018/2/10.$
 */
interface CreateInit<T : ViewBinding?> {

    fun bindView() : T

    /**
     * 接收数据
     * @param intent
     */
    fun prepareData(intent: Intent?)

    /**
     * 初始化布局组件
     */
    fun initView()

    /**
     * 处理事件
     */
    fun initEvent()

    /**
     * 初始化数据
     */
    fun initData()
}