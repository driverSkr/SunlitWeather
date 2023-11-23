package com.driverskr.sunlitweather.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import coil.imageLoader
import coil.load
import coil.transform.CircleCropTransformation
import com.baidu.location.BDLocation
import com.driverskr.lib.EffectUtil
import com.driverskr.lib.extension.expand
import com.driverskr.lib.extension.startActivity
import com.driverskr.lib.extension.toast
import com.driverskr.lib.net.LoadState
import com.driverskr.lib.utils.IconUtils
import com.driverskr.lib.utils.LogUtil
import com.driverskr.lib.utils.SpUtil
import com.driverskr.lib.utils.permission.PermissionUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.adapter.ViewPagerAdapter
import com.driverskr.sunlitweather.bean.TempUnit
import com.driverskr.sunlitweather.bean.UserInfoBean
import com.driverskr.sunlitweather.databinding.ActivityHomeBinding
import com.driverskr.sunlitweather.databinding.NavHeaderMainBinding
import com.driverskr.sunlitweather.dialog.ChangeCityDialog
import com.driverskr.sunlitweather.dialog.UpgradeDialog
import com.driverskr.sunlitweather.location.LocationCallback
import com.driverskr.sunlitweather.location.SunlitLocation
import com.driverskr.sunlitweather.logic.AppRepository
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import com.driverskr.sunlitweather.ui.activity.vm.HomeViewModel
import com.driverskr.sunlitweather.ui.activity.vm.LAST_LOCATION
import com.driverskr.sunlitweather.ui.activity.vm.LoginViewModel
import com.driverskr.sunlitweather.ui.activity.vm.SearchViewModel
import com.driverskr.sunlitweather.ui.base.BaseVmActivity
import com.driverskr.sunlitweather.ui.fragment.WeatherFragment
import com.driverskr.sunlitweather.utils.ContentUtil
import com.driverskr.sunlitweather.utils.DisplayUtil
import com.driverskr.sunlitweather.utils.TencentUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseVmActivity<ActivityHomeBinding, HomeViewModel>(), LocationCallback {

    //权限数组
    private val permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //请求权限意图
    private lateinit var requestPermissionIntent: ActivityResultLauncher<Array<String>>
    //定位封装
    private lateinit var sunlitLocation: SunlitLocation

    private val fragments: MutableList<Fragment> by lazy { ArrayList() }
    private val cityList = ArrayList<CityEntity>()
    private var mCurIndex = 0
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var navHeaderBinding: NavHeaderMainBinding

    private var locationViewModel: SearchViewModel? = null

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //此处是跳转的result回调方法
            if (it.data != null && it.resultCode == Activity.RESULT_OK) {
                val isLogin = it.data!!.getBooleanExtra("login", false)
                if (isLogin) {
                    loginViewModel.register(it.data!!.getSerializableExtra("user_info") as UserInfoBean)
                    toast("登录成功")
                } else {
                    toast("已退出登录")
                }
                initUserInfo()
            }
        }

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

        mBinding.tvLocation.text = district
        viewModel.getSearchCity(district,true)

        locationViewModel?.curLocation?.value = district
        locationViewModel?.loadState?.value = LoadState.Finish
    }

    private fun startLocation() {
        locationViewModel?.loadState?.postValue(LoadState.Start("正在获取位置..."))
        sunlitLocation.startLocation()
    }

    private fun registerIntent() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val fineLocation = true == it[Manifest.permission.ACCESS_FINE_LOCATION]
            val writeStorage = true == it[Manifest.permission.WRITE_EXTERNAL_STORAGE]
            if (fineLocation && writeStorage) {
                //权限已经获取到，开始定位
                startLocation()
            }
        }
    }

    private fun requestPermission() {
        //因为项目的最低版本API是23，所以肯定需要动态请求危险权限，只需要判断权限是否拥有即可
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions)
            return
        }
        //开始定位
        startLocation()
    }

    /**
     * 当前的天气code
     */
    private var currentCode = ""

    override fun bindView() = ActivityHomeBinding.inflate(layoutInflater)

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
        registerIntent()

        viewModel.searchCity.observe(this) {
            Log.d("driverSkr",it.toString())
        }
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        initLocation()
        requestPermission()

        hideTitleBar()
        // 沉浸式态栏
        immersionStatusBar()

        mBinding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        mBinding.viewPager.offscreenPageLimit = 5

        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                mBinding.ivLoc.visibility =
                    if (cityList[position].isLocal) View.VISIBLE else View.INVISIBLE

                mBinding.llRound.getChildAt(mCurIndex).isEnabled = false
                mBinding.llRound.getChildAt(position).isEnabled = true
                mCurIndex = position
                mBinding.tvLocation.text = cityList[position].cityName
            }
        })

        mBinding.ivAddCity.expand(10, 10)

        mBinding.ivBg.setImageResource(IconUtils.defaultBg)

        navHeaderBinding = NavHeaderMainBinding.bind(mBinding.navView.getHeaderView(0))
        // 侧边栏顶部下移状态栏高度
        ViewCompat.setOnApplyWindowInsetsListener(navHeaderBinding.llUserHeader) { view, insets ->
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.topMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            insets
        }

        // 设置默认单位
        val unitConfig = PreferenceManager.getDefaultSharedPreferences(context)
            .getString("unit", TempUnit.SHE.tag)
        val menu = mBinding.navView.menu
        if (unitConfig == "she") {
            menu.findItem(R.id.navShe).isChecked = true
        } else {
            menu.findItem(R.id.navHua).isChecked = true
        }
        menu.findItem(R.id.itemUnit).subMenu.setGroupCheckable(R.id.navUnitGroup, true, true)

        //用户信息
        initUserInfo()
    }

    private fun initUserInfo() {
        val account = SpUtil.getInstance(this).account
        if (account.isNotEmpty()) {
            navHeaderBinding.tvAccount.text = account
            navHeaderBinding.ivAvatar.load(
                SpUtil.getInstance(this).avatar, imageLoader = context.imageLoader
            ) {
                placeholder(R.drawable.ic_avatar_default)
                transformations(CircleCropTransformation())
            }
        } else {
            navHeaderBinding.tvAccount.text = getString(R.string.login_plz)
            navHeaderBinding.ivAvatar.load(R.drawable.ic_avatar_default)
        }
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        mBinding.ivSetting.setOnClickListener {
            if (!mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                mBinding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }
        mBinding.ivAddCity.setOnClickListener {
            startActivity<AddCityActivity>()
        }
        mBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navCity -> {
                    startActivity<CityManagerActivity>()
                }
                R.id.navTheme -> {
                    startActivity<ThemeActivity>()
                }
                R.id.navShe -> {
                    changeUnit(TempUnit.SHE)
                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.navHua -> {
                    changeUnit(TempUnit.HUA)
                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.navFeedback -> {
                    startActivity<FeedBackActivity>()
                }
                R.id.navAbout -> {
                    startActivity<AboutActivity>()
                }
            }
            true
        }

        navHeaderBinding.llUserHeader.setOnClickListener {
            if (SpUtil.getInstance(this).account.isEmpty()) {
                launcher.launch(Intent(this, LoginActivity::class.java))
            } else {
                launcher.launch(Intent(this, UserInfoActivity::class.java))
            }
        }

        // 检查登录状态
        loginViewModel.checkLogin().observe(this) {
            if (!it && SpUtil.getInstance(this).account.isNotEmpty()) {
                TencentUtil.sTencent.logout(this)
                SpUtil.getInstance(this).logout()
            }
        }

        viewModel.mCities.observe(this) {
            if (it.isEmpty()) {
                startActivity<AddCityActivity>()
            } else {
                cityList.clear()
                cityList.addAll(it)
                showCity()
            }
        }

        viewModel.newVersion.observe(this) {
            UpgradeDialog(it).show(supportFragmentManager, "upgrade_dialog")
        }

        viewModel.mCurCondCode.observe(this, ::changeBg)

        getLocation()
    }

    private fun changeUnit(unit: TempUnit) {
        viewModel.changeUnit(unit)
        (fragments[mCurIndex] as WeatherFragment).changeUnit()
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        viewModel.getCities()
        viewModel.checkVersion()
    }

    /**
     * 显示城市
     */
    private fun showCity() {
        if (mCurIndex > cityList.size - 1) {
            mCurIndex = cityList.size - 1
        }

        mBinding.ivLoc.visibility =
            if (cityList[mCurIndex].isLocal) View.VISIBLE else View.INVISIBLE
        mBinding.tvLocation.text = cityList[mCurIndex].cityName

        mBinding.llRound.removeAllViews()

        // 宽高参数
        val size = DisplayUtil.dp2px(4f)
        val layoutParams = LinearLayout.LayoutParams(size, size)
        // 设置间隔
        layoutParams.rightMargin = 12

        for (i in cityList.indices) {
            // 创建底部指示器(小圆点)
            val view = View(this@HomeActivity)
            view.setBackgroundResource(R.drawable.background)
            view.isEnabled = false

            // 添加到LinearLayout
            mBinding.llRound.addView(view, layoutParams)
        }
        // 小白点
        mBinding.llRound.getChildAt(mCurIndex).isEnabled = true
        mBinding.llRound.visibility = if (cityList.size <= 1) View.GONE else View.VISIBLE

        fragments.clear()
        for (city in cityList) {
            val cityId = city.cityId
//            LogUtil.i("cityId: " + cityId)
            val weatherFragment = WeatherFragment.newInstance(cityId)
            fragments.add(weatherFragment)
        }

//        mBinding.viewPager.adapter?.notifyDataSetChanged()
        mBinding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        mBinding.viewPager.currentItem = mCurIndex
    }

    override fun onResume() {
        super.onResume()
        if (ContentUtil.CITY_CHANGE) {
            viewModel.getCities()
            ContentUtil.CITY_CHANGE = false
        }
        mBinding.ivEffect.drawable?.let {
            (it as Animatable).start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    private fun changeBg(condCode: String) {
        if (currentCode == condCode) {
            return
        }
        currentCode = condCode
        // 获取背景
        val bgDrawable = IconUtils.getBg(this@HomeActivity, condCode.toInt())

        val originDrawable = mBinding.ivBg.drawable
        val targetDrawable = resources.getDrawable(bgDrawable)
        val transitionDrawable = TransitionDrawable(
            arrayOf<Drawable>(
                originDrawable,
                targetDrawable
            )
        )

        mBinding.ivBg.setImageDrawable(transitionDrawable)
        transitionDrawable.isCrossFadeEnabled = true
        transitionDrawable.startTransition(1000)

        // 获取特效
        val effectDrawable = EffectUtil.getEffect(context, condCode.toInt())
        mBinding.ivEffect.setImageDrawable(effectDrawable)
    }

    /**
     * 获取当前城市
     */
    private fun getLocation() {
        if (checkGPSAndPermission()) {
            locationViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
            locationViewModel?.curLocation?.observe(this) {
                if (!it.isNullOrEmpty()) {
                    judgeLocation(it)
                }
            }
            // 根据定位城市获取详细信息
            locationViewModel?.curCity?.observe(this) { item ->
                val curCity = AddCityActivity.location2CityBean(item)
                locationViewModel?.addCity(curCity, isLocal = true, fromSplash = true)
            }
            locationViewModel?.addFinish?.observe(this) {
                viewModel.getCities()
                ContentUtil.CITY_CHANGE = false
            }
        }
    }

    /**
     * 判断城市变化
     */
    private fun judgeLocation(cityName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cacheLocation = AppRepository.getInstance().getCache<String>(LAST_LOCATION)
            LogUtil.e("location: $cityName")
            LogUtil.e("cacheLocation: $cacheLocation")
            withContext(Dispatchers.Main) {
                if (cityName != cacheLocation) {
                    ChangeCityDialog(this@HomeActivity).apply {
                        setContent("检测到当前城市为${cityName}，是否切换到该城市")
                        setOnConfirmListener {
                            locationViewModel?.getCityInfo(cityName, true)
                        }
                        show()
                    }
                }
            }
        }
    }

    /**
     * 检查GPS状态及GPS权限
     */
    private fun checkGPSAndPermission(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val pr1 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val pr2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if ((pr1 || pr2)) {
            val pm1 = PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            val pm2 = PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            return (pm1 || pm2)
        }
        return false
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.ivEffect.drawable?.let {
            (it as Animatable).stop()
        }
    }
}