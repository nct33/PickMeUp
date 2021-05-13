package com.example.pickmeup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout

class Adapter(
    private var feedList : MutableList<Feed>,
    private val listener : SetListener
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val notes = LayoutInflater.from(parent.context).inflate(R.layout.feed_view, parent, false)

        return ViewHolder(notes)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        val currentItem = feedList[position]

        holder.type.text = currentItem.type
        if (currentItem.type == "photo") {
            DownloadImageFromInternet(holder.source_image).execute(currentItem.source)
        } else {
            holder.source_quote.text = currentItem.source
        }
        holder.author.text = currentItem.author
        holder.feedTagView.tags = arrayListOf(currentItem.category)
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

    override fun getItemCount() = feedList.size

    inner class ViewHolder(items : View) : RecyclerView.ViewHolder(items), View.OnClickListener {
        val type : TextView = items.findViewById(R.id.type)
        val source_image : ImageView = items.findViewById(R.id.source_image)
        val source_quote : TextView = items.findViewById(R.id.source_quote)
        val author : TextView = items.findViewById(R.id.author)
        val feedTagView : TagContainerLayout = items.findViewById(R.id.tags)

        init {
            items.setOnClickListener(this)
        }

        override fun onClick(view : View) {
            val position = adapterPosition
            listener.onClick(position)
        }
    }

    interface SetListener {
        fun onClick(position : Int)
    }
}