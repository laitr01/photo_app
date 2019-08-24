package com.reactive.trach.beautyphotoapp.albums

import android.support.v7.widget.RecyclerView
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
        val listener: (Int, List<String>, List<Photo>, List<String>) -> Unit
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photos: ArrayList<Photo> = ArrayList()

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
        holder.bind(position, photos.map { it.imageName }, photos, photos.map { it.imageName }, listener)
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

        fun bind(position: Int, photos: List<String>,
                 photo: List<Photo>,
                 titles: List<String>,
                 listener: (Int, List<String>, List<Photo>, List<String>) -> Unit) {

            Glide.with(view.context)
                    .load(RxSaveImage.parseImageUrl(photos[position]))
                    .crossFade(500)
                    .placeholder(R.drawable.img_default_meizi)
                    .error(R.drawable.img_default_meizi)
                    .into(imageView)
            DensityUtil.setViewMargin(itemView, false, 0, 0, 0, 0)

            view.setOnClickListener {
                listener(adapterPosition, photos, photo, titles)
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