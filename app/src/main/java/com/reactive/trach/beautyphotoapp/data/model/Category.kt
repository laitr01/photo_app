package com.reactive.trach.beautyphotoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Category(
        @Expose
        @SerializedName("cat_id")
        val catId: Int,
        @Expose
        @SerializedName("cat_name")
        val catName: String,
        @Expose
        @SerializedName("cat_img")
        val catImg: String
)