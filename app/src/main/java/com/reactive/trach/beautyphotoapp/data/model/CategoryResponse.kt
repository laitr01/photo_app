package com.reactive.trach.beautyphotoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CategoryResponse(
        @Expose
        @SerializedName("type")
        val type: String,
        @Expose
        @SerializedName("items")
        val items: List<Category>
)
