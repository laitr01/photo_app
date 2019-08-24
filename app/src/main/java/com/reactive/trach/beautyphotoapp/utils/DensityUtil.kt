package com.reactive.trach.beautyphotoapp.utils

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import com.reactive.trach.beautyphotoapp.PhotoApplication

class DensityUtil {

    companion object {

        fun setViewMargin(view: View, isDp: Boolean, left: Int, right: Int, top: Int, bottom: Int): ViewGroup.LayoutParams? {
            var leftPx = left
            var rightPx = right
            var topPx = top
            var bottomPx = bottom
            val params = view.layoutParams
            var marginParams: ViewGroup.MarginLayoutParams? = null
            //Nhận thông số cài đặt lề của chế độ xem
            marginParams = if (params is ViewGroup.MarginLayoutParams) {
                params
            } else {
                //Tạo một tham số mới khi nó không tồn tại
                ViewGroup.MarginLayoutParams(params)
            }

            if (isDp) {
                leftPx = dip2px(view.context.resources, left.toFloat())
                rightPx = dip2px(view.context.resources,right.toFloat())
                topPx = dip2px(view.context.resources,top.toFloat())
                bottomPx = dip2px(view.context.resources,bottom.toFloat())
            }

            marginParams.setMargins(leftPx, topPx, rightPx, bottomPx)
            view.layoutParams = marginParams
            view.requestLayout()
            return marginParams
        }

        /**
         * DIP TO PIXEL
         */
        fun dip2px(resources: Resources, dpValue: Float): Int {
            val scale = resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}