package com.driverskr.sunlitweather.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.baidu.location.BDLocation
import com.driverskr.lib.extension.expand
import com.driverskr.lib.extension.startActivity
import com.driverskr.lib.extension.toast
import com.driverskr.lib.net.LoadState
import com.driverskr.lib.utils.permission.PermissionUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.adapter.SearchAdapter
import com.driverskr.sunlitweather.adapter.TopCityAdapter
import com.driverskr.sunlitweather.bean.CityBean
import com.driverskr.sunlitweather.bean.Location
import com.driverskr.sunlitweather.databinding.ActivityAddCityBinding
import com.driverskr.sunlitweather.location.LocationCallback
import com.driverskr.sunlitweather.location.SunlitLocation
import com.driverskr.sunlitweather.ui.activity.vm.SearchViewModel
import com.driverskr.sunlitweather.ui.base.BaseVmActivity
import com.driverskr.sunlitweather.ui.fragment.PermissionFragment

/**
 * @Author: driverSkr
 * @Time: 2023/11/22 13:34
 * @Description: $
 */
class AddCityActivity: BaseVmActivity<ActivityAddCityBinding, SearchViewModel>(), LocationCallback {

    //定位封装
    private lateinit var sunlitLocation: SunlitLocation

    private var searchAdapter: SearchAdapter? = null

    private var topCityAdapter: TopCityAdapter? = null

    private val searchCities by lazy { ArrayList<CityBean>() }

    private val topCities by lazy { ArrayList<String>() }

    private var fromSplash = false

    private var requestedGPS = false

    override fun bindView() = ActivityAddCityBinding.inflate(layoutInflater)

    /**
     * 初始化定位
     */
    private fun initLocation() {
        sunlitLocation = SunlitLocation.getInstance(this)
        sunlitLocation.setCallback(this)
    }

    /**
     * 接收定位
     * @param bdLocation 定位数据
     */
    override fun onReceiveLocation(bdLocation: BDLocation) {
        val district = bdLocation.district //获取区县
        viewModel?.curLocation?.value = district
        viewModel?.loadState?.value = LoadState.Finish
    }

    private fun startLocation() {
        viewModel?.loadState?.postValue(LoadState.Start("正在获取位置..."))
        sunlitLocation.startLocation()
    }

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
        intent?.let {
            fromSplash = it.getBooleanExtra("fromSplash", false)
        }
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        initLocation()

        setTitle("添加城市")
        mBinding.etSearch.threshold = 2

        searchAdapter = SearchAdapter(
            this@AddCityActivity,
            searchCities,
            mBinding.etSearch.text.toString()
        ) {
            viewModel.addCity(it)
        }
        mBinding.rvSearch.adapter = searchAdapter

        topCityAdapter = TopCityAdapter(topCities) {
            viewModel.getCityInfo(it)
        }
        val layoutManager = GridLayoutManager(context, 3)
        mBinding.rvTopCity.adapter = topCityAdapter
        mBinding.rvTopCity.layoutManager = layoutManager

        mBinding.tvGetPos.expand(10,10)

        //编辑框输入监听
        mBinding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keywords = mBinding.etSearch.text.toString()
                if (!TextUtils.isEmpty(keywords)) {
                    viewModel.searchCity(keywords)
                } else {
                    mBinding.rvSearch.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        mBinding.tvGetPos.setOnClickListener {
            getLocation()
        }
        getLocation()
    }

