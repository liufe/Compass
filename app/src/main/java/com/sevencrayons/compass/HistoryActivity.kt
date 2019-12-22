package com.sevencrayons.compass

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_history.*
import java.io.File
import java.io.FilenameFilter


class HistoryActivity : AppCompatActivity(), OnHistoryItemClickListener {
    override fun onItemClick(data: File) {
        var intent = Intent()
        intent.action = Intent.ACTION_VIEW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(this, "com.fly.compass.fileProvider", data)
            intent.setDataAndType(uri, "text/plain")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            val uri = Uri.fromFile(data)
            intent.setDataAndType(uri, "text/plain")
        }
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        loadData()
    }

    /**
     * 读取本地数据,只得到目录索引
     */
    private fun loadData() {
        var rootPath = externalCacheDir.absolutePath + "/xlog"
        var rootFile = File(rootPath)
        if (!rootFile.exists()) {
            rootFile.mkdirs()
        }
        var files = rootFile.listFiles(FileFilter())
        files.reverse()
        recycle.layoutManager = LinearLayoutManager(this)
        recycle.addItemDecoration(DividerItemDecoration(this, 1))
        recycle.adapter = HistoryAdapter(files, this)
    }

    inner class FileFilter : FilenameFilter {
        override fun accept(dir: File?, name: String?): Boolean {
            if (name.isNullOrEmpty()) return false
            return name.endsWith(".txt")
        }

    }

    class HistoryAdapter constructor(var file: Array<File>, var listener: OnHistoryItemClickListener) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_history, viewGroup, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
            viewHolder.textView.text = file[i].name
            viewHolder.itemView.setOnClickListener {
                listener?.onItemClick(file[i])
            }
        }

        override fun getItemCount(): Int {
            return file.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var textView: TextView = itemView.findViewById(R.id.fileName)

        }
    }

}

interface OnHistoryItemClickListener {

    fun onItemClick(data: File)

}
