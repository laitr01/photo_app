package com.reactive.trach.beautyphotoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Album(
        @Expose
        @SerializedName("albumId")
        val albumId: Long = 0,
        @Expose
        @SerializedName("album_name")
        val album_name: String = "",
        @Expose
        @SerializedName("description")
        val description: String = "",
        @Expose
        @SerializedName("publishedDate")
        val publishedDate: String = "",
        @Expose
        @SerializedName("categoryId")
        val categoryId: Int = 0,
        @Expose
        @SerializedName("createdById")
        val createdById: Int = 0,
        @Expose
        @SerializedName("createdBy")
        val createdBy: String = "",
        @Expose
        @SerializedName("coverImg")
        val coverImg: String = "",
        @Expose
        @SerializedName("time")
        val time: Long = 0
)