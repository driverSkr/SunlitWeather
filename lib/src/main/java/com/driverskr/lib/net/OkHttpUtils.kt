package com.driverskr.lib.net

import com.driverskr.lib.BaseApplication
import com.driverskr.lib.BuildConfig
import com.driverskr.lib.net.interceptor.LogInterceptor
import com.driverskr.lib.net.interceptor.NetCacheInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

object OkHttpUtils {

    private const val DEFAULT_TIMEOUT = 15L

    // log interceptor
    private val mLoggingInterceptor: Interceptor by lazy { LogInterceptor() }
    private val mCustomerInterceptor: Interceptor by lazy { MyInterceptor() }

    private var cer: String? = null

    private val mClient: OkHttpClient by lazy { generateClient(cer) }

    fun getClient(cer: String? = ""): OkHttpClient {
        OkHttpUtils.cer = cer
        return mClient
    }

    private fun generateClient(cer: String?): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            addNetworkInterceptor(NetCacheInterceptor())
            // 设置缓存
            cache(Cache(File(BaseApplication.context.externalCacheDir, "okhttp-cache"), 5 * 1024 * 1024))
            dns(HttpDns())
        }

        builder.addInterceptor(mCustomerInterceptor)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(mLoggingInterceptor)
//            builder.eventListenerFactory(OkHttpEventListener.FACTORY)
//            builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
        } else {
            builder.proxy(Proxy.NO_PROXY)
        }
        if (cer.isNullOrEmpty()) {
//          // 信任所有证书
            SSLCerUtils.setTrustAllCertificate(builder)
        } else {
            // 自签名证书
            SSLCerUtils.setCertificate(BaseApplication.context, builder, cer)
        }

        return builder.build()
    }
}