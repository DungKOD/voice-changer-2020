package com.audioeffect.voicechanger.task

import android.os.AsyncTask
import com.audioeffect.voicechanger.entities.DetailAudio
import java.io.File
import java.lang.Exception

class ReadAudioPath(private val callback: ReadPathCallback) : AsyncTask<String, Void, ArrayList<DetailAudio>>() {
    override fun doInBackground(vararg params: String?): ArrayList<DetailAudio> {
        return params[0]?.let { readData(it) }!!
    }

    private fun readData(path: String): ArrayList<DetailAudio> {
        val detailAudio = ArrayList<DetailAudio>()
        try {
            val file = File(path)
            detailAudio.add(DetailAudio(file.name))
        } catch (e: Exception) {
            print(e)
        }
        return detailAudio
    }

    override fun onPostExecute(result: ArrayList<DetailAudio>?) {
        super.onPostExecute(result)
        result?.let {
            callback.success(result)
        }
    }

    interface ReadPathCallback {
        fun success(detailAudio: ArrayList<DetailAudio>)
    }
}