package com.audioeffect.voicechanger.ui.components

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout


class ViewChild(
    context: Context,
    private val xMax: Int,
    private val heightMax: Int
) : View(context) {

    private var isEnd = false

    init {
        x = xMax.toFloat()
        setBackgroundColor(Color.parseColor("#FFFFFF"))
    }

    private var params: RelativeLayout.LayoutParams? = null

    fun changeBackground() {
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams.width = pxFromDp(context, 2F).toInt()
        layoutParams.height = 0
        params = layoutParams as RelativeLayout.LayoutParams
        params?.addRule(RelativeLayout.CENTER_VERTICAL)
    }

    fun move() {
        if (x > 0) {
            x--
        } else {
            isEnd = true
        }
    }

    fun incrementHeight() {
        if (params == null) return
        if (height < heightMax) {

            val animationHeight = heightMax * 0.5
            if (height >= animationHeight) {
                params?.height = params?.height!! + 1
            } else {
                params?.height = params?.height!! + 3
            }
            layoutParams = params
        }
    }

    fun isEnd() = isEnd

    private fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }
}