package com.driverskr.sunlitweather.ui.activity

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import com.driverskr.lib.extension.toast
import com.driverskr.lib.utils.CommonUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.databinding.ActivityAboutBinding
import com.driverskr.sunlitweather.ui.base.BaseActivity

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override fun bindView() = ActivityAboutBinding.inflate(layoutInflater)

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
        setTitle(getString(R.string.about))
        mBinding.tvVersionNum.text = CommonUtil.getVersionName(this)
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        mBinding.rlDisclaimer.setOnClickListener {
            toast("本应用仅用于交流学习，天气数据来源于和风天气(https://dev.qweather.com/)，数据准确性仅供参考，本App不承担任何法律责任")
        }
        mBinding.tvWebSit.apply {
            setOnClickListener {
                val url = mBinding.tvWebSit.text.toString()
                val uri = Uri.parse(url)
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = uri
                startActivity(intent)
            }
            paint.apply {
                flags = Paint.UNDERLINE_TEXT_FLAG
                isAntiAlias = true
            }
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
    }
}