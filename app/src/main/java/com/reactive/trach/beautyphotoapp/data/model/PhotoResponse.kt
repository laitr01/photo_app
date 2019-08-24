package com.reactive.trach.beautyphotoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotoResponse(
        @Expose
        @SerializedName("album")
        val album: Long,
        @Expose
        @SerializedName("type")
        val type: String,
        @Expose
        @SerializedName("nextOffset")
        val next_offset: Int,
        @Expose
        @SerializedName("items")
        val items: List<Photo>
)
