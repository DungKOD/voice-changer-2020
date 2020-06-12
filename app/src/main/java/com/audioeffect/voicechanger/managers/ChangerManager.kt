package com.audioeffect.voicechanger.managers

import android.annotation.SuppressLint
import android.os.*
import com.audioeffect.voicechanger.bridge.VoicePlayUtil
import com.audioeffect.voicechanger.bridge.VoiceUtil

class ChangerManager(
    mPath: String
) {
    var mVoicePlayUtil = VoicePlayUtil()
    private var callBackPath: InitPathCallback? = null

    companion object {
        const val WHAT_INIT_AUDIO = 1
    }

    init {
        InitSoundProgress().execute(mPath)
    }

    @SuppressLint("StaticFieldLeak")
    inner class InitSoundProgress : AsyncTask<String?, Void?, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Int {
            return VoiceUtil.init(params[0])
        }

        override fun onPostExecute(result: Int) {
            callBackPath?.initSuccess(result)
        }
    }

    fun playEffect(effectID: Int) {
        mVoicePlayUtil.playVoice(effectID)
    }

    fun pauseEffect(isPause: Boolean) {
        VoiceUtil.pause(isPause)
    }

    fun stopEffect() {
        mVoicePlayUtil.stopVoice()
    }

    fun isPlaying(boolean: Boolean) {
        mVoicePlayUtil.isPlaying = boolean
    }

    fun setCallback(callback: InitPathCallback): ChangerManager {
        callBackPath = callback
        return this
    }

    interface InitPathCallback {
        fun initSuccess(value: Int)
    }
}