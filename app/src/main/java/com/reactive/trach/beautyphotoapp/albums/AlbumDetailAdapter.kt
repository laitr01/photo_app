package com.reactive.trach.beautyphotoapp.albums

import android.app.SharedElementCallback
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.data.model.Photo
import com.reactive.trach.beautyphotoapp.utils.DensityUtil
import com.reactive.trach.beautyphotoapp.utils.RxSaveImage

class AlbumDetailAdapter(
        val listener: (Int, Photo, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val photos: ArrayList<Photo> = ArrayList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PhotoHolder.create(parent)
    }

    fun setList(data: List<Photo>) {
        photos.clear()
        photos.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as PhotoHolder
        holder.bind(position, photos[position], photos, listener)
    }

    fun updateItem(photo: Photo) {
        val index = photos.indexOf(photo)
        if (index > -1) {
            photos[index] = photo
            notifyItemRangeChanged(index, 2)
        }
    }

    class PhotoHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.findViewById<ImageView>(R.id.thumbnail)

        fun bind(position: Int, photo: Photo,
                 photos: List<Photo>,
                 listener: (Int, Photo, View) -> Unit) {

            Glide.with(view.context)
                    .load(RxSaveImage.parseImageUrl(photos[position].imageName))
                    .placeholder(R.drawable.img_default_meizi)
                    .error(R.drawable.img_default_meizi)
                    .into(imageView)
            DensityUtil.setViewMargin(itemView, false, 0, 0, 0, 0)

            view.setOnClickListener {
                listener(adapterPosition, photo, imageView)
            }
        }

        companion object {
            fun create(parent: ViewGroup): PhotoHolder {
                return PhotoHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.photo_layout, parent, false))
            }
        }
    }
}