package com.example.speechify.ui.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.speechify.R
import com.example.speechify.data.SongModel
import kotlinx.android.synthetic.main.item_up_next.view.*

class PlayListAdapter : ListAdapter<SongModel, UpNextViewHolder>(SongModelDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpNextViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_up_next, parent, false)
        return UpNextViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpNextViewHolder, position: Int) {
        val data = getItem(position)
        holder.nameTv.text = data.title
        holder.detailsTv.text = data.details
        holder.coverImage.setImageResource(data.imageFile)
    }
}

class UpNextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTv: TextView = itemView.titleTv
    val detailsTv: TextView = itemView.detailsTv
    var coverImage: ImageView = itemView.coverImage
}


class SongModelDiffCallback : DiffUtil.ItemCallback<SongModel>() {
    override fun areItemsTheSame(
        oldItem: SongModel,
        newItem: SongModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SongModel,
        newItem: SongModel
    ): Boolean {
        return oldItem == newItem
    }
}