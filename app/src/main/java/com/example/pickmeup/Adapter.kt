package com.example.pickmeup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
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

        holder.image.setBackgroundResource(currentItem.image)
        holder.content.text = currentItem.content
        holder.source.text = currentItem.source
        holder.feedTagView.tags = currentItem.tags
    }

    override fun getItemCount() = feedList.size

    inner class ViewHolder(items : View) : RecyclerView.ViewHolder(items), View.OnClickListener {
        val image : View = items.findViewById(R.id.content_image)
        val content : TextView = items.findViewById(R.id.content)
        val source : TextView = items.findViewById(R.id.source)
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