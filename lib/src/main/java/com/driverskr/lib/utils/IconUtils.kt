package com.driverskr.lib.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.driverskr.lib.R
import com.driverskr.lib.plugin.PluginUtil
import com.driverskr.lib.utils.WeatherUtil.convert
import com.driverskr.lib.utils.DateUtil.getNowHour

/**
 * @Author: driverSkr
 * @Time: 2023/11/17 16:54
 * @Description: $
 */
object IconUtils {

    @JvmStatic
    fun getDayIcon(context: Context, weatherCode: String): Drawable? {
        return getIcon(context, weatherCode, "d")
    }

    @JvmStatic
    fun getNightIcon(context: Context, weatherCode: String): Drawable? {
        return getIcon(context, weatherCode, "n")
    }

    fun getIcon(context: Context, weatherCode: String, postFix: String): Drawable? {
        val isPlugin = SpUtil.getInstance(context).getThemeFlag() == 1
        val code = parseCode(weatherCode, postFix)
        val resName = "icon_$code"
        if (isPlugin) {
            val pluginRes = PluginUtil.getPluginRes(context, resName)
            return pluginRes
                ?: context.resources.getDrawable(R.drawable.icon_100d)
        } else {
            val resId = getDrawableRes(context, resName, R.drawable.icon_100d)
            return context.resources.getDrawable(resId)
        }
    }

    /**
     * 获取白天深色天气图标
     */
    @JvmStatic
    fun getDayIconDark(context: Context, weather: String): Int {
        val code = parseCode(weather, "d")
        return getDrawableRes(context, "icon_$code", R.drawable.icon_100d)
    }

    /**
     * 获取白天深色天气图标
     */
    @JvmStatic
    fun getNightIconDark(context: Context, weather: String): Int {
        val code = parseCode(weather, "n")
        return getDrawableRes(context, "icon_$code", R.drawable.icon_100n)
    }

    private fun parseCode(weather: String, postFix: String): String {
        return when (val code = (weather.ifEmpty { "0" }).toInt()) {
            in 150 until 199,
            in 350 until 399,
            in 450 until 499 -> {
                (code - 50).toString() + "n"
            }
            else -> {
                code.toString() + postFix
            }
        }
    }

    /**
     * 获取白天背景
     */
    val defaultBg: Int
        get() = if (isDay()) R.drawable.bg_0_d else R.drawable.bg_0_n

    @JvmStatic
    fun isDay(): Boolean {
        val now = getNowHour()
        return now in 7..18
    }

    /**
     * 获取白天背景
     */
    fun getBg(context: Context, code: Int): Int {
        return if (isDay()) getDayBg(context, code) else getNightBg(
            context,
            code
        )
    }

    /**
     * 获取白天背景
     */
    fun getDayBg(context: Context, code: Int): Int {
        var newCode = convert(code)
        if (newCode > 10) {
            newCode /= 10
        }
        return getDrawableRes(context, "bg_" + newCode + "_d", R.drawable.bg_0_d)
    }

    /**
     * 获取晚上背景
     */
    fun getNightBg(context: Context, code: Int): Int {
        var newCode = convert(code)
        if (newCode > 10) {
            newCode /= 10
        }
        return getDrawableRes(context, "bg_" + newCode + "_n", R.drawable.bg_0_n)
    }

    fun getDrawableRes(context: Context, weather: String, def: Int): Int {
        return getRes(context, "drawable", weather, def)
    }

    fun getRes(context: Context, type: String?, weather: String, def: Int): Int {
        return try {
            var id = context.resources.getIdentifier(weather, type, context.packageName)
            if (id == 0) {
                id = def
            }
            id
        } catch (e: Exception) {
            Log.e("IconUtils","获取资源失败：$weather")
            def
        }
    }
}