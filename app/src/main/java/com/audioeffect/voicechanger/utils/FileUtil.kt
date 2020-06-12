package com.audioeffect.voicechanger.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.audioeffect.voicechanger.item.ItemGallery
import com.audioeffect.voicechanger.utils.Constans.VOICE_CHANGER_FOLDER
import java.io.File
import java.lang.Exception
import java.util.*


object FileUtil {
    private val TAG = FileUtil::class.java.simpleName
    private const val SLASHES = "/"
    private var sPackageFilesDirectory: String? = null
    private var sStoragePath: String? = null

    private fun getDefaultFolderPath(context: Context?): String? {
        Log.d("getDefaultFolderPath", "getDefaultFolderPath=")
        if (sStoragePath == null) {
            sStoragePath =
                Environment.getExternalStorageDirectory().toString() + File.separator + VOICE_CHANGER_FOLDER
            val file = File(sStoragePath)
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    sStoragePath = getPathInPackage(context, true)
                }
            }
        }
        return sStoragePath
    }

    @SuppressLint("SetWorldWritable", "SetWorldReadable")
    private fun getPathInPackage(context: Context?, grantPermissions: Boolean): String? {
        if (context == null || sPackageFilesDirectory != null) return sPackageFilesDirectory
        val path = context.filesDir.toString() + "/" + VOICE_CHANGER_FOLDER
        val file = File(path)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "Create package dir of VoiceChanger failed!")
                return null
            }
            if (grantPermissions) {
                if (file.setExecutable(true, false)) {
                    Log.i(TAG, "Package folder is executable")
                }
                if (file.setReadable(true, false)) {
                    Log.i(TAG, "Package folder is readable")
                }
                if (file.setWritable(true, false)) {
                    Log.i(TAG, "Package folder is writable")
                }
            }
        }
        sPackageFilesDirectory = path
        return sPackageFilesDirectory
    }

    fun getAllItemInGallery(context: Context): ArrayList<ItemGallery> {
        val itemGalleries = ArrayList<ItemGallery>()
        val uri = MediaStore.Files.getContentUri("external")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val projection = arrayOf("_data", "_display_name", "title", "duration")
            val selection = "_data like ? "
            val sortOrder = "date_modified" + " DESC"
            val cursor = context.contentResolver.query(
                uri,
                projection,
                selection,
                arrayOf("%" + getDefaultFolderPath(context) + SLASHES + "%"),
                sortOrder
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getColumnIndex("id")
                    val dataIndex = cursor.getColumnIndex("_data")
                    val displayNameIndex = cursor.getColumnIndex("_display_name")
                    val titleIndex = cursor.getColumnIndex("title")
                    val durationIndex = cursor.getColumnIndex("duration")
                    if (cursor.getString(displayNameIndex) != null) {
                       try {
                           val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                       }catch (e:Exception){}

                        itemGalleries.add(
                            ItemGallery(
                                id,
                                cursor.getString(dataIndex)
                                , cursor.getString(displayNameIndex)
                                , cursor.getString(titleIndex)
                                , 0
                                , cursor.getString(durationIndex)
                            )
                        )
                    }
                }
                cursor.close()
            } else {
                Log.e(TAG, "Cursor is null!")
            }
        } else {
            val projection = arrayOf("*")
            val selection = "_data like ? "
            val sortOrder = "date_modified" + " DESC"
            val cursor = context.contentResolver.query(
                uri,
                projection,
                selection,
                arrayOf("%" + getDefaultFolderPath(context) + SLASHES + "%"),
                sortOrder
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val type = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    val typeNol = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE.toString())
                    val id = cursor.getColumnIndex("_id")
                    val dataIndex = cursor.getColumnIndex("_data")
                    val displayNameIndex = cursor.getColumnIndex("_display_name")
                    val titleIndex = cursor.getColumnIndex("title")
                    val descriptionIndex = cursor.getColumnIndex("description")
                    val durationIndex = cursor.getColumnIndex("duration")

                    if (cursor.getString(displayNameIndex) != null) {
                        if (cursor.getString(displayNameIndex) != Constans.AudioName + Constans.TYPE_MP3 &&
                            cursor.getInt(type) != 0
                        ) {
                            val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                            itemGalleries.add(
                                ItemGallery(
                                    id,
                                    cursor.getString(dataIndex)
                                    , cursor.getString(displayNameIndex)
                                    , cursor.getString(titleIndex)
                                    , cursor.getInt(descriptionIndex)
                                    , cursor.getString(durationIndex)
                                )
                            )
                        }
                    }
                }
                cursor.close()
            } else {
                Log.e(TAG, "Cursor is null!")
            }
        }
        return itemGalleries
    }

    fun notifyNewFile(context: Context, outFile: String, effectID: Int) {
        MediaScannerConnection.scanFile(context, arrayOf(outFile), null) { _, _ -> updateMetaDataFile(context, outFile, effectID) }
    }

    fun getNameOrigin(): String {
        return UtilView.sdf().format(Date(System.currentTimeMillis())) + Constans.TYPE_MP3
    }

    private fun updateMetaDataFile(context: Context, path: String, effectID: Int): Int {
        var result = -1
        val projection = arrayOf("_id")
        val selection = "_data = ?"
        val selectionArgs = arrayOf(path)
        val queryUri = MediaStore.Files.getContentUri("external")
        val contentResolver = context.contentResolver
        val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)!!
        if (c.moveToFirst()) {
            val id = c.getLong(c.getColumnIndexOrThrow("_id"))
            val updateUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
            val contentValues = ContentValues()
            contentValues.put("description", effectID)
            result = contentResolver.update(updateUri, contentValues, "_id=?", arrayOf(id.toString()))
        }
        c.close()
        return result
    }


    fun notifyEdit(context: Context, outFile: String, effectID: Int, name: String) {
        MediaScannerConnection.scanFile(context, arrayOf(outFile), null) { _, _ -> updateEdit(context, outFile, effectID, name) }
    }

    private fun updateEdit(context: Context, path: String, effectID: Int, name: String): Int {
        var result = -1
        val projection = arrayOf("_id")
        val selection = "_data = ?"
        val selectionArgs = arrayOf(path)
        val queryUri = MediaStore.Files.getContentUri("external")
        val contentResolver = context.contentResolver
        val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)!!
        if (c.moveToFirst()) {
            val id = c.getLong(c.getColumnIndexOrThrow("_id"))
            val updateUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
            val contentValues = ContentValues()
            contentValues.put("_display_name", name)
            contentValues.put("description", effectID)
            result = contentResolver.update(updateUri, contentValues, "_id=?", arrayOf(id.toString()))
        }
        c.close()
        return result
    }

    fun deleteFile(path: String?): Int {
        var result = -1
        val fDelete = File(path)
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                result = 1
            }
        }
        return result
    }

    @SuppressLint("Recycle")
    fun deleteMediaStoreFile(context: Context, path: String): Int {
        var result = -1
        val projection = arrayOf("_id")
        val selection = "_data = ?"
        val selectionArgs = arrayOf(path)
        val queryUri = MediaStore.Files.getContentUri("external")
        val contentResolver = context.contentResolver
        val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)!!
        if (c.moveToFirst()) {
            val id = c.getLong(c.getColumnIndexOrThrow("_id"))
            val deleteUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
            result = contentResolver.delete(deleteUri, null, null)
        }
        c.close()
        return result
    }
}