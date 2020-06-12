package com.audioeffect.voicechanger.ui.components

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.github.squti.androidwaverecorder.WaveRecorder
import kotlin.collections.ArrayList
import kotlin.math.log10


class WaveManager {

    companion object {
        const val INTERVAL_DRAW_CHILD = 30L
        const val INTERVAL_MOVE = 3L
        const val INTERVAL_INCREMENT = 2L
        const val MAX_HEIGHT = 200
        const val MAX_AMPLITUDE = 32767.0
        const val RATIO_DB_HEIGHT = 4
        const val MAX_DB = 190
    }

    private val views = ArrayList<ViewChild>()
    private var db = 0.0
    private var numBerView = 0
    private var thread: Thread? = null
    private var isAddView = true

    private var timeSleep = 0L
    fun draw(
        activity: Activity, viewGroup: ViewGroup,
        waveRecorder: WaveRecorder?
    ) {
        thread = Thread {
            while (true) {
                activity.runOnUiThread {
                    if (isAddView) {
                        timeSleep++
                        if (timeSleep == Long.MAX_VALUE) {
                            timeSleep = 0L
                        }

                        removeChild(viewGroup)
                        waveRecorder?.onAmplitudeListener = {
                            db = 20 * log10(it / MAX_AMPLITUDE) * RATIO_DB_HEIGHT

                            if (db < -MAX_HEIGHT) {
                                db = (-MAX_HEIGHT).toDouble()
                            }
                        }

                        drawViewChild(viewGroup, timeSleep, db)

                        moveChild(timeSleep)

                        incrementHeight(timeSleep)
                    }
                }

                try {
                    Thread.sleep(3L)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        thread?.start()
    }

    fun stopThread() {
        thread?.interrupt()
        thread = null
        numBerView = 0
    }

    @Synchronized
    private fun removeChild(viewGroup: ViewGroup) {
        for (i in views.indices) {
            try {
                val view = views[i]
                if (view.isEnd()) {
                    viewGroup.removeView(view)
                    views.remove(view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    private fun incrementHeight(timeSleep: Long) {
        if (thread != null) {
            if (timeSleep % INTERVAL_INCREMENT != 0L) return
            for (view in views) {
                view.incrementHeight()
            }
        }
    }

    @Synchronized
    private fun moveChild(timeSleep: Long) {
        if (thread != null) {
            if (timeSleep % INTERVAL_MOVE != 0L) return

            for (view in views) {
                view.move()
            }
        }
    }

    private var view = 0
    @Synchronized
    private fun drawViewChild(
        viewGroup: ViewGroup,
        timeSleep: Long,
        db: Double
    ) {
        if (thread != null) {
            if (timeSleep % INTERVAL_DRAW_CHILD != 0L) return
            val amplitude = MAX_HEIGHT + db

            if (numBerView < 2) {
                numBerView++
            } else {
                if (amplitude - view >= 10) {
                    view = amplitude.toInt()
                    if(view<10){
                        view =10
                    }
                    val viewChild = ViewChild(viewGroup.context, viewGroup.width, (amplitude).toInt())
                    viewGroup.addView(viewChild)
                    views.add(viewChild)

                    Log.d("amplitude","view=$view")
                } else {
                    val a = 10 - (amplitude - view)
                    view = (10 - a + amplitude).toInt()
                    if (view > 200) {
                        view = 200
                    }

                    if(view<10){
                        view =10
                    }
                    Log.d("c","view=$view")

                    val viewChild = ViewChild(viewGroup.context, viewGroup.width, (view).toInt())
                    viewGroup.addView(viewChild)
                    views.add(viewChild)
                }
            }
        }
    }

    fun checkAddView(isDraw: Boolean) {
        isAddView = isDraw
    }
}