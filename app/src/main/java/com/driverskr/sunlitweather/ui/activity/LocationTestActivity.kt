package com.driverskr.sunlitweather.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.baidu.location.BDLocation
import com.driverskr.sunlitweather.databinding.ActivityLocationTestBinding
import com.driverskr.sunlitweather.location.LocationCallback
import com.driverskr.sunlitweather.location.SunlitLocation

/**
 * 测试下定位是否可以使用
 */
class LocationTestActivity : AppCompatActivity(), LocationCallback {

    private lateinit var binding: ActivityLocationTestBinding
    //权限数组
    private val permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //请求权限意图
    private lateinit var requestPermissionIntent: ActivityResultLauncher<Array<String>>

    private lateinit var sunlitLocation: SunlitLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        registerIntent()    //注册权限请求（获得请求的结果）
        super.onCreate(savedInstanceState)
        binding = ActivityLocationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLocation()  //初始化定位

        requestPermission() //请求权限
    }

    /**
     * 初始化定位
     */
    private fun initLocation() {
        sunlitLocation = SunlitLocation.getInstance(this)
        sunlitLocation.setCallback(this)
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        sunlitLocation.startLocation()
    }

    /**
     * 接收定位
     * @param bdLocation 定位数据
     */
    override fun onReceiveLocation(bdLocation: BDLocation) {
        val address  = bdLocation.addrStr
        binding.tvAddressDetail.text = address
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
}