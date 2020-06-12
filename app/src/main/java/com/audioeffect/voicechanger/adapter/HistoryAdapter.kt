package com.audioeffect.voicechanger.adapter

import android.annotation.SuppressLint
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.bridge.VoiceUtil
import com.audioeffect.voicechanger.event.PauseEvent
import com.audioeffect.voicechanger.event.PlayAudioEvent
import com.audioeffect.voicechanger.event.ProgressEvent
import com.audioeffect.voicechanger.event.SeeBarEvent
import com.audioeffect.voicechanger.item.ItemGallery
import com.audioeffect.voicechanger.utils.Constans
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_history.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HistoryAdapter(private val listHistory: ArrayList<ItemGallery>, private val callBackHistory: HistoryCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var checkColorPosition = -1
    private var mItemPlayingPosition = -1
    private var mIsPlay = false
    private var mLastClickTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_history, parent, false)
        )
    }

    override fun getItemCount() = listHistory.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listHistory[position]
        if (listHistory.size > 0) {
            try {
                    val nameMp3 = item.fileName.substring(item.fileName.lastIndexOf("."))
                    if (nameMp3 == Constans.TYPE_MP3) {
                        (holder as ItemHolder).container(item)
                    }

            } catch (e: Exception) {
            }
        }
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        init {
            EventBus.getDefault().register(this)
        }

        override val containerView: View?
            get() = itemView

        fun container(item: ItemGallery) {
            setColorPosition()
            if (mItemPlayingPosition != position) {
                loadIvPlayResource(R.drawable.ic_icon_play)
            } else {
                if (mIsPlay) {
                    loadIvPlayResource(R.drawable.ic_icon_pause)
                } else {
                    loadIvPlayResource(R.drawable.ic_icon_play)
                }
            }

            if (VoiceUtil.getEffectDrawable(item.effectID) !== -1) {
                Glide.with(itemView.context)
                    .load(VoiceUtil.getEffectDrawable(item.getEffectID()))
                    .into(imgEff)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_record)
                    .centerCrop()
                    .into(imgEff)
            }

            txtNameAudio.text = item.fileName
            txtNameEff.text = VoiceUtil.getEffectName(item.effectID)
            listener(item)
        }

        @Subscribe
        fun updateProgress(event: ProgressEvent) {
            if (event.position == position) {
                sbAudio.progress = event.process
            }
        }

        @Subscribe
        fun playAudio(event: PlayAudioEvent) {
            if (event.position == position) {
                if (event.b) {
                    loadIvPlayResource(R.drawable.ic_icon_pause)
                    sbAudio.visibility = View.VISIBLE
                    txtNameEff.visibility = View.GONE
                } else {
                    loadIvPlayResource(R.drawable.ic_icon_play)
                    sbAudio.visibility = View.GONE
                    txtNameEff.visibility = View.VISIBLE
                    sbAudio.progress = 0
                }
            }
        }

        @Subscribe
        fun pauseEvent(event: PauseEvent) {
            if (event.position == position) {
                if (event.b) {
                    loadIvPlayResource(R.drawable.ic_icon_pause)
                    sbAudio.visibility = View.VISIBLE
                    txtNameEff.visibility = View.GONE
                } else {
                    loadIvPlayResource(R.drawable.ic_icon_play)
                    sbAudio.visibility = View.GONE
                    txtNameEff.visibility = View.VISIBLE
                }
            }
        }

        private fun setColorPosition() {
            if (position == checkColorPosition && mIsPlay) {
                rlItemView.setBackgroundColor(itemView.context.resources.getColor(R.color.colorEffect))
                imgEff.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.custom_imageclick))
                sbAudio.visibility = View.VISIBLE
                txtNameEff.visibility = View.GONE

            } else {
                rlItemView.setBackgroundColor(itemView.context.resources.getColor(R.color.colorWhite))
                imgEff.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.custom_image))
                sbAudio.visibility = View.GONE
                txtNameEff.visibility = View.VISIBLE
            }
        }

        @SuppressLint("NewApi")
        private fun loadIvPlayResource(
            resourceID: Int
        ) {
            Glide.with(itemView.context)
                .load(itemView.context.getDrawable(resourceID))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgPlay)
        }

        fun listener(item: ItemGallery) {
            itemView.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    mLastClickTime = SystemClock.elapsedRealtime()
                    callBackHistory.playAudioItem(listHistory, position, Constans.KEY_DEFAULT)
                }
            }

            imgPlay.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    mLastClickTime = SystemClock.elapsedRealtime()
                    callBackHistory.playAudioItem(listHistory, position, Constans.KEY_IMAGE)
                }
            }

            imgDetail.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    mLastClickTime = SystemClock.elapsedRealtime()
                    callBackHistory.onIvOptionClicked(viewPopup, item)
                }
            }

            sbAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    val progress = seekBar!!.progress.toFloat()
                    EventBus.getDefault().post(SeeBarEvent(progress.toInt()))
                }
            })
        }
    }


    fun getItemPlayingPosition(): Int {
        return mItemPlayingPosition
    }

    fun setItemPlayingPosition(position: Int) {
        val prevChecked = mItemPlayingPosition
        mItemPlayingPosition = position
        if (prevChecked != -1) {
            notifyItemChanged(prevChecked)
        }
        if (checkColorPosition != -1) {
            notifyItemChanged(checkColorPosition)
        }

        checkColorPosition = position
        notifyItemChanged(mItemPlayingPosition)
    }

    fun getIsPlay(): Boolean {
        return mIsPlay
    }

    fun setPlay(isPlay: Boolean) {
        mIsPlay = isPlay
    }

    /**
     * callBack to click item
     */
    interface HistoryCallback {
        fun startDetail(fileName: String, filePath: String, effectID: Int,idAudio:Int)
        fun playAudioItem(itemGalleries: ArrayList<ItemGallery>, position: Int, key: String)
        fun pauseAudio(path: String, position: Int)
        fun onIvOptionClicked(view: View?, itemGallery: ItemGallery?)
    }
}