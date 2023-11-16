package com.driverskr.sunlitweather.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.driverskr.sunlitweather.db.dao.CacheDao
import com.driverskr.sunlitweather.db.dao.CityDao
import com.driverskr.sunlitweather.db.entity.CacheEntity
import com.driverskr.sunlitweather.db.entity.CityEntity

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 12:00
 * @Description: $
 */
@Database(entities = [CacheEntity::class, CityEntity::class], version = 1, exportSchema = false)
internal abstract class AppDatabase: RoomDatabase() {

    abstract fun cacheDao(): CacheDao

    abstract fun cityDao(): CityDao

    companion object {
        private const val DATABASE_NAME = "sunlit-weather.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also{ instance = it}
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context,AppDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.e("AppDatabase","db: onCreate")
                    }
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                    }
                }).build()
        }
    }
    object MIGRATION0_1 : Migration(0, 1) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //1.创建一个新的符合Entity字段的新表user_new
            database.execSQL(
                "CREATE TABLE AdProfile_New (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT,"       //关注点1
                        + "url TEXT,"       //关注点1
                        + "md5 TEXT,"       //关注点1
                        + "size TEXT,"       //关注点1
                        + "download INTEGER NOT NULL,"     //关注点2
                        + "enable INTEGER NOT NULL)"     //关注点2
            )
            //2.将旧表user中的数据拷贝到新表user_new中
            database.execSQL(("INSERT INTO AdProfile_New(id,name,url,md5,size,download,enable) " + "SELECT id,name,url,md5,size,download,enable FROM AdProfile"))
            //3.删除旧表user
            database.execSQL("DROP TABLE AdProfile")
            //4.将新表user_new重命名为user,升级完毕
            database.execSQL("ALTER TABLE AdProfile_New RENAME TO AdProfile")
            //database.close()
        }
    }
}