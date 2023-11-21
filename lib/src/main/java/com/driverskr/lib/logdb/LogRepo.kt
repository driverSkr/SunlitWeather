package com.driverskr.lib.logdb


import com.driverskr.lib.BaseApplication
import com.driverskr.lib.logdb.dao.LogDao
import com.driverskr.lib.logdb.entity.LogEntity


class LogRepo {


    private val logDao: LogDao = LogDatabase.getInstance(BaseApplication.context).logDao()

    fun addLog(content: String) {
        logDao.addLog(LogEntity(content))
    }

    companion object {
        @Volatile
        private var instance: LogRepo? = null

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: LogRepo()
                        .also { instance = it }
            }
    }
}