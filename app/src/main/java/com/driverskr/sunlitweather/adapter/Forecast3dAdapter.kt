package com.driverskr.sunlitweather.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driverskr.lib.utils.IconUtils
import com.driverskr.lib.utils.WeatherUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.bean.Daily
import com.driverskr.sunlitweather.bean.TempUnit
import com.driverskr.sunlitweather.databinding.ItemForecastBinding
import com.driverskr.sunlitweather.utils.ContentUtil

/**
 * @Author: driverSkr
 * @Time: 2023/11/21 17:49
 * @Description: $
 */
class Forecast3dAdapter(val context: Context, val datas: List<Daily>):
    RecyclerView.Adapter<Forecast3dAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemForecastBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datas[position]
        if (ContentUtil.APP_SETTING_UNIT == TempUnit.HUA.tag) {
            val minHua = WeatherUtil.getF(item.tempMin)
            val maxHua = WeatherUtil.getF(item.tempMax)
            holder.binding.tvTemp.text = "${minHua}~${maxHua}°F"
        } else {
            holder.binding.tvTemp.text = "${item.tempMin}~${item.tempMax}°C"
        }

        var desc = item.textDay
        if (item.textDay != item.textNight) {
            desc += "转" + item.textNight
        }
        holder.binding.tvDesc.text = desc

        when (position) {
            0 -> {
                holder.binding.tvWeek.text = context.getString(R.string.today)
                if (IconUtils.isDay()) {
                    holder.binding.iv3fDay.setImageResourceName(item.iconDay)
                } else {
                    holder.binding.iv3fDay.setImageResourceName(item.iconNight)
                }
            }
            1 -> {
                holder.binding.tvWeek.text = context.getString(R.string.tomorrow)
                holder.binding.iv3fDay.setImageResourceName(item.iconDay)
            }
            else -> {
                holder.binding.tvWeek.text = context.getString(R.string.after_t)
                holder.binding.iv3fDay.setImageResourceName(item.iconDay)
            }
        }
    }

    override fun getItemCount(): Int = 3

    class ViewHolder(val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root)
}