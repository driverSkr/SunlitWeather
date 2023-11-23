package com.driverskr.sunlitweather.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.adapter.CityManagerAdapter
import com.driverskr.sunlitweather.adapter.MyItemTouchCallback
import com.driverskr.sunlitweather.databinding.ActivityCityManagerBinding
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import com.driverskr.sunlitweather.ui.activity.vm.CityManagerViewModel
import com.driverskr.sunlitweather.ui.base.BaseVmActivity
import com.driverskr.sunlitweather.utils.ContentUtil

class CityManagerActivity: BaseVmActivity<ActivityCityManagerBinding, CityManagerViewModel>() {

    private val datas by lazy { ArrayList<CityEntity>() }

    private val dataLocal by lazy { ArrayList<CityEntity>() }

    private var adapterLocal: CityManagerAdapter? = null

    private var adapter: CityManagerAdapter? = null

    //    @Inject
    lateinit var itemTouchCallback: MyItemTouchCallback

    override fun bindView() = ActivityCityManagerBinding.inflate(layoutInflater)

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        setTitle(getString(R.string.control_city))

        itemTouchCallback = MyItemTouchCallback(this)

        adapterLocal = CityManagerAdapter(dataLocal){}

        adapter = CityManagerAdapter(datas) {
            viewModel.updateCities(it)
            ContentUtil.CITY_CHANGE = true
        }

        mBinding.rvLocal.adapter = adapterLocal

        mBinding.recycleView.adapter = adapter

        mBinding.recycleView.setStateCallback {
            itemTouchCallback.dragEnable = it
        }

        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(mBinding.recycleView)
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        adapter?.listener = object : CityManagerAdapter.OnCityRemoveListener {
            override fun onCityRemove(pos: Int) {
                viewModel.removeCity(datas[pos].cityId)
                datas.removeAt(pos)
                adapter?.notifyItemRemoved(pos)
                ContentUtil.CITY_CHANGE = true
            }
        }

        viewModel.cities.observe(this) {
            dataLocal.clear()
            datas.clear()
            for (cityEntity in it) {
                if (cityEntity.isLocal) {
                    dataLocal.add(cityEntity)
                } else {
                    datas.add(cityEntity)
                }
            }
            adapterLocal?.notifyDataSetChanged()
            adapter?.notifyDataSetChanged()
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        viewModel.getCities()
    }

}