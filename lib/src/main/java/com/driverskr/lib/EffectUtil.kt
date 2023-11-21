package com.driverskr.lib

import android.content.Context
import android.graphics.drawable.Drawable
import com.driverskr.lib.specialeffects.*
import com.driverskr.lib.utils.WeatherUtil
import me.wsj.lib.utils.DateUtil

class EffectUtil {

    companion object {
        fun getEffect(context: Context, code: Int): Drawable? {
            val isDay = DateUtil.getNowHour() in 7 until 18
            val type = WeatherUtil.convert(code)
            return when (type) {
                1 -> {
                    if (isDay)
                        EffectSunDrawable(context.resources.getDrawable(R.drawable.sun_icon))
                    else
                        EffectMoonNDrawable(context.resources.getDrawable(R.drawable.bg_fine_night_moon))
                }
                2 -> {
                    if (isDay) {
                        EffectCloudDrawable(
                            arrayOf(
                                context.resources.getDrawable(R.drawable.cloudy_day_1),
                                context.resources.getDrawable(R.drawable.cloudy_day_2),
                                context.resources.getDrawable(R.drawable.cloudy_day_3),
                                context.resources.getDrawable(R.drawable.cloudy_day_4)
                            )
                        )
                    } else
                        EffectCloudDrawable(
                            arrayOf(
                                context.resources.getDrawable(R.drawable.cloudy_day_1),
                                context.resources.getDrawable(R.drawable.cloudy_night1),
                                context.resources.getDrawable(R.drawable.cloudy_night2)
                            )
                        )
                }
                3 -> {
                    EffectCloudDrawable(
                        arrayOf(
                            context.resources.getDrawable(R.drawable.fog_cloud_1),
                            context.resources.getDrawable(R.drawable.fog_cloud_2)
                        )
                    )
                }
                4 -> {  // 中雨
                    EffectRainDrawable(
                        1,
                        arrayOf(
                            context.resources.getDrawable(R.drawable.raindrop_s),
                            context.resources.getDrawable(R.drawable.raindrop_m),
                            context.resources.getDrawable(R.drawable.raindrop_l),
                            context.resources.getDrawable(R.drawable.raindrop_xl)
                        )
                    )
                }
                40 -> {  // 小雨
                    EffectRainDrawable(
                        0,
                        arrayOf(
                            context.resources.getDrawable(R.drawable.raindrop_s),
                            context.resources.getDrawable(R.drawable.raindrop_m),
                            context.resources.getDrawable(R.drawable.raindrop_l),
                            context.resources.getDrawable(R.drawable.raindrop_xl)
                        )
                    )
                }
                42 -> {  // 大雨
                    EffectRainDrawable(
                        2,
                        arrayOf(
                            context.resources.getDrawable(R.drawable.raindrop_m),
                            context.resources.getDrawable(R.drawable.raindrop_l),
                            context.resources.getDrawable(R.drawable.raindrop_xl),
                            context.resources.getDrawable(R.drawable.raindrop_2xl)
                        )
                    )
                }
                5 -> {
                    EffectLightningDrawable(
                        context.resources.getDrawable(R.drawable.lightning_1),
                        context.resources.getDrawable(R.drawable.lightning_2)
                    )
                }
                6 -> {  // 小雪
                    EffectSnowDrawable(
                        0,
                        arrayOf(
                            context.resources.getDrawable(R.drawable.snowflake_tiny),
                            context.resources.getDrawable(R.drawable.snowflake_s),
                            context.resources.getDrawable(R.drawable.snowflake_m),
                            context.resources.getDrawable(R.drawable.snowflake_l),
                            context.resources.getDrawable(R.drawable.snowflake_xl),
                            context.resources.getDrawable(R.drawable.snowflake_xxl)
                        )
                    )
                }
                61 -> {  // 大雪
                    EffectSnow2Drawable(
                        0,
                        arrayOf(
//                            context.resources.getDrawable(R.drawable.snow_flower),
                            context.resources.getDrawable(R.drawable.snow_flower_22),
                            context.resources.getDrawable(R.drawable.snow_flower_24)
                        )
                    )
                }
                else -> {
                    null
                }
            }
        }
    }
}