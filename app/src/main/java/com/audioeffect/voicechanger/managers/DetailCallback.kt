package com.audioeffect.voicechanger.managers

import android.net.Uri

interface DetailCallback {
    fun editAudio(path: String, effectID: Int, nameAudio: String, idFile: Int)
    fun reNameAudio(
        nameAudio: String,
        path: String,
        effectID: Int
    )
    fun pauseAudio()
    fun stopAudio()
    fun playAudio(uri: Uri)
    fun infoAudio(path: String)
    fun shareAudio(path: String)
    fun resumeAudio(uri: Uri?)
    fun detailAudio(nameAudio: String, path: String, effectID: Int, idAudio: Int)
    fun deleteAudio(path:String,name:String)
    fun isPlaying(b:Boolean)

}