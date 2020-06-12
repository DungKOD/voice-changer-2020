package com.audioeffect.voicechanger.base.animation

import android.graphics.Color


class AnimatedColor(private val mStartColor: Int, private val mEndColor: Int) {
    private val mStartHSV: FloatArray
    private val mEndHSV: FloatArray
    private val mMove = FloatArray(3)
    fun with(delta: Float): Int {
        if (delta <= 0) return mStartColor
        return if (delta >= 1) mEndColor else Color.HSVToColor(move(delta))
    }

    private fun move(delta: Float): FloatArray {
        mMove[0] = (mEndHSV[0] - mStartHSV[0]) * delta + mStartHSV[0]
        mMove[1] = (mEndHSV[1] - mStartHSV[1]) * delta + mStartHSV[1]
        mMove[2] = (mEndHSV[2] - mStartHSV[2]) * delta + mStartHSV[2]
        return mMove
    }

    private fun toHSV(color: Int): FloatArray {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv
    }

    init {
        mStartHSV = toHSV(mStartColor)
        mEndHSV = toHSV(mEndColor)
    }


}