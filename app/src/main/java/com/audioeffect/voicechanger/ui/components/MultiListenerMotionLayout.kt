package com.recodereffect.voicechanger.views.component

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resume


open class MultiListenerMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    private val listeners = CopyOnWriteArrayList<TransitionListener>()

    init {
        super.setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(
                motionLayout: MotionLayout,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                listeners.forEach {

                    it.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                }
            }

            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {
                listeners.forEach {
                    it.onTransitionStarted(motionLayout, startId, endId)
                }
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                listeners.forEach {
                    it.onTransitionChange(motionLayout, startId, endId, progress)
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                listeners.forEach {
                    it.onTransitionCompleted(motionLayout, currentId)
                }
            }
        })
    }


    suspend fun awaitTransitionComplete(transitionId: Int, timeout: Long = 1000L) {

        var listener: TransitionListener? = null

        try {
            withTimeout(timeout) {
                suspendCancellableCoroutine<Unit> { continuation ->
                    val l = object : TransitionAdapter() {
                        override fun onTransitionCompleted(
                            motionLayout: MotionLayout,
                            currentId: Int
                        ) {
                            if (currentId == transitionId) {
                                removeTransitionListener(this)
                                continuation.resume(Unit)
                            }
                        }
                    }

                    continuation.invokeOnCancellation {
                        removeTransitionListener(l)
                    }

                    addTransitionListener(l)
                    listener = l
                }
            }

        } catch (tex: TimeoutCancellationException) {

            listener?.let(::removeTransitionListener)
            throw CancellationException(
                "Transition to state with id: $transitionId did not" +
                        " complete in timeout.", tex
            )
        }
    }

    fun addTransitionListener(listener: TransitionListener) {
        listeners.addIfAbsent(listener)
    }

    fun removeTransitionListener(listener: TransitionListener) {
        listeners.remove(listener)
    }

    @Deprecated(
        message = "Use addTransitionListener instead", replaceWith = ReplaceWith(
            "addTransitionListener(listener)",
            "com.dungnv.voidrecoder.views.component.MultiListenerMotionLayout.addTransitionListener"
        )
    )
    override fun setTransitionListener(listener: TransitionListener) {
        throw IllegalArgumentException("Use addTransitionListener instead")
    }
}