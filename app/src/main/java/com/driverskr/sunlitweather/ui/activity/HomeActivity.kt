package com.driverskr.sunlitweather.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.preference.PreferenceManager
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.baidu.location.BDLocation
import com.driverskr.lib.extension.expand
import com.driverskr.lib.utils.IconUtils
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.adapter.ViewPagerAdapter
import com.driverskr.sunlitweather.bean.TempUnit
import com.driverskr.sunlitweather.databinding.ActivityHomeBinding
import com.driverskr.sunlitweather.databinding.NavHeaderMainBinding
import com.driverskr.sunlitweather.location.LocationCallback
import com.driverskr.sunlitweather.location.SunlitLocation
import com.driverskr.sunlitweather.logic.db.entity.CityEntity
import com.driverskr.sunlitweather.ui.activity.vm.HomeViewModel
import com.driverskr.sunlitweather.ui.base.BaseVmActivity
import com.driverskr.sunlitweather.utils.ContentUtil

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

    private lateinit var navHeaderBinding: NavHeaderMainBinding
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
    }

    private fun startLocation() {
        sunlitLocation.startLocation()
    }

    private fun registerIntent() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val fineLocation = true == it[Manifest.permission.ACCESS_FINE_LOCATION]
            val writeStorage = true == it[Manifest.permission.WRITE_EXTERNAL_STORAGE]
            if (fineLocation && writeStorage) {
                //权限已经获取到，开始定位
                startLocation();
            }
        }
    }

    private fun requestPermission() {
        //因为项目的最低版本API是23，所以肯定需要动态请求危险权限，只需要判断权限是否拥有即可
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return
        }
        //开始定位
        startLocation();
    }

    override fun bindView() = ActivityHomeBinding.inflate(layoutInflater)

    /**
     * 接收数据
     * @param intent
     */
    override fun prepareData(intent: Intent?) {
        registerIntent()
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
        //initUserInfo()
    }

    /*private fun initUserInfo() {
        val account = SpUtil.getAccount(this)
        if (account.isNotEmpty()) {
            navHeaderBinding.tvAccount.text = account
            navHeaderBinding.ivAvatar.load(
                SpUtil.getAvatar(this), imageLoader = context.imageLoader
            ) {
                placeholder(R.drawable.ic_avatar_default)
                transformations(CircleCropTransformation())
            }
        } else {
            navHeaderBinding.tvAccount.text = getString(R.string.login_plz)
            navHeaderBinding.ivAvatar.load(R.drawable.ic_avatar_default)
        }
    }*/

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
            Toast.makeText(this,"添加城市",Toast.LENGTH_SHORT).show()
        }
        mBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navCity -> {
                    Toast.makeText(this,"启动城市管理活动",Toast.LENGTH_SHORT).show()
                }
                R.id.navTheme -> {
                    Toast.makeText(this,"启动主题活动",Toast.LENGTH_SHORT).show()
                }
                R.id.navShe -> {
                    Toast.makeText(this,"使用摄氏度",Toast.LENGTH_SHORT).show()
                }
                R.id.navHua -> {
                    Toast.makeText(this,"使用华氏度",Toast.LENGTH_SHORT).show()
                }
                R.id.navFeedback -> {
                    Toast.makeText(this,"启动反馈活动",Toast.LENGTH_SHORT).show()
                }
                R.id.navAbout -> {
                    Toast.makeText(this,"启动关于活动",Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

    }

    /**
     * 初始化数据
     */
    override fun initData() {
        viewModel.getCities()
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

    override fun onDestroy() {
        super.onDestroy()
        mBinding.ivEffect.drawable?.let {
            (it as Animatable).stop()
        }
    }
}