package com.audioeffect.voicechanger.task

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import com.audioeffect.voicechanger.item.ItemFolder
import com.audioeffect.voicechanger.utils.UtilView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReadFileFolder(private val context: Context, private val callback: FileCallback) : AsyncTask<String, Void, ArrayList<ItemFolder>>() {
    companion object {
        val MAIN_STORAGE: String = Environment.getExternalStorageDirectory().absolutePath
        const val MP3 = "mp3"
    }

    var typeItem = ""

    private var listFolder = ArrayList<ItemFolder>()

    override fun doInBackground(vararg params: String?): ArrayList<ItemFolder> {
        return readFile(params[0])
    }

    override fun onPostExecute(result: ArrayList<ItemFolder>?) {
        super.onPostExecute(result)
        if (result != null) {
            val sortedAppsList: List<ItemFolder> = result.sortedBy { it.name?.toLowerCase(Locale.ROOT) }
            callback.onSuccess(sortedAppsList)
        }
    }

    private fun readFile(path: String?): ArrayList<ItemFolder> {
        if (UtilView.sItemFolders.isNotEmpty()) {
            UtilView.sItemFolders.clear()
            if (MAIN_STORAGE != path) {
                val f = File(path)
                if (f.parentFile != null) {
                    typeItem = "PARENT_FOLDER"
                    UtilView.sItemFolders.add(ItemFolder(f.parentFile.path))
                }
            }
        }

        val f = File(path)

        if (f.exists()) {
            val files = f.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (inFile in files) {
                    if (inFile.isDirectory) {
                        if (!isFolderHide(inFile.name)) {
                            typeItem = "FOLDER"
                            UtilView.sItemFolders.add(
                                ItemFolder(
                                    inFile.path,
                                    inFile.name,
                                    getChildCount(inFile),
                                    formatDate(inFile.lastModified())
                                )
                            )
                        }
                    }
                    if (inFile.isFile && isMP3File(inFile.path)) {
                        var millSecond = 0
                        try {
                            val uri = Uri.parse(inFile.path)
                            val mmr = MediaMetadataRetriever()
                            mmr.setDataSource(context, uri)
                            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            millSecond = Integer.parseInt(durationStr)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (!isFolderHide(inFile.name)) {
                            typeItem = "FILE"
                            UtilView.sItemFolders.add(ItemFolder(inFile.path, inFile.name, UtilView.formatTime(millSecond)))
                        }
                    }
                }
            }
        }

        return UtilView.sItemFolders
    }

    private fun isMP3File(fileName: String?): Boolean {
        var extension: String? = null
        if (fileName != null) {
            val filenameArray = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            extension = filenameArray[filenameArray.size - 1]
        }
        return MP3 == extension
    }


    private fun isFolderHide(name: String): Boolean {
        if (name.startsWith(".")) {
            return true
        }

        return false
    }

    private fun getChildCount(inFile: File): Int {
        var count = 0
        val files = inFile.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (!file.isHidden) {
                    if (file.isDirectory) {
                        count++
                    }
                    if (file.isFile && isMP3File(file.path)) {
                        count++
                    }
                }
            }
        }
        return count
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: Long): String {
        val spf = SimpleDateFormat("MM/dd/yyyy HH:mm")
        return spf.format(Date(date))
    }

    interface FileCallback {
        fun onSuccess(listFolder: List<ItemFolder>)
    }
}