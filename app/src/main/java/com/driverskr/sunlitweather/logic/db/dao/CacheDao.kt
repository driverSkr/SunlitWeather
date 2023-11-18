package com.driverskr.sunlitweather.logic.db.dao

import androidx.room.*
import com.driverskr.sunlitweather.logic.db.entity.CacheEntity

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 11:51
 * @Description: $
 */
@Dao
interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCache(cache: CacheEntity): Long

    @Query("select * from cache where `key`=:key")
    fun getCache(key: String): CacheEntity?

    @Delete
    fun deleteCache(cache: CacheEntity): Int
}