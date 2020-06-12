package com.audioeffect.voicechanger.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.audioeffect.voicechanger.R
import com.audioeffect.voicechanger.adapter.FolderAdapter
import com.audioeffect.voicechanger.item.ItemFolder
import com.audioeffect.voicechanger.task.ReadFileFolder
import com.audioeffect.voicechanger.utils.Constans
import com.audioeffect.voicechanger.utils.FileUtil
import kotlinx.android.synthetic.main.activity_open_file.*
import kotlinx.android.synthetic.main.layout_toolbar_file.*
import java.io.File

class OpenFileActivity : AppCompatActivity(), FolderAdapter.ItemClickCallBack {
    val MAIN_STORAGE = Environment.getExternalStorageDirectory().absolutePath
    private var mPath: String? = null
    private var adapterPosition = 0
    var mLayoutManager: LinearLayoutManager? = null
    private lateinit var listFile: ArrayList<ItemFolder>
    private lateinit var adapter: FolderAdapter
    private var minScroll = 0
    private var maxItemLayout = 0
    private var typeItem = 0
    private var nameScroll = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_file)
        listFile = ArrayList()
        initAdapter()
        readFileFolder()
        listenerFile()
        scrollAdapter()
    }


    private fun listenerFile() {
        imgBackFile.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapter() {
        mLayoutManager = LinearLayoutManager(applicationContext)
        rcFolder.layoutManager = mLayoutManager
        adapter = FolderAdapter(listFile)
        adapter.setItemClickCallBack(this)
        rcFolder.adapter = adapter
    }

    private fun readFileFolder() {
        ReadFileFolder(this, object : ReadFileFolder.FileCallback {
            override fun onSuccess(listFolder: List<ItemFolder>) {
                listFile.clear()
                listFile.addAll(listFolder)
                adapter.notifyDataSetChanged()
                maxItemLayout = mLayoutManager!!.findLastVisibleItemPosition()
            }
        }).execute(MAIN_STORAGE)
    }

    private fun scrollAdapter() {
        rcFolder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                mPath?.let {
                    nameScroll = mPath!!.substring(mPath?.lastIndexOf("/")!! + 1)
                }

                val lastPos = mLayoutManager!!.findLastVisibleItemPosition()
                if (minScroll == 0) {
                    maxItemLayout = lastPos
                }

                if (lastPos > maxItemLayout) {
                    if (nameScroll == getString(R.string.o)) {
                        typeItem = lastPos
                    } else {
                        adapterPosition = lastPos
                    }
                } else {
                    0
                }
                minScroll = 1
            }
        })
    }

    override fun onFolderItemClicked(filePath: String, nameFolder: String, position: Int) {
        mPath = filePath
        ReadFileFolder(this, object : ReadFileFolder.FileCallback {
            override fun onSuccess(listFolder: List<ItemFolder>) {
                listFile.clear()
                listFile.addAll(listFolder)
                adapter.notifyDataSetChanged()
                rcFolder.scrollToPosition(0)
                val name = filePath.substring(filePath.lastIndexOf("/") + 1)

                if (name == getString(R.string.o)) {
                    tvTitle.text = getString(R.string.opent_audio_file)
                } else {
                    tvTitle.text = name
                }
            }
        }).execute(filePath)
    }

    override fun onFolderParentClick(filePath: String, nameFolder: String) {
        mPath = filePath
        ReadFileFolder(this, object : ReadFileFolder.FileCallback {
            override fun onSuccess(listFolder: List<ItemFolder>) {
                listFile.clear()
                listFile.addAll(listFolder)
                adapter.notifyDataSetChanged()
                rcFolder.scrollToPosition(adapterPosition)
                val name = filePath.substring(filePath.lastIndexOf("/") + 1)
                if (name == getString(R.string.o)) {
                    rcFolder.scrollToPosition(typeItem)
                    tvTitle.text = getString(R.string.opent_audio_file)
                } else {
                    rcFolder.scrollToPosition(adapterPosition)
                    tvTitle.text = name
                }
            }
        }).execute(filePath)
    }

    override fun onFileItemClicked(filePath: String) {
        val intent = Intent(this, EffectActivity::class.java)

        intent.putExtra(Constans.KEY_PATH, filePath)
        intent.putExtra(Constans.KEY_EFFECT, Constans.KEY_NEW_RECORDER)
        intent.putExtra(Constans.NAME_EDIT, "")
        intent.putExtra(Constans.KEY_NAME_DEFAULT, FileUtil.getNameOrigin())
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (adapter.getFirstItemFolder().type === ItemFolder.ItemType.PARENT_FOLDER) {
            val f = File(mPath!!)
            mPath = f.parentFile?.path
            ReadFileFolder(this, object : ReadFileFolder.FileCallback {
                override fun onSuccess(listFolder: List<ItemFolder>) {
                    val name = mPath?.substring(mPath?.lastIndexOf("/")!! + 1)
                    listFile.clear()
                    listFile.addAll(listFolder)
                    adapter.notifyDataSetChanged()
                    if (name == getString(R.string.o)) {
                        rcFolder.scrollToPosition(typeItem)
                        tvTitle.text = getString(R.string.opent_audio_file)
                    } else {
                        rcFolder.scrollToPosition(adapterPosition)
                        tvTitle.text = name
                    }
                }
            }).execute(mPath)

        } else {
            finish()
        }
    }
}
