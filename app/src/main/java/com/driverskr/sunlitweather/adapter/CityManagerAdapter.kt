package com.driverskr.sunlitweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driverskr.sunlitweather.databinding.ItemCityManagerBinding
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import java.util.*

class CityManagerAdapter(
    private val mData: List<CityEntity>,
    var onSort: ((List<CityEntity>) -> Unit)? = null
) : RecyclerView.Adapter<CityManagerAdapter.ViewHolder>(), IDragSort {

    var listener: OnCityRemoveListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemCityManagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[holder.adapterPosition]
        holder.binding.tvItemCity.text = item.cityName

        holder.binding.tvDelete.setOnClickListener {
            listener?.onCityRemove(holder.adapterPosition)
        }
        if (item.isLocal) {
            holder.binding.ivLocal.visibility = View.VISIBLE
            holder.binding.ivDrag.visibility = View.GONE

        } else {
            holder.binding.ivLocal.visibility = View.GONE
            holder.binding.ivDrag.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount() = mData.size

    class ViewHolder(val binding: ItemCityManagerBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    public interface OnCityRemoveListener {
        fun onCityRemove(pos: Int)
    }

    override fun move(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mData, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mData, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun dragFinish() {
        onSort?.let { it(mData) }
    }
}