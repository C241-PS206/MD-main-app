package com.capstone.agrovision.view.bookmark

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.agrovision.R
import com.capstone.agrovision.data.local.Bookmark

class BookmarkAdapter(
    private val bookmarkList: List<Bookmark>,
    private val onItemClickListener: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarkList[position]
        holder.bind(bookmark, onItemClickListener)
    }

    override fun getItemCount(): Int = bookmarkList.size

    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val savedImage: ImageView = itemView.findViewById(R.id.savedImage)
        private val tvResult: TextView = itemView.findViewById(R.id.tvSavedResult)
        private val tvDescResult: TextView = itemView.findViewById(R.id.tvSavedResultDesc)

        fun bind(bookmark: Bookmark, onItemClick: (Bookmark) -> Unit) {
            savedImage.setImageURI(Uri.parse(bookmark.imageUri))
            tvResult.text = bookmark.result
            tvDescResult.text = bookmark.description

            itemView.setOnClickListener {
                onItemClick(bookmark)
            }
        }
    }
}
