package com.audioeffect.voicechanger.ui.activities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.adapter.HistoryAdapter
import com.audioeffect.voicechanger.adapter.MainListAdapter
import com.audioeffect.voicechanger.base.animation.AnimatedColor
import com.audioeffect.voicechanger.bridge.VoiceUtil
import com.audioeffect.voicechanger.event.*
import com.audioeffect.voicechanger.item.ItemGallery
import com.audioeffect.voicechanger.managers.AudioRecorder
import com.audioeffect.voicechanger.managers.DetailCallback
import com.audioeffect.voicechanger.managers.DetailPlayAudio
import com.audioeffect.voicechanger.permission.PermissionApp
import com.audioeffect.voicechanger.permission.PermissionApp.Companion.mIsDoNotShowClicked
import com.audioeffect.voicechanger.ui.components.FiltersMotionLayout
import com.audioeffect.voicechanger.ui.fragments.SettingActivity
import com.audioeffect.voicechanger.utils.Constans
import com.audioeffect.voicechanger.utils.FileUtil
import com.audioeffect.voicechanger.utils.UtilView
import com.audioeffect.voicechanger.utils.UtilView.formatTime
import com.audioeffect.voicechanger.utils.UtilView.setAnimationBTN
import com.audioeffect.voicechanger.utils.UtilView.setAnimationHide
import com.audioeffect.voicechanger.utils.UtilView.setAnimationPlay
import com.audioeffect.voicechanger.utils.UtilView.setAnimationText
import com.masoudss.lib.Utils
import com.masoudss.lib.WaveGravity
import com.recodereffect.voicechanger.utils.bindView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_noview.*
import kotlinx.android.synthetic.main.layout_permission.*
import kotlinx.android.synthetic.main.layout_play.*
import kotlinx.android.synthetic.main.layout_recoder.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(), SettingActivity.ViewNavCallback, HistoryAdapter.HistoryCallback, DetailPlayAudio.ProgressView,

    FiltersMotionLayout.OnCallbackListener {

    companion object {
        var listHistory: ArrayList<ItemGallery>? = null
    }


    //////////////////////////// BIND VIEW /////////////////////////////////

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val filtersMotionLayout: FiltersMotionLayout by bindView(R.id.filters_motion_layout)
    private lateinit var mainListAdapter: MainListAdapter
    private val loadingDuration: Long
        get() = (600 / 0.8).toLong()

    private lateinit var permissionApp: PermissionApp
    private lateinit var adapter: HistoryAdapter
    private var mCallback: DetailCallback? = null
    private var mHandler: Handler? = null
    private var checkRecorder = 0
    private var mIsCompleted = false
    private var checkPlayAudio = 0
    private var mPosition = -1
    private var mIsRecord = false
    private var key = ""
    private var nameDefault = ""
    private var audioRecorder: AudioRecorder? = null
    private lateinit var settingActivity: SettingActivity

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        createView()
        UtilView.savePrefsPre(this)
        permissionInit()
        mainListener()
        getData()
        drawableNavigation()
        backRateApp()
        initView()
    }

    private fun animateStatusBar() {
        val oneToTwo = AnimatedColor(ContextCompat.getColor(this, R.color.colorWhite), ContextCompat.getColor(this, R.color.color_view_alpha))
        val animator = ObjectAnimator.ofFloat(0f, 0.5F).setDuration(200)
        animator.addUpdateListener { animation ->
            val v = animation.animatedValue as Float
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = oneToTwo.with(v)
            }
        }
        animator.start()
    }

    private fun animateHideStatusBar() {
        val oneToTwo = AnimatedColor(ContextCompat.getColor(this, R.color.colorWhite), ContextCompat.getColor(this, R.color.color_view_alpha))
        val animator = ObjectAnimator.ofFloat(0.5F, 0F).setDuration(200)
        animator.addUpdateListener { animation ->
            val v = animation.animatedValue as Float
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = oneToTwo.with(v)
            }
        }
        animator.start()
    }

    private fun createView() {
        listHistory = ArrayList()
        audioRecorder = AudioRecorder(this)
        mHandler = Handler()
    }

    private fun initView() {


        audioRecorder = AudioRecorder(this)
        // Init FilterLayout
        useFiltersMotionLayout()

        mainListAdapter =
            MainListAdapter(this)
        recyclerView.let {
            it.adapter = mainListAdapter
            it.layoutManager = LinearLayoutManager(this)
            it.setHasFixedSize(true)
        }

        updateRecyclerViewAnimDuration()
    }


    private fun useFiltersMotionLayout() {

        filtersMotionLayout.let {
            it.isVisible = true
            it.setOnCallbackListener(this)
            it.setAudioRecorder(audioRecorder!!)
        }
    }


    private fun updateRecyclerViewAnimDuration() = recyclerView.itemAnimator?.run {
        removeDuration = loadingDuration * 60 / 100
        addDuration = loadingDuration
    }

    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator =
        mainListAdapter.getScaleDownAnimator(isScaledDown)


    private fun waveTouch() {
        waveformSeekBar.setOnTouchListener { _, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

//    private fun animationTouch() {
//        rlViewBackground.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    return@setOnTouchListener true
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    return@setOnTouchListener true
//                }
//                MotionEvent.ACTION_UP -> {
//                    return@setOnTouchListener true
//                }
//            }
//            return@setOnTouchListener false
//        }
//    }

    private fun getDummyWaveSample(): IntArray {
        val data = IntArray(50)
        for (i in data.indices)
            data[i] = Random().nextInt(data.size)
        return data
    }

    private fun permissionInit() {
        initImage()
        permissionApp = PermissionApp()
        requestPermission()
    }

    private fun backRateApp() {

    }

    private fun drawableNavigation() {
        settingActivity = SettingActivity(this, this)
        mCallback = DetailPlayAudio(this, this)
        settingActivity.createView()
        if (!settingActivity.isSetting) {
            UtilView.setStatusBarColor(this, R.color.colorWhite)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getData() {
        if (intent.getStringExtra(Constans.KEY_NEW_FILE) != null) {
            key = intent.getStringExtra(Constans.KEY_NEW_FILE)
        }
        if (key.isNotEmpty()) {

        }
    }

    private fun initImage() {
//        UtilView.loadGlide(this, R.drawable.img_permission, imgPermission)
//        UtilView.loadGlide(this, R.drawable.img_novoice, imgNoItem)
    }

    override fun onRestart() {
        super.onRestart()
        requestPermission()
    }

    @SuppressLint("NewApi")
    private fun mainListener() {
        btnPermission.setOnClickListener {
            if (mIsDoNotShowClicked) {
                permissionApp.gotoDetailSettings(this)
            } else {
                requestPermission()
            }
        }

//        rlRecorder.setOnClickListener {
//            if (checkRecorder == 0) {
//                animationView()
//            } else {
//                startRecorder()
//            }
//        }

//        btnClose.setOnClickListener {
//            closeViewRecorder()
//        }

//        viewRecorder.setOnClickListener {
//            if (checkRecorder != 0 && !mIsRecord) {
//                closeViewRecorder()
//            }
//
//            if (checkPlay != 0) {
//                closePlay()
//                closeViewRecorder()
//            }
//
//            popupWindow?.isShowing?.let {
//                setAnimationHide(viewRecorder)
//                setAnimationStatus()
//                viewRecorder.visibility = View.GONE
//            }
//        }

//        imgNavigation.setOnClickListener {
//            settingActivity.StartSetting()
//            UtilView.setStatusBarColor(this, R.color.backgroundStart)
//        }
//
//        btnOpenFile.setOnClickListener {
//            intentToOpenFile()
//        }
//
//        btnEditFile.setOnClickListener {
//            mCallback?.editAudio(path, effectID, nameAudio, idFile)
//        }
//
//        btnShare.setOnClickListener {
//            mCallback?.shareAudio(path)
//        }
//
//        btnReName.setOnClickListener {
//            mCallback?.reNameAudio(nameAudio, path, effectID)
//        }
//
//        btnInfo.setOnClickListener {
//            mCallback?.infoAudio(path)
//        }
//
//        rlPlayAudio.setOnClickListener {
//            if (checkPlayAudio == 1) {
//                mCallback?.pauseAudio()
//                showStop()
//                checkPlayAudio = 0
//            } else {
//                mCallback?.resumeAudio(uri)
//                checkPlayAudio = 1
//                showStart()
//            }
//        }
    }

    private fun showStart() {
//        imgStopAudio.visibility = View.GONE
//        imgPlayAudio.visibility = View.VISIBLE
    }

    private fun showStop() {
//        imgPlayAudio.visibility = View.GONE
//        imgStopAudio.visibility = View.VISIBLE
    }

    private fun startRecorder() {
//        animationRecorder()
//        UtilView.loadGlide(this, R.drawable.ic_icon_stop_record, imgRecorder)
//        if (btnOpenFile.visibility == View.VISIBLE || btnClose.visibility == View.VISIBLE) {
//            setAnimationHide(btnClose)
//            setAnimationHide(btnOpenFile)
//            btnClose.visibility = View.GONE
//            btnOpenFile.visibility = View.GONE
//        }
//        txtTitle.text = getString(R.string.recording)
//        if (mIsRecord) {
//            onStopRecord()
//        } else {
//            resolveRecord()
//        }
    }

    private fun closeViewRecorder() {
//        chronometer.visibility = View.GONE
//        txtTitle.visibility = View.GONE
//        motion_base.transitionToStart()
//        setAnimationStatus()
//        setAnimationHide(viewRecorder)
//        viewRecorder.visibility = View.GONE
//        setAnimationHide(icRecorder)
//        icRecorder.visibility = View.GONE
//        btnOpenFile.visibility = View.GONE
//        btnClose.visibility = View.GONE
//        viewAnimation.visibility = View.GONE
//        checkRecorder = 0
    }

    private fun setAnimationStatus() {
        animateHideStatusBar()
    }

    private fun setAnimationStatusShow() {
        animateStatusBar()
    }



    private fun setScale() {
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        btnOpenFile.startAnimation(scaleDown)
        btnClose.startAnimation(scaleDown)
        Handler().postDelayed({
            btnOpenFile.startAnimation(scaleUp)
            btnClose.startAnimation(scaleUp)
        }, 400)
        btnOpenFile.visibility = View.VISIBLE
        btnClose.visibility = View.VISIBLE
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        permissionApp.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this,
            btnPermission,
            icPer
        )
    }

    override fun onResume() {
        super.onResume()
        if (permissionApp.hasPermissions(this, *UtilView.PERMISSIONS)) {
            permissionApp.showMainActivity(icPer)
        }
        mCallback?.resumeAudio(null)
        audioRecorder?.checkAddView(true)
        showStart()
    }


    private fun requestPermission() {
        permissionApp.requestPermission(this, icPer)
    }

    @SuppressLint("HandlerLeak")
    private fun resolveRecord() {
        nameDefault = FileUtil.getNameOrigin()
        try {
            audioRecorder?.startRecorder(rlContainer, nameDefault)
            chronometerListener()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        mIsRecord = true
    }

    private fun chronometerListener() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }





    override fun onBackPressed() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFinishEvent(event: StopSaveFile) {
        closeViewRecorder()
        FileUtil.deleteFile(UtilView.getPathCache(nameDefault))
    }







    override fun startDetail(fileName: String, filePath: String, effectID: Int, idAudio: Int) {
        mCallback?.detailAudio(fileName, filePath, effectID, idAudio)
    }

    @SuppressLint("SetTextI18n")
    override fun playAudioItem(itemGalleries: ArrayList<ItemGallery>, position: Int, key: String) {
//        mPosition = position
//        txtNameStorage.text = itemGalleries[position].name + Constans.TYPE_MP3
//        txtNameEffEct.text = VoiceUtil.getEffectName(itemGalleries[position].effectID)
//        setImgPlay()
//        idFile = itemGalleries[position].idAudio
//        when (key) {
//            Constans.KEY_DEFAULT -> {
//                if (checkPlay == 0) {
//                    rlRecorder.visibility = View.GONE
//                    showView()
//                    checkPlay += 1
//                    checkPlayAudio += 1
//                    adapter.setItemPlayingPosition(position)
//                    adapter.setPlay(false)
//                    showViewPlay()
//                    playAudio(itemGalleries, position)
//                }
//            }
//
//            Constans.KEY_IMAGE -> {
//                playAudioImage(position, itemGalleries)
//            }
//        }
    }





    override fun pauseAudio(path: String, position: Int) {
    }

    override fun onIvOptionClicked(view: View?, itemGallery: ItemGallery?) {

    }

    override fun progress(process: Int) {
        waveformSeekBar.progress = process / 10
        EventBus.getDefault().post(ProgressEvent(process, mPosition))
    }

    override fun onCompletion() {
        waveformSeekBar.progress = 0
        adapter.setPlay(false)
        checkPlayAudio = 0
        adapter.setItemPlayingPosition(mPosition)
        showStop()
        mIsCompleted = true
        chronometerPlay.text = "00:00"
        EventBus.getDefault().post(PlayAudioEvent(mPosition, false))
    }

    override fun onTimePay(timeStart: Long) {
        runOnUiThread {
            chronometerPlay.text = formatTime(timeStart * 1000)
        }
    }

    override fun setPlay(b: Boolean) {
        adapter.setPlay(b)
        EventBus.getDefault().post(PlayAudioEvent(mPosition, b))
    }

    override fun onStopAudio() {
        closeViewRecorder()
    }

    override fun onFinish() {
        finish()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        audioRecorder?.checkAddView(false)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun endNav(b: Boolean) {
        if (b) {
            UtilView.setStatusBarColor(this, R.color.backgroundStart)
        } else {
            if (checkRecorder != 0 && !mIsRecord) {
                UtilView.setStatusBarColor(this, R.color.colorStatus)
            } else {
                UtilView.setStatusBarColor(this, R.color.colorWhite)
            }
        }
    }

    override fun startNav() {
        UtilView.setStatusBarColor(this, R.color.backgroundStart)
    }

    override fun closeNav() {
        onBackPressed()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFinishEvent(event: RateEvent) {
        if (event.message == Constans.RATE_FINISH) {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        mCallback?.pauseAudio()
    }



    override fun onClickFolder() {
        Intent(this@MainActivity, OpenFileActivity::class.java).apply {
            startActivity(this)
        }
    }
}
