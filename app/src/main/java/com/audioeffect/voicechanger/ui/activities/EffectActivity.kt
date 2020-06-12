package com.audioeffect.voicechanger.ui.activities

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.audioeffect.voicechanger.MobileBilling
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.adapter.EffectAdapter
import com.audioeffect.voicechanger.bridge.VoicePlayUtil
import com.audioeffect.voicechanger.bridge.VoiceUtil
import com.audioeffect.voicechanger.event.FinishEvent
import com.audioeffect.voicechanger.item.ItemEffect
import com.audioeffect.voicechanger.managers.ChangerManager

import com.audioeffect.voicechanger.utils.Constans
import com.audioeffect.voicechanger.utils.EventLog
import com.audioeffect.voicechanger.utils.FileUtil
import com.audioeffect.voicechanger.utils.UtilView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_effect.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt

class EffectActivity : AppCompatActivity(), EffectAdapter.OnClickCallback, VoicePlayUtil.Callback,
    OnSeekBarChangeListener {
    private var pathString = ""
    private var nameFile = ""
    private lateinit var adapter: EffectAdapter
    private var mIsRequestPlay = false
    private var mItemEffect: ItemEffect? = null
    private var mVoicePlayUtil: VoicePlayUtil? = null
    private var keyEffect = ""
    private var nameEdit = ""
    private var idFile = 0
    private var mLastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_effect)
        sbBar.setOnSeekBarChangeListener(this)
        UtilView.setStatusBarColor(this@EffectActivity, R.color.colorStatus)
        getDataStorage()
        listener()
        initPathSuccess()
    }

    init {
        mVoicePlayUtil = VoicePlayUtil()
        mVoicePlayUtil!!.setCallback(this)
    }

    override fun onResume() {
        super.onResume()
//        loadAds()
        if (mIsRequestPlay && !mVoicePlayUtil!!.isPlaying) {
            VoiceUtil.pause(false)
            mVoicePlayUtil!!.isPlaying = true
            updateUIOnResume()
        }
    }


    override fun onComplete() {
        mIsRequestPlay = false
        runOnUiThread {
            updateUIOnPause()
        }
    }

    override fun onStartVoice() {
    }

    override fun onStopVoice() {
        if (mIsRequestPlay && !mVoicePlayUtil!!.isPlaying) {
            mVoicePlayUtil!!.playVoice(mItemEffect!!.effectID)
        }
    }

    override fun onPlayingVoice(currentTime: Int, totalTime: Int) {
        updateProgress(currentTime, totalTime)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val progress = seekBar!!.progress.toFloat() / seekBar.max
        if (mVoicePlayUtil!!.isPlaying) {
            VoiceUtil.seekToPercent(progress)
        }
    }

    override fun onStop() {
        if (mIsRequestPlay && mVoicePlayUtil!!.isPlaying) {
            VoiceUtil.pause(true)
            mVoicePlayUtil!!.isPlaying = false
        }
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun updateProgress(currentTime: Int, totalTime: Int) {
        runOnUiThread {
            val progress = (currentTime.toFloat() / totalTime * sbBar.max).roundToInt()
            sbBar.progress = progress
        }
    }

    private fun updateUIOnPause() {
        Glide.with(this)
            .load(R.drawable.ic_icon_play1)
            .into(ivPlay)
    }

    private fun updateUIOnResume() {
        Glide.with(this)
            .load(R.drawable.ic_icon_pause_1)
            .into(ivPlay)
    }

    override fun onEffectClicked(effect: ItemEffect, position: Int) {
        try {
            playEffAudio(effect, position)
        } catch (e: Exception) {
            playEffAudio(effect, position)
            e.printStackTrace()
        }
    }

    private fun playEffAudio(effect: ItemEffect, position: Int) {
        mVoicePlayUtil!!.stopVoice()
        sbBar.progress = 0
        mItemEffect = effect
        mIsRequestPlay = true
        adapter.setPositionClick(position)
        runOnUiThread { updateUIOnResume() }
        if (!mVoicePlayUtil!!.isPlaying) {
            mVoicePlayUtil!!.playVoice(effect.effectID)
        } else {
            mVoicePlayUtil!!.stopVoice()
        }
    }

    private fun getDataStorage() {
        intent.getStringExtra(Constans.KEY_NAME_DEFAULT)?.let {
            nameFile = it.substring(0, it.lastIndexOf("."))
            txtNameAudio.text = it
        }

        intent.getStringExtra(Constans.NAME_EDIT)?.let {
            nameEdit = it
        }

        intent.getStringExtra(Constans.KEY_EFFECT)?.let {
            keyEffect = it
        }

        intent.getIntExtra(Constans.ID_AUDIO, 0).let {
            idFile = it
        }

        when (intent.getStringExtra(Constans.KEY_EFFECT)) {
            Constans.KEY_EDIT -> {
                val id = intent.getIntExtra(Constans.KEY_EFFECT_ID, 0)
                mItemEffect = ItemEffect(VoiceUtil.getEffectName(id), id, VoiceUtil.getEffectDrawable(id))
            }

            Constans.KEY_NEW_RECORDER -> {
                mItemEffect = ItemEffect("Normal", VoiceUtil.TYPE_NORMAL, R.drawable.icon1)
            }
        }
        getDataPath()
    }

    private fun getDataPath() {
        intent.getStringExtra(Constans.KEY_PATH)?.let {
            pathString = "/storage/emulated/0/Zing MP3/Ai Là Người Thương Em_Quân A.P_-1079249721.mp3"
        }
        initAdapter()
    }

    override fun onBackPressed() {
        try {
            if (keyEffect == Constans.KEY_NEW_RECORDER) {
                FileUtil.deleteFile(UtilView.getPathCache(nameFile + Constans.TYPE_MP3))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        finish()
    }

    private fun listener() {
        imgBackEffect.setOnClickListener {
            onBackPressed()
        }

        rlView.setOnClickListener {
            print("e")
        }

        rlViewBackground.setOnClickListener {
            print(nameFile)
        }

        ivPlay.setOnClickListener {
            try {
                if (!mIsRequestPlay) {
                    mIsRequestPlay = true
                    runOnUiThread {
                        updateUIOnResume()
                    }

                    if (mItemEffect?.effectID == null) {
                        mVoicePlayUtil!!.playVoice(0)
                    } else {
                        mVoicePlayUtil!!.playVoice(mItemEffect!!.effectID)
                    }
                } else {
                    if (mVoicePlayUtil!!.isPlaying) {
                        runOnUiThread { updateUIOnPause() }
                    } else {
                        runOnUiThread { updateUIOnResume() }
                    }
                    VoiceUtil.pause(mVoicePlayUtil!!.isPlaying)
                    mVoicePlayUtil!!.isPlaying = !mVoicePlayUtil!!.isPlaying
                }
            } catch (e: Exception) {
                print(e)
            }
        }

        btnSave.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
                mLastClickTime = SystemClock.elapsedRealtime()
                if (MobileBilling.isPurchasedFeature(this)) {
                    saveFile()
                } else {


                }
            }


        }
    }

    private fun saveFile() {
        VoiceUtil.pause(true)
        mVoicePlayUtil!!.isPlaying = false
        mIsRequestPlay = false
        runOnUiThread {

        }
    }


//    private fun isPriceAds(): Boolean {
//        val pref = applicationContext.getSharedPreferences(Constans.KEY_PRICE_ADS, Context.MODE_PRIVATE)
//        return pref.getBoolean(Constans.IS_INTRO, false)
//    }

    private fun initAdapter() {
        VoiceUtil.createListEffect()
        adapter = EffectAdapter(VoiceUtil.getsItemEffects())
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rcEffect.layoutManager = linearLayoutManager
        adapter.setCallback(this)
        rcEffect.adapter = adapter
        setItemColor()
        scroll()
    }

    private fun scroll() {
        if (keyEffect == Constans.KEY_EDIT) {
            for (i in 0 until VoiceUtil.getsItemEffects().size) {
                if (mItemEffect?.effectID == VoiceUtil.getsItemEffects()[i].effectID) {
                    rcEffect.scrollToPosition(i)
                }
            }
        }
    }

    private fun setItemColor() {
        for (i in 0 until VoiceUtil.getsItemEffects().size) {
            val item = VoiceUtil.getsItemEffects()[i]
            if (mItemEffect?.effectID == item.effectID) {
                adapter.setPositionClick(i)
            }
        }
    }

    private fun initPathSuccess() {
        ChangerManager(pathString).setCallback(object : ChangerManager.InitPathCallback {
            override fun initSuccess(value: Int) {
                rlViewProgress.visibility = View.GONE
                UtilView.setStatusBarColor(this@EffectActivity, R.color.colorWhite)
            }
        })
    }

    override fun onDestroy() {
        mVoicePlayUtil!!.stopVoice()
        if (mVoicePlayUtil != null) {
            mVoicePlayUtil!!.setCallback(null)
            mVoicePlayUtil = null
        }
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFinishEvent(event: FinishEvent) {
        finish()
    }
}
