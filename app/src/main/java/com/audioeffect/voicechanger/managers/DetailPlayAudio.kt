package com.audioeffect.voicechanger.managers

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Handler
import com.audioeffect.voicechanger.event.SeeBarEvent
import com.audioeffect.voicechanger.ui.activities.EffectActivity
import com.audioeffect.voicechanger.utils.Constans
import com.audioeffect.voicechanger.utils.UtilView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import kotlin.Exception

class DetailPlayAudio(private val context: Context, private val progressView: ProgressView) : DetailCallback, OnCompletionListener {

    private var mMediaPlayer: MediaPlayer? = null
    private var mDurationHandler = Handler()
    private var thread: Thread? = null
    private var uriDetail: Uri? = null
    private var timeSleep = 0L
    private var timeStop = 0L
    private var startTime = 0L
    private var timeView = 0L

    companion object {
        var isComplete = false
    }

    override fun editAudio(path: String, effectID: Int, nameAudio: String, idFile: Int) {
        getDataEdit(path, effectID, nameAudio, idFile)
    }

    private fun getDataEdit(path: String, effectID: Int, nameAudio: String, idFile: Int) {
        var effId = effectID
        var name = nameAudio
        try {
            name = nameAudio.substring(nameAudio.lastIndexOf("_") - 8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var pathDefault = UtilView.getPathCache(name)

        val file = File(pathDefault)
        if (!file.exists()) {
            pathDefault = path
        }
        intentToEffect(pathDefault, effId, name, nameAudio, idFile)
    }

    override fun reNameAudio(nameAudio: String, path: String, effectID: Int) {

    }

    override fun pauseAudio() {
        mMediaPlayer?.pause()
        timeStop = startTime
        mDurationHandler.removeCallbacks(mUpdateSeekBarTime())
    }

    override fun stopAudio() {
        try {
            mMediaPlayer?.stop()
            killThread()
            isComplete = true
            mDurationHandler.removeCallbacks(mUpdateSeekBarTime())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun playAudio(uri: Uri) {
        timeSleep = 0
        uriDetail = uri
        mMediaPlayer?.reset()
        mMediaPlayer = MediaPlayer.create(context, uri)
        mMediaPlayer?.start()
        mMediaPlayer?.setOnCompletionListener(this)
        isComplete = false
        timePlayAudio()
        mDurationHandler.postDelayed(mUpdateSeekBarTime(), 100)
    }

    override fun infoAudio(path: String) {

    }

    override fun shareAudio(path: String) {
        UtilView.shareFile(context, path)
    }

    override fun resumeAudio(uri: Uri?) {
        try {
            mMediaPlayer?.start()
            timeView = timeSleep - timeStop
            mDurationHandler.postDelayed(mUpdateSeekBarTime(), 100)
            uri?.let {
                if (mMediaPlayer == null && uri.toString().isNotEmpty()) {
                    timeView = 0
                    playAudio(uri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detailAudio(nameAudio: String, path: String, effectID: Int, idAudio: Int) {

    }

    override fun deleteAudio(path: String, name: String) {

    }

    override fun isPlaying(b: Boolean) {
        mMediaPlayer?.let {
            if (b) {
                stopAudio()
                progressView.setPlay(false)
            } else {
                uriDetail?.let {
                    playAudio(uriDetail!!)
                    progressView.setPlay(true)
                }
            }
        }
    }

    private fun mUpdateSeekBarTime(): Runnable = object : Runnable {
        override fun run() {
            try {
                if (mMediaPlayer?.isPlaying!!) {
                    val process = mMediaPlayer?.currentPosition!! * 1000 / mMediaPlayer!!.duration
                    progressView.progress(process)
                    mDurationHandler.postDelayed(this, 0)
                }
            } catch (e: Exception) {
                print(e)
            }
        }
    }

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe
    fun seekTo(event: SeeBarEvent) {
        val processA: Double = event.progress / 1000.toDouble()
        val duration = mMediaPlayer!!.duration
        val processUpdate = processA * duration
        mMediaPlayer?.seekTo(processUpdate.toInt())
    }

    interface ProgressView {
        fun progress(process: Int)
        fun onCompletion()
        fun onTimePay(timeStart: Long)
        fun setPlay(b: Boolean)
        fun onStopAudio()
        fun onFinish()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mDurationHandler.removeCallbacks(mUpdateSeekBarTime())
        progressView.onCompletion()
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
        killThread()
        isComplete = true
    }

    private fun intentToEffect(
        path: String,
        effectID: Int,
        nameAudio: String,
        nameAudio1: String,
        idFile: Int
    ) {
        val intent = Intent(context, EffectActivity::class.java)
        intent.putExtra(Constans.KEY_PATH, path)
        intent.putExtra(Constans.KEY_EFFECT_ID, effectID)
        intent.putExtra(Constans.KEY_EFFECT, Constans.KEY_EDIT)
        intent.putExtra(Constans.NAME_EDIT, nameAudio1)
        intent.putExtra(Constans.ID_AUDIO, idFile)
        intent.putExtra(Constans.KEY_NAME_DEFAULT, nameAudio)
        context.startActivity(intent)
    }

    private fun timePlayAudio() {
        thread = Thread {
            while (!isComplete) {
                timeSleep++
                startTime = timeSleep - timeView
                if (timeSleep == Long.MAX_VALUE) {
                    timeSleep = 0L
                }
                timeAudio(startTime)
                try {
                    Thread.sleep(1000L)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        thread?.start()
    }

    private fun timeAudio(timeStart: Long) {
        try {
            if (mMediaPlayer?.isPlaying!!) {
                progressView.onTimePay(timeStart)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun killThread() {
        thread?.interrupt()
        thread = null
    }
}
