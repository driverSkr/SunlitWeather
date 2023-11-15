package com.driverskr.sunlitweather.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.ViewPropertyAnimator
import androidx.lifecycle.lifecycleScope
import com.driverskr.lib.service.WidgetService
import com.driverskr.sunlitweather.databinding.ActivitySplashBinding
import com.driverskr.sunlitweather.ui.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    lateinit var animate: ViewPropertyAnimator

    @SuppressLint("ObsoleteSdkInt")
    private fun startIntent() {
        lifecycleScope.launch {
            var citySize: Int

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this@SplashActivity, WidgetService::class.java))
            } else {
                startService(Intent(this@SplashActivity, WidgetService::class.java))
            }

            withContext(Dispatchers.IO) {

            }
        }
    }

    override fun bindView(): ActivitySplashBinding {
        TODO("Not yet implemented")
    }

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
        TODO("Not yet implemented")
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        TODO("Not yet implemented")
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        TODO("Not yet implemented")
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        TODO("Not yet implemented")
    }

}