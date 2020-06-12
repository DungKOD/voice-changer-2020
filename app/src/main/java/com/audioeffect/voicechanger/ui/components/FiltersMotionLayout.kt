package com.audioeffect.voicechanger.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.managers.AudioRecorder
import com.audioeffect.voicechanger.ui.activities.MainActivity
import com.recodereffect.voicechanger.utils.bindView
import com.recodereffect.voicechanger.views.component.MultiListenerMotionLayout
import kotlinx.coroutines.launch

class FiltersMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MultiListenerMotionLayout(context, attrs, defStyleAttr) {

    /////////////// BIND VIEW ////////////////////

    private val closeIcon: ImageView by bindView(R.id.close_icon)
    private val filterIcon: ImageView by bindView(R.id.filter_icon)
    private val btnOpenFile: ImageView by bindView(R.id.btnOpenFile)
    private val line: RelativeLayout by bindView(R.id.line)

    ///////////////// INIT /////////////////////
    private lateinit var audioRecorder: AudioRecorder

    private var isRecorder = false


    private lateinit var onCallbackListener: OnCallbackListener

    init {
        inflate(context, R.layout.layout_filter_motion, this)

        enableClicks()
    }

    private fun openSheet(): Unit = performAnimation {

        setTransition(R.id.set1_base, R.id.set2_path)
        
        transitionToState(R.id.set2_path)
        startScaleDownAnimator(true)
        awaitTransitionComplete(R.id.set2_path)

        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)

        transitionToState(R.id.set4_settle)
        awaitTransitionComplete(R.id.set4_settle)
    }

    private fun closeSheet(): Unit = performAnimation {

        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)

        setTransition(R.id.set2_path, R.id.set3_reveal)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)

        setTransition(R.id.set1_base, R.id.set2_path)
        progress = 1f
        transitionToStart()
        startScaleDownAnimator(false)
        awaitTransitionComplete(R.id.set1_base)
    }

    private fun recorderSheet(): Unit = performAnimation {



    }

    private fun enableClicks() = when (currentState) {

        R.id.set1_base -> {

            filterIcon.setOnClickListener {
                openSheet()
            }

            closeIcon.setOnClickListener(null)
        }

        R.id.set4_settle -> {

            closeIcon.setOnClickListener {
                audioRecorder.stopRecorder(line)
                closeSheet()
            }

            filterIcon.setOnClickListener {
                recorder()

            }

            btnOpenFile.setOnClickListener {
                onCallbackListener.onClickFolder()
            }
        }

        else -> throw IllegalStateException("Chỉ có thể được gọi cho 3 currentStates được phép")
    }

    private fun disableClicks() {

        filterIcon.setOnClickListener(null)
        closeIcon.setOnClickListener(null)

    }

    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
            disableClicks()
            block()
            enableClicks()
        }
    }


    private fun startScaleDownAnimator(isScaledDown: Boolean): Unit =
        (context as MainActivity)
            .getAdapterScaleDownAnimator(isScaledDown)
            .apply { duration = transitionTimeMs }
            .start()

    fun setOnCallbackListener(onCallbackListener: OnCallbackListener) {
        this.onCallbackListener = onCallbackListener

    }

    fun setAudioRecorder(audioRecorder: AudioRecorder) {
        this.audioRecorder = audioRecorder
    }


    private fun recorder() {
        isRecorder = if (!isRecorder) {
            audioRecorder.startRecorder(line, "asasas.mp3")
            true
        } else {
            audioRecorder.stopRecorder(line)
            false
        }
    }

    interface OnCallbackListener {
        fun onClickFolder()
    }
}