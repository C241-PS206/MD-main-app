package com.capstone.agrovision.view.bookmark

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.agrovision.R
import com.capstone.agrovision.data.local.BookmarkResult

class BookmarkAdapter(
    private val bookmarkList: MutableList<BookmarkResult>,
    private val onItemClickListener: (BookmarkResult) -> Unit,
    private val onDeleteClickListener: (BookmarkResult) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarkList[position]
        holder.bind(bookmark, onItemClickListener, onDeleteClickListener)
    }

    override fun getItemCount(): Int = bookmarkList.size

    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val savedImage: ImageView = itemView.findViewById(R.id.savedImage)
        private val tvResult: TextView = itemView.findViewById(R.id.tvSavedResult)
        private val tvDescResult: TextView = itemView.findViewById(R.id.tvSavedResultDesc)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(
            bookmark: BookmarkResult,
            onItemClick: (BookmarkResult) -> Unit,
            onDeleteClick: (BookmarkResult) -> Unit
        ) {
            savedImage.setImageURI(Uri.parse(bookmark.imagePath))
            tvResult.text = bookmark.result
            tvDescResult.text = bookmark.result

            itemView.setOnClickListener {
                onItemClick(bookmark)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(bookmark)
            }
        }
    }

    fun removeItem(bookmark: BookmarkResult) {
        val position = bookmarkList.indexOf(bookmark)
        if (position != -1) {
            bookmarkList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
