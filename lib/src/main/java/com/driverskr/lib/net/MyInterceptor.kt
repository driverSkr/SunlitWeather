package com.driverskr.lib.net

import com.driverskr.lib.BaseApplication.Companion.context
import com.driverskr.lib.utils.CommonUtil
import com.driverskr.lib.utils.DeviceUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by wangsj on 2022/1/12.
 *
 * @author wangsj
 */
class MyInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().run {
            addHeader("Pkg", context.packageName)
            addHeader("App-Code", CommonUtil.getVersionCode(context).toString())
            addHeader("Device-Brand", DeviceUtil.getDeviceBrand())
            addHeader("System-Model", DeviceUtil.getSystemModel())
            addHeader("System-Version", DeviceUtil.getSystemVersion())
            build()
        }
        return chain.proceed(request)
    }

}