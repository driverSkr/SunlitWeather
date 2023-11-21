package com.driverskr.lib

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 16:07
 * @Description: $
 */
open class BaseApplication: Application() {

    companion object {
        var context: Context by Delegates.notNull()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}