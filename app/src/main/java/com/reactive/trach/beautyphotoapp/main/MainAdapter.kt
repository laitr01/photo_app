package com.reactive.trach.beautyphotoapp.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.data.model.Album
import com.reactive.trach.beautyphotoapp.utils.DensityUtil
import com.reactive.trach.beautyphotoapp.utils.RxSaveImage


class MainAdapter(val listener: (Album) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val albums: ArrayList<Album> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AlbumHolder.create(parent)
    }

    fun setList(data: List<Album>) {
        albums.clear()
        albums.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as AlbumHolder
        holder.bind(albums[position])

        holder.view.findViewById<RelativeLayout>(R.id.albumLayout).setOnClickListener {
            listener(albums[position])
        }
    }

    fun updateList(data: List<Album>) {
        albums.addAll(data)
        notifyDataSetChanged()
    }

    class AlbumHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.findViewById(R.id.thumbnail) as ImageView

        fun bind(album: Album) {
            Glide.with(view.context)
                    .load(RxSaveImage.parseImageUrl(album.coverImg))
                    .crossFade(200)
                    .placeholder(R.drawable.img_default_meizi)
                    .error(R.drawable.img_default_meizi)
                    .into(imageView)

            view.findViewById<TextView>(R.id.star_photo_name).text = album.album_name
            if (adapterPosition % 2 == 0) {
                DensityUtil.setViewMargin(itemView, false, 1, 1, 1, 0)
            } else {
                DensityUtil.setViewMargin(itemView, false, 1, 1, 1, 0)
            }

        }

        companion object {
            fun create(parent: ViewGroup): AlbumHolder {
                return AlbumHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.album_layout, parent, false))
            }
        }
    }
}