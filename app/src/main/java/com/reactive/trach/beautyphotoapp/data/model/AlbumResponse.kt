package com.reactive.trach.beautyphotoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AlbumResponse(
        @Expose
        @SerializedName("type")
        val type: String,
        @Expose
        @SerializedName("next_offset")
        val nextOffset: Int,
        @Expose
        @SerializedName("items")
        val items: List<Album>
)
