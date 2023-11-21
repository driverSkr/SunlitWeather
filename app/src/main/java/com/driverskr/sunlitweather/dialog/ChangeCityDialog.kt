package com.driverskr.sunlitweather.dialog

import android.content.Context
import com.driverskr.lib.dialog.BaseDialog
import com.driverskr.sunlitweather.databinding.DialogChangeCityBinding

/**
 * 预警Dialog
 */
class ChangeCityDialog(context: Context) : BaseDialog<DialogChangeCityBinding>(context, 0.66f, 0f) {

    var mListener: (() -> Unit)? = null

    override fun bindView() = DialogChangeCityBinding.inflate(layoutInflater)

    override fun initView() {
        setCanceledOnTouchOutside(true)
    }

    fun setContent(content: String) {
        mBinding.tvContent.text = content
    }

    override fun initEvent() {
        mBinding.tvCancel.setOnClickListener { dismiss() }
        mBinding.tvConfirm.setOnClickListener {
            mListener?.invoke()
            dismiss()
        }
    }

    fun setOnConfirmListener(listener: () -> Unit) {
        mListener = listener
    }
}