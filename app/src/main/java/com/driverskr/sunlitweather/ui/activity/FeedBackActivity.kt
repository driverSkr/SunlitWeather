package com.driverskr.sunlitweather.ui.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.driverskr.lib.extension.toast
import com.driverskr.lib.net.LoadState
import com.driverskr.lib.utils.DeviceUtil
import com.driverskr.lib.utils.LogUtil
import com.driverskr.lib.utils.permission.PermissionUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.databinding.ActivityFeedBackBinding
import com.driverskr.sunlitweather.ui.activity.vm.FeedBackViewModel
import com.driverskr.sunlitweather.ui.base.BaseActivity

class FeedBackActivity: BaseActivity<ActivityFeedBackBinding>() {

    private lateinit var textWatcher: TextWatcher

    private val viewModel: FeedBackViewModel by viewModels()

    override fun bindView() = ActivityFeedBackBinding.inflate(layoutInflater)

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
        setTitle(getString(R.string.feed_back))
    }

    /**
     * 处理事件
     */
    override fun initEvent() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              mBinding.tvInputCount.text = getString(R.string.feed_back_size, s?.length)
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        mBinding.etFeedBack.addTextChangedListener(textWatcher)

        mBinding.btnCommit.setOnClickListener {
            if (mBinding.etFeedBack.text.toString().isBlank()) {
                toast("请输入您的意见")
                return@setOnClickListener
            }

            viewModel.sendFeedBack(mBinding.etFeedBack.text.toString())
        }

        viewModel.feedBackResult.observe(this) {
            toast(it)
            finish()
        }

        viewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Start -> {
                    showLoading(true)
                }
                is LoadState.Error -> {
                    toast(it.msg)
                }
                is LoadState.Finish -> {
                    showLoading(false)
                }
            }
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.etFeedBack.removeTextChangedListener(textWatcher)
    }

    fun getEmei() {
        PermissionUtil.with(this).permission(android.Manifest.permission.READ_PHONE_STATE)
            .onGranted {
                LogUtil.e(DeviceUtil.getCPUSerial(context))
            }.onDenied {
                toast("没有权限")
            }.start()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, FeedBackActivity::class.java)
            context.startActivity(intent)
        }
    }
}