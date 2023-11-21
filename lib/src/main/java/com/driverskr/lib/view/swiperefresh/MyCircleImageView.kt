package com.driverskr.lib.view.swiperefresh

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView

/**
 * @Author: driverSkr
 * @Time: 2023/11/21 11:58
 * @Description: $
 */
class MyCircleImageView: AppCompatImageView {

    private var mListener: Animation.AnimationListener? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    fun setAnimationListener(listener: Animation.AnimationListener?) {
        mListener = listener
    }

    override fun onAnimationStart() {
        super.onAnimationStart()
        if (mListener != null) {
            mListener!!.onAnimationStart(animation)
        }
    }

    override fun onAnimationEnd() {
        super.onAnimationEnd()
        if (mListener != null) {
            mListener!!.onAnimationEnd(animation)
        }
    }
}