    private fun getLocation() {
        hideKeyboard()
        checkAndOpenGPS()
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        // 定位获取的数据
        viewModel.curLocation.observe(this) {
            if (it.isNullOrEmpty()) {
                toast("获取定位失败")
            } else {
                mBinding.tvCurLocation.visibility = View.VISIBLE
                mBinding.tvCurLocation.text = it
                viewModel.getCityInfo(it, true)
            }
        }

        // 根据定位城市获取详细信息
        viewModel.curCity.observe(this) { item ->
            val curCity = location2CityBean(item)
            // 显示城市详细位置
            mBinding.tvCurLocation.text = curCity.cityName
            viewModel.addCity(curCity, true, fromSplash)
        }

        // 选中的热门城市的信息
        viewModel.choosedCity.observe(this) { item ->
            val curCity = location2CityBean(item)
            viewModel.addCity(curCity)
        }

        viewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Start -> {
                    showLoading(true, it.tip)
                }

                is LoadState.Error -> {
                    toast(it.msg)
                }

                is LoadState.Finish -> {
                    if (viewModel.isStopped()) {
                        showLoading(false)
                    }
                }
            }
        }

        viewModel.addFinish.observe(this) {
            if (it) {
                if (fromSplash) {
                    startActivity<HomeActivity>()
                }
                finish()
            }
        }

        viewModel.searchResult.observe(this) {
            showSearchResult(it)
        }

        viewModel.topCity.observe(this) {
            showTopCity(it)
        }
    }

    /**
     * step1，检查并打开位置服务
     */
    fun checkAndOpenGPS() {
        if (checkGPSOpen()) {
            checkPermission()
        } else {
            AlertDialog.Builder(this)
                .setMessage("请前往设置中打开位置服务")
                .setTitle("打开位置服务")
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->
                    openGPS()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    fun checkPermission() {
        if (checkGPSPermission()) {
            checkGetLocation()
        } else {
            AlertDialog.Builder(this)
                .setMessage("为了方便您获取当前所在城市，需要申请定位权限，请同意授权")
                .setTitle("权限申请")
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->
                    PermissionUtil.with(this@AddCityActivity).permission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                        .onGranted {
                            checkGetLocation()
                        }
                        .onDenied {
                            PermissionUtil.gotoSetting(this@AddCityActivity)
                        }.start()
                }.show()
        }
    }

    /**
     * 检查并获取位置
     */
    fun checkGetLocation() {
        if (checkGPSOpen()) {
            startLocation()
        } else {
            AlertDialog.Builder(this)
                .setMessage("请前往设置中打开位置服务")
                .setTitle("打开定位")
                .setPositiveButton("确定") { _, _ ->
                    openGPS()
                }.show()
        }
    }

    /**
     * 检查GPS权限
     */
    fun checkGPSPermission(): Boolean {
        val pm1 = PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val pm2 = PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return (pm1 || pm2)
    }

    /**
     * 检查GPS状态
     */
    fun checkGPSOpen(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val pr1 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val pr2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return (pr1 || pr2)
    }

    /**
     * 启动GPS
     */
    private fun openGPS() {
        requestedGPS = true
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.add(PermissionFragment.newInstance(), "permission_fragment")
        beginTransaction.commitAllowingStateLoss()
    }

    /**
     * 展示搜索结果
     */
    private fun showSearchResult(basic: List<Location>) {
        mBinding.rvSearch.visibility = View.VISIBLE

        searchCities.clear()

        basic.forEach { item ->
            searchCities.add(location2CityBean(item))
        }
        searchAdapter?.notifyDataSetChanged()
    }

    /**
     * 展示热门城市
     */
    private fun showTopCity(locations: List<String>) {
        topCities.clear()
        topCities.addAll(locations)
        topCityAdapter?.notifyDataSetChanged()
    }

    override fun initData() {
        viewModel.getTopCity()
//        viewModel.getCacheLocation()
    }

    override fun onResume() {
        super.onResume()
        // 获取权限后尝试获取位置
        if (requestedGPS) {
            requestedGPS = false
            checkAndOpenGPS()
        }
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun finish() {
        hideKeyboard()
        super.finish()
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    companion object {
        fun startActivity(context: Context, fromSplash: Boolean = false) {
            val intent = Intent(context, AddCityActivity::class.java)
            intent.putExtra("fromSplash", fromSplash)
            context.startActivity(intent)
        }

        /**
         * location转citybean
         */
        @JvmStatic
        fun location2CityBean(location: Location): CityBean {
            var parentCity = location.adm2
            val adminArea = location.adm1
            val city = location.country
            if (TextUtils.isEmpty(parentCity)) {
                parentCity = adminArea
            }
            if (TextUtils.isEmpty(adminArea)) {
                parentCity = city
            }
            val cityBean = CityBean()
            cityBean.cityName = parentCity + " - " + location.name
            cityBean.cityId = location.id
            cityBean.cnty = city
            cityBean.adminArea = adminArea
            return cityBean
        }
    }
}