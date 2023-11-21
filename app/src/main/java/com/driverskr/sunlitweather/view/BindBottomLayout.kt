package com.driverskr.sunlitweather.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.driverskr.sunlitweather.utils.ContentUtil

/**
 * @Author: driverSkr
 * @Time: 2023/11/21 14:14
 * @Description: $
 */
class BindBottomLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr){
    private var mTotalLength = 0
    private var mExtentHeight = 0

    private var firstViewCnt = 3

    init {
        firstViewCnt = if (ContentUtil.screenHeight >= 2300) 4 else 3
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mTotalLength = 0
        var top3Height = 0
        var maxWidth = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val usedHeight = mTotalLength
            measureChildWithMargins(
                child, widthMeasureSpec, 0,
                heightMeasureSpec, usedHeight
            )
            val childHeight = child.measuredHeight
            val lp = child.layoutParams as LayoutParams
            mTotalLength += childHeight + lp.topMargin + lp.bottomMargin
            val margin = lp.leftMargin + lp.rightMargin
            val measuredWidth = child.measuredWidth + margin
            maxWidth = maxWidth.coerceAtLeast(measuredWidth)
            if (i <= firstViewCnt) {
                top3Height += childHeight + lp.topMargin + lp.bottomMargin
            }
        }

        mTotalLength += paddingTop + paddingBottom

        // 计算扩展高度，以保持前两个item保持在屏幕底部
        mExtentHeight = ContentUtil.visibleHeight - top3Height
        if (mExtentHeight > 0) {
            mTotalLength += mExtentHeight
        }
        maxWidth += paddingLeft + paddingRight
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(mTotalLength, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var currentY = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            currentY += lp.topMargin
            child.layout(
                lp.leftMargin,
                currentY,
                lp.leftMargin + child.measuredWidth,
                currentY + child.measuredHeight
            )
            currentY += child.measuredHeight + lp.bottomMargin
            if (i == 0 && mExtentHeight > 0) {
                currentY += mExtentHeight
            }
        }
    }
}