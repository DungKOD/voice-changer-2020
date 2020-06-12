package com.audioeffect.voicechanger.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.audioeffect.voicechanger.BuildConfig
import com.audioeffect.voicechanger.MobileApp
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.item.ItemFolder
import com.bumptech.glide.Glide
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


object UtilView {
    val sItemFolders = ArrayList<ItemFolder>()

    fun getPath(nameFile: String): String {
        return Environment.getExternalStorageDirectory().toString() + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + Constans.CACHE_FOLDER + File.separator + nameFile
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun setStatusBarGradient(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            val background =
                activity.resources.getDrawable(R.drawable.view_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.navigationBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    fun setStatusBarWhite(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            val background =
                activity.resources.getDrawable(R.drawable.background_main)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.navigationBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    fun setStatusStep(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            val background =
                activity.resources.getDrawable(R.drawable.gradient_step)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.navigationBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    fun shareFile(context: Context, path: String) {
        try {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = Constans.TYPE_SHARE
            val fileToShare = File(path)
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".FileProvider",
                fileToShare
            )
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(sharingIntent, "Share audio"))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun goneView(view: View) {
        view.visibility = View.GONE
    }

    fun visibilityView(view: View) {
        view.visibility = View.VISIBLE
    }

    fun savePrefsPre(context: Context) {
        val pref = context.getSharedPreferences(Constans.MY_PREFS, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(Constans.IS_INTRO, true)
        editor.apply()
    }

    fun savePrefsAds(context: Context) {
        val pref = context.getSharedPreferences(Constans.KEY_PRICE_ADS, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(Constans.IS_INTRO, true)
        editor.apply()
    }

    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    fun createHiddenCacheFolder(): Boolean {
        val storagePath: String =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + Constans.CACHE_FOLDER + File.separator + Constans.NO_MEDIA_FILE
        val file = File(storagePath)
        return if (!file.exists()) {
            file.mkdirs()
        } else true
    }

    fun loadGlide(context: Context, resourceId: Int, view: ImageView) {
        Glide.with(context).load(resourceId).into(view)
    }

    fun setAnimation(view: View) {
        val animation = AlphaAnimation(0.3f, 1f)
        animation.duration = 200
        view.startAnimation(animation)
    }

    fun setAnimationPlay(view: View) {
        val animation = AlphaAnimation(0.3f, 1f)
        animation.duration = 200
        view.startAnimation(animation)
    }

    fun formatTime(time: Long): String? {
        val sdf = SimpleDateFormat("mm:ss")
        val date = Date(time)
        return sdf.format(date)
    }

    fun setAnimationBTN(view: View) {
        val animation = AlphaAnimation(0.3f, 1f)
        animation.duration = 250
        view.startAnimation(animation)
    }


    fun setAnimationText(view: View) {
        val animation = AlphaAnimation(0.0f, 1f)
        animation.duration = 500
        view.startAnimation(animation)
    }

    fun setAnimationHide(view: View) {
        val animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 200
        view.startAnimation(animation)
    }

    fun newFilePath(fileName: String): String? {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + Constans.CACHE_FOLDER + File.separator + fileName + Constans.TYPE_MP3
    }

    fun getPathAudioCache(): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + Constans.CACHE_FOLDER + File.separator + Constans.AudioName + Constans.TYPE_MP3
    }

    fun getPathCache(nameFile: String): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + Constans.CACHE_FOLDER + File.separator + nameFile
    }

    fun setPathNewFile(fileName: String): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + Constans.VOICE_CHANGER_FOLDER + File.separator + fileName + Constans.TYPE_MP3
    }

    @SuppressLint("InlinedApi")
    fun setStatusBarColor(context: Context, color: Int) {
        val window: Window = (context as Activity).window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(context, color)
        }
    }

    @SuppressLint("InlinedApi")
    fun setStatusBarColorAlpha(context: Context, color: Int) {
        Handler().postDelayed({
            val window: Window = (context as Activity).window
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(context, color)
            }
        }, 20)
    }

    fun savePrefsData(context: Context) {
        val pref = context.getSharedPreferences(Constans.RATE, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("isIntro", Constans.RATE)
        editor.apply()
    }

    fun formatTime(duration: Int): String {
        if (duration <= 0) {
            return "n/a"
        }
        return if (checkDurationToHour(duration)) {
            String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration.toLong()),
                TimeUnit.MILLISECONDS.toMinutes(duration.toLong()) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) % TimeUnit.MINUTES.toSeconds(1)
            )
        } else {
            String.format(
                "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) % TimeUnit.MINUTES.toSeconds(1)
            )
        }
    }

    private fun checkDurationToHour(duration: Int): Boolean {
        return TimeUnit.MILLISECONDS.toHours(duration.toLong()) > 0
    }

    fun updateMetaDataFile(context: Context, path: String, effectID: Int): Int {
        var result = -1
        val projection = arrayOf("_id")
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(path)

        val queryUri = MediaStore.Files.getContentUri("external")
        val contentResolver = context.contentResolver
        val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)!!
        if (c.moveToFirst()) {
            val id = c.getLong(c.getColumnIndexOrThrow("_id"))
            val updateUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
            val contentValues = ContentValues()
            contentValues.put("document_id", effectID)
            contentResolver.update(
                updateUri,
                contentValues,
                selection,
                selectionArgs
            )
            result = contentResolver.update(updateUri, contentValues, "_id=?", arrayOf(id.toString()))
        }
        c.close()
        return result
    }

    fun formatSampleRate(content: String): String? {
        val size = content.toLong()
        if (size <= 0) return "0"
        val units = arrayOf("Hz", "kHz", "mHz", "gHz", "tHz")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1000.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / Math.pow(
                1000.0,
                digitGroups.toDouble()
            )
        ) + " " + units[digitGroups]
    }

    fun formatBitRate(content: String): String? {
        val size = content.toInt()
        return if (size <= 0) "0" else (size / 1000).toString() + " Kbps"
    }

    @SuppressLint("SimpleDateFormat")
    fun sdf(): SimpleDateFormat {
        return SimpleDateFormat("yyyyMMdd_HHmmss")
    }

    fun restorePrefData(activity: Activity): String? {
        val pref = activity.getSharedPreferences(Constans.RATE, Context.MODE_PRIVATE)
        return pref.getString("isIntro", "")
    }

    @JvmStatic
    fun intentToPolicy(context: Context) {
        val url = MobileApp.policy(context)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }


}