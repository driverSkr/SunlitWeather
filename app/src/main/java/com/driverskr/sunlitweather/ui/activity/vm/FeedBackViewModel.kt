package com.driverskr.sunlitweather.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.driverskr.lib.net.HttpUtils
import com.driverskr.sunlitweather.ui.base.BaseViewModel
import com.driverskr.sunlitweather.utils.ContentUtil

/**
 * @Author: driverSkr
 * @Time: 2023/11/23 10:29
 * @Description: $
 */
class FeedBackViewModel(val app: Application) : BaseViewModel(app) {

    val feedBackResult = MutableLiveData<String>()

    fun sendFeedBack(content: String) {
        val param = HashMap<String, Any>()
        param["content"] = content

        val url = ContentUtil.BASE_URL + "api/feedback"

        launch {
            val result = HttpUtils.post<String>(url, param)
            result?.let {
                feedBackResult.postValue(it)
            }
        }
    }
}