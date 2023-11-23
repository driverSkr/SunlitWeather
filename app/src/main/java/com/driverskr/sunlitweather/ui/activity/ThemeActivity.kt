package com.driverskr.sunlitweather.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.driverskr.lib.extension.toast
import com.driverskr.lib.net.LoadState
import com.driverskr.lib.utils.SpUtil
import com.driverskr.plugin_lib.SkinManager
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.databinding.ActivityThemeBinding
import com.driverskr.sunlitweather.ui.activity.vm.ThemeViewModel
import com.driverskr.sunlitweather.ui.base.BaseVmActivity
import java.io.File

class ThemeActivity: BaseVmActivity<ActivityThemeBinding, ThemeViewModel>() {

    private var curFlag = 0

    override fun bindView() = ActivityThemeBinding.inflate(layoutInflater)

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        setTitle("主题设置")
        curFlag = SpUtil.getInstance(this).themeFlag
        setSelected(curFlag)
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        mBinding.llDefault.setOnClickListener {
            changeSelect(0)
        }
        mBinding.llColorful.setOnClickListener {
            changeSelect(1)
        }

        viewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Error -> {
                    toast("下载失败，请重试")
                    changeSelect(0)
                }
                LoadState.Finish -> {
                    showLoading(false)
                }
                is LoadState.Start -> {
                    showLoading(true, "正在下载...")
                }
            }
        }

        viewModel.downloadStatus.observe(this) {
            toast("设置成功")
            SkinManager.getInstance().loadSkin(SpUtil.getInstance(this).pluginPath, false)
        }
    }

    private fun changeSelect(index: Int) {
        if (curFlag == index) {
            return
        }
        curFlag = index
        setSelected(index)
        SpUtil.getInstance(this).themeFlag = index
        if (index == 1) {
            val path = SpUtil.getInstance(this).pluginPath
            if (path.isEmpty() || !File(path).exists()) {
                viewModel.downPlugin()
            } else {
                SkinManager.getInstance().loadSkin(path, false)
            }
        } else {
            SkinManager.getInstance().loadSkin("", false)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSelected(index: Int) {
        if (index == 1) {
            mBinding.llDefault.background = resources.getDrawable(R.drawable.shape_theme_bg)
            mBinding.llColorful.background =
                resources.getDrawable(R.drawable.shape_theme_bg_selected)
        } else {
            mBinding.llDefault.background =
                resources.getDrawable(R.drawable.shape_theme_bg_selected)
            mBinding.llColorful.background = resources.getDrawable(R.drawable.shape_theme_bg)
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
    }

}