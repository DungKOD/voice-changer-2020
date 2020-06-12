package com.audioeffect.voicechanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.item.ItemEffect
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_effect.*

class EffectAdapter(private val listEffect: ArrayList<ItemEffect>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mEffectCallback: OnClickCallback? = null
    private var checkColorPosition = -1

    companion object {
        const val ItemEffect = 1
        const val ItemEmpty = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemEmpty -> {
                ItemEmpty(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_empty, parent, false)
                )
            }
            else -> {
                ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_effect, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == listEffect.size) {
            ItemEmpty
        } else {
            ItemEffect
        }
    }

    override fun getItemCount() = listEffect.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemHolder -> {
                val item = listEffect[position]
                holder.container(item, position)
            }
            is ItemEmpty -> {
                holder.container()
            }
        }
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun container(item: ItemEffect, position: Int) {
            txtNameEff.text = item.effectName
            Glide.with(itemView.context).load(item.drawableResource).into(imgEff)
            listener(item)
            if (position == checkColorPosition) {
                rlItemView.setBackgroundColor(itemView.context.resources.getColor(R.color.colorEffect))
                imgEff.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.custom_imageclick))
            } else {
                rlItemView.setBackgroundColor(itemView.context.resources.getColor(R.color.colorWhite))
                imgEff.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.custom_image))
            }
        }

        private fun listener(item: ItemEffect) {
            itemView.setOnClickListener {
                mEffectCallback!!.onEffectClicked(item, position)
            }
        }
    }

    inner class ItemEmpty(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun container() {
        }
    }

    fun setCallback(effectItemCallback: OnClickCallback) {
        mEffectCallback = effectItemCallback
    }

    interface OnClickCallback {
        fun onEffectClicked(effect: ItemEffect, position: Int)
    }

    fun setPositionClick(position: Int) {
        if (checkColorPosition != -1) {
            notifyItemChanged(checkColorPosition)
        }

        checkColorPosition = position
        notifyItemChanged(checkColorPosition)
    }
}