package com.driverskr.sunlitweather.ui.activity

import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.driverskr.lib.extension.toast
import com.driverskr.lib.utils.LogUtil
import com.driverskr.sunlitweather.databinding.ActivityLoginBinding
import com.driverskr.sunlitweather.ui.activity.vm.LoginViewModel
import com.driverskr.sunlitweather.ui.base.BaseVmActivity
import com.driverskr.sunlitweather.utils.TencentUtil
import com.tencent.connect.common.Constants
import com.tencent.tauth.DefaultUiListener
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject

open class LoginActivity: BaseVmActivity<ActivityLoginBinding, LoginViewModel>() {

    private var uiListener: IUiListener = object : BaseUiListener() {
        override fun doComplete(values: JSONObject) {
            LogUtil.e("AuthorSwitch_SDK:" + SystemClock.elapsedRealtime())
            try {
                val token = values.getString(Constants.PARAM_ACCESS_TOKEN)
                val expires = values.getString(Constants.PARAM_EXPIRES_IN)
                val openId = values.getString(Constants.PARAM_OPEN_ID)
                if (token.isNotEmpty() && expires.isNotEmpty() && openId.isNotEmpty()) {
                    TencentUtil.sTencent.setAccessToken(token, expires)
                    TencentUtil.sTencent.openId = openId
                }
            } catch (e: Exception) {}
            viewModel.getUserInfo()
        }
    }

    override fun bindView() = ActivityLoginBinding.inflate(layoutInflater)

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
        Tencent.setIsPermissionGranted(true, Build.MODEL)
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        setTitle("登录")
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        mBinding.btnLogin.setOnClickListener {
            if (!TencentUtil.sTencent.isSessionValid) {
                // 判断会话是否有效
                when (TencentUtil.sTencent.login(this, "all",uiListener)) {
                    0 -> LogUtil.d("正常登录")
                    1 -> LogUtil.d("开始登录")
                    -1 -> toast("QQ登录异常")
                    2 -> LogUtil.d("使用H5登陆或显示下载页面")
                    else -> toast("QQ登录出错")
                }
            } else {
                viewModel.getUserInfo()
            }
        }

        viewModel.checkLogin().observe(this) {
            if (it) viewModel.getUserInfo()
        }

        viewModel.userInfo.observe(this) {
            if (it.first) {
                val intent = Intent()
                intent.putExtra("login", true)
                intent.putExtra("user_info", it.second)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                toast("获取用户信息失败")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 腾讯QQ回调，这里的iu仍然是相关的UIListener
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener)
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, uiListener)
            }
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        //To do sth.
    }
}

abstract class BaseUiListener : DefaultUiListener() {
    override fun onComplete(response: Any?) {
        if (response == null) {
            LogUtil.e("返回为空，登录失败")
            return
        }
        val jsonResponse = response as JSONObject
        if (jsonResponse.length() == 0) {
            LogUtil.e("返回为空，登录失败")
            return
        }
        doComplete(response)
    }
    abstract fun doComplete(values: JSONObject)

    override fun onError(e: UiError) {
        Log.e("fund", "onError: ${e.errorDetail}")
    }

    override fun onCancel() {
        LogUtil.e("取消登录")
    }
}