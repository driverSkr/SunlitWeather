package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.lib.net.HttpUtils
import com.driverskr.lib.utils.LogUtil
import com.driverskr.lib.utils.SpUtil
import com.driverskr.sunlitweather.bean.UserInfoBean
import com.driverskr.sunlitweather.ui.activity.BaseUiListener
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.ContentUtil
import com.driverskr.sunlitweather.utils.TencentUtil
import com.google.gson.Gson
import com.tencent.connect.UserInfo
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import org.json.JSONObject

class LoginViewModel(private val app: Application) : BaseViewModel(app) {

    /**
     * 检查用户登录状态
     */
    fun checkLogin(): MutableLiveData<Boolean> {
        val loginStatus = MutableLiveData<Boolean>()

        if (TencentUtil.sTencent.accessToken.isNullOrEmpty()) {
            TencentUtil.sTencent.initSessionCache(TencentUtil.sTencent.loadSession(ContentUtil.TC_APP_ID))
        }
        TencentUtil.sTencent.checkLogin(object : BaseUiListener() {
            override fun doComplete(jsonResp: JSONObject) {
                if (jsonResp.optInt("ret", -1) == 0) {
                    val jsonObject: JSONObject =
                        TencentUtil.sTencent.loadSession(ContentUtil.TC_APP_ID)
                    TencentUtil.sTencent.initSessionCache(jsonObject)
                    if (jsonObject == null) {
                        loginStatus.postValue(false)
                        LogUtil.e("jsonObject is null " + "登录失败")
                    } else {
                        loginStatus.postValue(true)
                        LogUtil.e(jsonObject.toString() + "登录成功")
                    }
                } else {
                    loginStatus.postValue(false)
                    LogUtil.e("token过期，请调用登录接口拉起手Q授权登录 " + "登录失败")
                }
            }
        })
        return loginStatus
    }

    val userInfo = MutableLiveData<Pair<Boolean, UserInfoBean?>>()

    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        if (TencentUtil.sTencent.isSessionValid) {
            val listener: IUiListener = object : BaseUiListener() {
                override fun doComplete(jsonObject: JSONObject) {
                    if (jsonObject.has("nickname")) {
                        val userInfoBean =
                            Gson().fromJson(jsonObject.toString(), UserInfoBean::class.java)
                        userInfoBean.token = TencentUtil.sTencent.qqToken.accessToken
                        userInfoBean.open_id = TencentUtil.sTencent.qqToken.openId

                        SpUtil.getInstance(app).account = userInfoBean.nickname
                        SpUtil.getInstance(app).avatar = userInfoBean.figureurl_qq
                        SpUtil.getInstance(app).setToken(userInfoBean.token)

                        userInfo.postValue(Pair(true, userInfoBean))
                    } else {
                        userInfo.postValue(Pair(false, null))
                        LogUtil.e("获取用户信息失败")
                    }
                }

                override fun onError(e: UiError) {
                    super.onError(e)
                    userInfo.postValue(Pair(false, null))
                }
            }

            UserInfo(app, TencentUtil.sTencent.qqToken).getUserInfo(listener)
        }
    }

    fun register(userInfo: UserInfoBean) {
        val url = ContentUtil.BASE_URL + "api/register"
        launch {
            HttpUtils.post<String>(url, userInfo)
        }
    }
}