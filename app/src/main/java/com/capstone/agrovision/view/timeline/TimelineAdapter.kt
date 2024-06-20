package com.capstone.agrovision.view.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.agrovision.R
import com.capstone.agrovision.view.upload.Upload

class TimelineAdapter(private val postList: List<Upload>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val postTime: TextView = itemView.findViewById(R.id.postTime)
        val postContent: TextView = itemView.findViewById(R.id.postContent)
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val post = postList[position]
        holder.userName.text = post.userName
        holder.postTime.text = post.postTime
        holder.postContent.text = post.postContent

        Glide.with(holder.postImage.context)
            .load(post.postImage)
            .into(holder.postImage)
    }

    override fun getItemCount() = postList.size
}
