package com.reactive.trach.beautyphotoapp.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo(
        @Expose
        @SerializedName("photoId")
        val photoId: Long,
        @Expose
        @SerializedName("albumId")
        val albumId: String,
        @Expose
        @SerializedName("description")
        val description: String,
        @Expose
        @SerializedName("publishedDate")
        val publishedDate: String,
        @Expose
        @SerializedName("categoryId")
        val categoryId: Int,
        @Expose
        @SerializedName("createdById")
        val createdById: Int,
        @Expose
        @SerializedName("createdBy")
        val createdBy: String,
        @Expose
        @SerializedName("tag")
        val tag: String,
        @Expose
        @SerializedName("imageName")
        val imageName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(photoId)
        parcel.writeString(albumId)
        parcel.writeString(description)
        parcel.writeString(publishedDate)
        parcel.writeInt(categoryId)
        parcel.writeInt(createdById)
        parcel.writeString(createdBy)
        if (tag == null) parcel.writeString("") else parcel.writeString(tag)
        parcel.writeString(imageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}