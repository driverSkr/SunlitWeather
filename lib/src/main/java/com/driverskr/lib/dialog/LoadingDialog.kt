package com.driverskr.lib.dialog

import android.content.Context
import com.driverskr.lib.databinding.DialogLoadingBinding
import com.driverskr.lib.view.LoadingDrawable

/**
 * @Author: driverSkr
 * @Time: 2023/11/15 11:48
 * @Description: 自定义加载进度对话框$
 */
class LoadingDialog(context: Context): BaseDialog<DialogLoadingBinding?>(context, 0.38f, 0f) {
    var loadingDrawable: LoadingDrawable? = null

    override fun bindView(): DialogLoadingBinding? {
        TODO("Not yet implemented")
    }

    /**
     * 初始化布局组件
     */
    override fun initView() {
        TODO("Not yet implemented")
    }

    /**
     * 增加按钮点击事件
     */
    override fun initEvent() {
        TODO("Not yet implemented")
    }

}