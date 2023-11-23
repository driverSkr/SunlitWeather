package com.driverskr.sunlitweather.ui.activity

import android.content.Intent
import coil.load
import com.driverskr.lib.utils.SpUtil
import com.driverskr.sunlitweather.databinding.ActivityUserInfoBinding
import com.driverskr.sunlitweather.ui.base.BaseActivity
import com.driverskr.sunlitweather.utils.TencentUtil

class UserInfoActivity: BaseActivity<ActivityUserInfoBinding>() {

    override fun bindView() = ActivityUserInfoBinding.inflate(layoutInflater)

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {}

    /**
     * 初始化布局组件
     */
    override fun initView() {
        setTitle("用户信息")

        mBinding.tvName.text = SpUtil.getInstance(this).account
        mBinding.ivAvatar.load(SpUtil.getInstance(this).avatar)
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        mBinding.btnLogout.setOnClickListener {
            TencentUtil.sTencent.logout(this)
            SpUtil.getInstance(this).logout()
            val intent = Intent()
            intent.putExtra("login", false)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
    }

}