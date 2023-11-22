package com.driverskr.lib.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import com.driverskr.lib.R
import com.driverskr.lib.databinding.DialogLoadingBinding
import com.driverskr.lib.view.LoadingDrawable

/**
 * @Author: driverSkr
 * @Time: 2023/11/15 11:48
 * @Description: 自定义加载进度对话框$
 */
class LoadingDialog(context: Context): BaseDialog<DialogLoadingBinding?>(context, 0.38f, 0f) {
    var loadingDrawable: LoadingDrawable? = null

    override fun bindView() = DialogLoadingBinding.inflate(layoutInflater)

    /**
     * 初始化布局组件
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        loadingDrawable = LoadingDrawable(
            context.resources.getDrawable(R.drawable.ic_loading_sun),
            context.resources.getDrawable(R.drawable.ic_loading_cloud)
        )
        mBinding?.ivLoading?.setImageDrawable(loadingDrawable)
    }

    /**
     * 增加按钮点击事件
     */
    override fun initEvent() {
    }

    fun setTip(tip: String?) {
        if (!tip.isNullOrEmpty()) {
            mBinding?.tvLoadingTip?.text = tip
        }
    }

    override fun show() {
        super.show()
        if (loadingDrawable != null) {
            (loadingDrawable as Animatable).start()
        }
    }

    override fun dismiss() {
        super.dismiss()
        if (loadingDrawable != null) {
            (loadingDrawable as Animatable).stop()
        }
    }

}