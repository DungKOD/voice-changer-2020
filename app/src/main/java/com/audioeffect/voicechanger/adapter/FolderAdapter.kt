package com.audioeffect.voicechanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.item.ItemFolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.dapter_folder.*
import java.util.ArrayList

class FolderAdapter(private val mItemFolders: ArrayList<ItemFolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mItemClickCallBack: ItemClickCallBack? = null
    private var positionScroll = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FolderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dapter_folder, parent, false)
        )
    }

    override fun getItemCount() = mItemFolders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        positionScroll = position
        val itemFolder = mItemFolders[position]
        (holder as FolderViewHolder).container(itemFolder)
    }

    fun getFirstItemFolder(): ItemFolder {
        return mItemFolders[0]
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun container(itemFolder: ItemFolder) {
            when {
                itemFolder.type === ItemFolder.ItemType.PARENT_FOLDER -> folderParent(itemFolder)
                itemFolder.type === ItemFolder.ItemType.FOLDER -> folderList(itemFolder)
                else -> fileList(itemFolder)
            }
        }

        private fun fileList(itemFolder: ItemFolder) {
            ll_folder.visibility = View.GONE

            ll_file.visibility = View.VISIBLE
            tv_folder_name.visibility = View.VISIBLE
            tv_date_modify.visibility = View.VISIBLE
            ll_file.setOnClickListener {
                notifyDataSetChanged()
                mItemClickCallBack?.onFileItemClicked(itemFolder.path)
            }

            Glide.with(itemView.context)
                .load(R.drawable.ic_audio)
                .centerCrop()
                .into(iv_file)
            tv_file_name.text = itemFolder.name
            tv_file_duration.text = itemFolder.duration
        }

        private fun folderList(itemFolder: ItemFolder) {
            ll_file.visibility = View.GONE
            ll_folder.visibility = View.VISIBLE
            tv_folder_name.visibility = View.VISIBLE
            tv_date_modify.visibility = View.VISIBLE

            ll_folder.setOnClickListener {
                notifyDataSetChanged()
                itemFolder.name?.let { it1 -> mItemClickCallBack?.onFolderItemClicked(itemFolder.path, it1, positionScroll) }
            }

            Glide.with(itemView.context)
                .load(R.drawable.ic_folder_app)
                .into(iv_folder)
            tv_folder_name.text = itemFolder.name
            val childCount = itemFolder.childCount
            val content = if (childCount < 2) "$childCount item" else "$childCount items"
            tv_child_count.text = content
            tv_date_modify.text = itemFolder.dateModify
        }

        private fun folderParent(itemFolder: ItemFolder) {
            ll_file.visibility = View.GONE
            tv_separate.visibility = View.GONE
            ll_folder.visibility = View.VISIBLE
            ll_folder.setOnClickListener {
                notifyDataSetChanged()
                mItemClickCallBack?.onFolderParentClick(itemFolder.path, itemView.context.getString(R.string.opent_audio_file))
            }

            Glide.with(itemView.context)
                .load(R.drawable.ic_folder_app)
                .into(iv_folder)
            tv_date_modify.visibility = View.GONE
            tv_folder_name.text = "..."
            tv_child_count.text = itemView.context.getString(R.string.Parent_folder)
        }
    }

    interface ItemClickCallBack {
        fun onFolderItemClicked(filePath: String, nameFolder: String, position: Int)
        fun onFolderParentClick(filePath: String, nameFolder: String)
        fun onFileItemClicked(filePath: String)
    }

    fun setItemClickCallBack(itemClickCallBack: ItemClickCallBack) {
        this.mItemClickCallBack = itemClickCallBack
    }
}