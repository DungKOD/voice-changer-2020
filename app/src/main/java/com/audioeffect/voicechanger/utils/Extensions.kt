package com.recodereffect.voicechanger.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Toast

inline fun getValueAnimator(

    forward: Boolean = true,
    duration: Long,
    interpolator: TimeInterpolator,
    crossinline updateListener: (progress: Float) -> Unit
): ValueAnimator {
    val a =
        if (forward) ValueAnimator.ofFloat(0f, 1f)
        else ValueAnimator.ofFloat(1f, 0f)
    a.addUpdateListener { updateListener(it.animatedValue as Float) }
    a.duration = duration
    a.interpolator = interpolator
    return a
}

fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio

    val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
    val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
    val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
    val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

inline val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

inline val Context.screenWidth: Int
    get() = Point().also {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(
            it
        )
    }.x

inline val View.screenWidth: Int
    get() = context!!.screenWidth

fun View.setScale(scale: Float) {
    this.scaleX = scale
    this.scaleY = scale
}

inline fun Context.toast(message: String = "") {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}

fun updateTime(currentMillisecond: Int, totalMilliSecond: Int): String {
    val timeDisplay = totalMilliSecond - currentMillisecond

    val second = timeDisplay / 1000
    val hh = second / 3600
    val mm = second % 3600 / 60
    val ss = second % 60
    val total = (totalMilliSecond / 1000) % 60

    var str = ""
    str = (return if (hh != 0) {

        String.format("%02d:%02d:%02d", hh, mm, ss)

    } else {
        String.format("%02d:%02d", mm, ss)
    })

    //  update ở đây
}

fun maxTime(totalMilliSecond: Int): String {
    val second = totalMilliSecond / 1000
    val hh = second / 3600
    val mm = second % 3600 / 60
    val ss = second % 60
    var str = ""
    str = (return if (hh != 0) {

        String.format("%02d:%02d:%02d", hh, mm, ss)

    } else {
        String.format("%02d:%02d", mm, ss)
    })
}

inline fun clickDelay100(noinline onClick: () -> Unit) {
    Handler().postDelayed({
        onClick()
    }, 100)

}