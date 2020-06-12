package com.audioeffect.voicechanger.managers

import android.app.Activity
import android.os.Environment
import android.view.ViewGroup
import com.audioeffect.voicechanger.ui.components.WaveManager
import com.audioeffect.voicechanger.utils.Constans
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File


class AudioRecorder(private val activity: Activity) {
    private var waveRecorder: WaveRecorder? = null
    private var waveManager: WaveManager? = null

    private fun configRecorder(nameDefault: String) {
        val path =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + Constans.CACHE_FOLDER + File.separator
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val fullName = path + Constans.AudioName + Constans.TYPE_MP3
        val file = File(fullName)
        file.createNewFile()

        val filePath = path + nameDefault
        waveRecorder = WaveRecorder(filePath)
    }

    fun startRecorder(viewGroup: ViewGroup, nameDefault: String) {
        configRecorder(nameDefault)
        waveManager = WaveManager()
        waveRecorder?.startRecording()
        waveManager?.draw(activity, viewGroup, waveRecorder)
        waveRecorder?.onAmplitudeListener = {
        }
    }

    fun stopRecorder(viewGroup: ViewGroup) {
        waveRecorder?.stopRecording()
        waveManager?.stopThread()
        viewGroup.removeAllViews()
        waveManager = null
    }

    fun checkAddView(boolean: Boolean) {
        waveManager?.checkAddView(boolean)
    }
}