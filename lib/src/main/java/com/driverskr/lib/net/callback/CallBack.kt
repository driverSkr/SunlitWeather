package com.driverskr.lib.net.callback

import java.io.IOException

interface CallBack<T> {

    @Throws(IOException::class)
    fun onNext(responseBody: String?)

    fun onSuccess(result: T, code: String?, msg: String?)
}