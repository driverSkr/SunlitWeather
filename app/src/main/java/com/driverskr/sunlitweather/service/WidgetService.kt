package com.driverskr.sunlitweather.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.driverskr.lib.net.HttpUtils
import com.driverskr.lib.utils.IconUtils
import com.driverskr.lib.utils.LogUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.bean.Now
import com.driverskr.sunlitweather.bean.WeatherNow
import com.driverskr.sunlitweather.logic.AppRepository
import com.driverskr.sunlitweather.ui.activity.SplashActivity
import com.driverskr.sunlitweather.ui.fragment.vm.CACHE_WEATHER_NOW
import com.driverskr.sunlitweather.utils.Constant
import com.driverskr.sunlitweather.utils.Lunar
import com.driverskr.sunlitweather.utils.NotificationUtil
import com.driverskr.sunlitweather.utils.RomUtil
import com.driverskr.sunlitweather.widget.WeatherWidget
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import java.util.*

const val Notify_Id = 999

class WidgetService : LifecycleService() {

    lateinit var connManager: ConnectivityManager

    /**
     * 防止Service首次启动时执行onStartCommand()中的updateRemoteOnce()
     */
    private var isFirst = true

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isFirst = true
        LogUtil.e("onCreate: ---------------------")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                Notify_Id,
                NotificationUtil.createNotification(this, Notify_Id),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                Notify_Id,
                NotificationUtil.createNotification(this, Notify_Id)
            )
        }

        connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connManager.registerDefaultNetworkCallback(callback)
        } else {
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            registerReceiver(netWorkStateReceiver, intentFilter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isFirst) {
            isFirst = false
        } else {
            lifecycleScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                updateRemoteOnce()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val callback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            LogUtil.d("network available。。。。")
            updateRemote()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            LogUtil.d("network unavailable。。。。")
            intervalJob?.cancel()
            intervalJob = null
        }
    }

    private val netWorkStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val activeNetworkInfo = connManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isAvailable) {
                LogUtil.d("network available。。。。")
                updateRemote()
            } else {
                LogUtil.d("network unavailable。。。。")
                intervalJob?.cancel()
                intervalJob = null
            }
        }
    }

    private suspend fun getWeather(cityId: String) {
        flow {
            val url = "https://devapi.qweather.com/v7/weather/now"
            val param = HashMap<String, Any>()
            param["location"] = cityId
            param["key"] = Constant.HEFENG_KEY

            HttpUtils.get<WeatherNow>(url, param, null)?.now?.let { emit(it) }
        }.flowOn(Dispatchers.Main).collect {

        }
    }

    private var intervalJob: Job? = null

    private fun updateRemote() {
        if (intervalJob != null) {
            return
        }
        intervalJob = lifecycleScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            LogUtil.e("WidgetService: 异常...")
        }) {
            while (isActive) {
                LogUtil.d("intervalJob run")
                updateRemoteOnce()
                delay(1800_000)
            }
        }
    }

    private suspend fun updateRemoteOnce() {
        val cities = AppRepository.getInstance().getCities()
        if (cities.isNotEmpty()) {
            var cityId = cities[0].cityId
            var cityName = cities[0].cityName
            cities.forEach {
                if (it.isLocal) {
                    cityId = it.cityId
                    cityName = it.cityName
                }
                return@forEach
            }

            val url = "https://devapi.qweather.com/v7/weather/now"
            val param = HashMap<String, Any>()
            param["location"] = cityId
            param["key"] = Constant.HEFENG_KEY

            val result = HttpUtils.get<WeatherNow>(url, param)
            result?.let {
                val now = it.now

                NotificationUtil.updateNotification(
                    this@WidgetService,
                    Notify_Id,
                    cityName,
                    now
                )

                updateWidget(cityId, cityName, now)
            }
        }
    }

    @SuppressLint("RemoteViewLayout")
    private suspend fun updateWidget(cityId: String, cityName: String, now: Now?) {
        LogUtil.d("updateWidget.............")

        val views = RemoteViews(packageName, R.layout.weather_widget)
        val location = if (cityName.contains("-")) cityName.split("-")[1] else cityName
        views.setTextViewText(R.id.tvLocation, location)

        now?.let {
            AppRepository.getInstance()
                .saveCache(CACHE_WEATHER_NOW + cityId, it)

            views.setTextViewText(R.id.tvWeather, it.text)
            views.setTextViewText(R.id.tvTemp, it.temp + "°C")
            if (IconUtils.isDay()) {
                views.setImageViewResource(R.id.ivWeather, IconUtils.getDayIconDark(this, it.icon))
            } else {
                views.setImageViewResource(
                    R.id.ivWeather,
                    IconUtils.getNightIconDark(this, it.icon)
                )
            }
        }
        views.setTextViewText(R.id.tvLunarDate, Lunar(Calendar.getInstance()).toString())

        initEvent(views)

        val componentName = ComponentName(this, WeatherWidget::class.java)
        AppWidgetManager.getInstance(this).updateAppWidget(componentName, views)
    }

    /**
     * 点击事件相关
     */
    private fun initEvent(views: RemoteViews) {
        // 日历
        val calendarIntent = Intent()

        val calendarCls = getCalendarCls()
        calendarIntent.component = ComponentName(calendarCls.first, calendarCls.second)
        val calendarPI = PendingIntent.getActivity(
            this, 0, calendarIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.llCalendar, calendarPI)
        views.setOnClickPendingIntent(R.id.tvLunarDate, calendarPI)

        // 时钟
        val clockIntent = Intent()

        val clockComponent = getClockComponent()
        clockIntent.component = ComponentName(clockComponent.first, clockComponent.second)
        val timePI = PendingIntent.getActivity(this, 0, clockIntent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.clockTime, timePI)

        // 风云
        val weatherIntent = Intent(this, SplashActivity::class.java)
        val weatherPI =
//            PendingIntent.getActivity(this, 0, weatherIntent, PendingIntent.FLAG_MUTABLE)
            PendingIntent.getActivity(this, 0, weatherIntent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.llWeather, weatherPI)
    }

    private fun getCalendarCls(): Pair<String, String> {
        return when {
            RomUtil.isMiui() -> "com.android.calendar" to "com.android.calendar.homepage.AllInOneActivity"
            RomUtil.isOppo() -> "com.coloros.calendar" to "com.android.calendar.AllInOneActivity"
            else -> "com.android.calendar" to "com.android.calendar.LaunchActivity"
        }
    }

    private fun getClockComponent(): Pair<String, String> {
        return when {
            RomUtil.isEmui() -> "com.android.deskclock" to "com.android.deskclock.AlarmsMainActivity"
            RomUtil.isMiui() -> "com.android.deskclock" to "com.android.deskclock.DeskClockTabActivity"
            RomUtil.isOppo() -> "com.coloros.alarmclock" to "com.coloros.alarmclock.AlarmClock"
            else -> "com.android.deskclock" to "com.android.deskclock.DeskClock"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connManager.unregisterNetworkCallback(callback)
        } else {
            unregisterReceiver(netWorkStateReceiver)
        }
        LogUtil.e("onDestroy: ---------------------")
    }
}