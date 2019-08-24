package com.reactive.trach.beautyphotoapp.main

import android.os.Bundle
import com.reactive.trach.beautyphotoapp.SceneParam

class PageMainParams(val catId: Int, val title: String): SceneParam {

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putString(TITLE, title)
            putInt(CATEGORY_ID, catId)
        }
    }

    companion object {
        const val CATEGORY_ID = "category_id"
        const val TITLE = "title"

        fun fromBundle(args: Bundle): PageMainParams {
            return PageMainParams(
                    args.getInt(CATEGORY_ID),
                    args.getString(TITLE)
            )
        }
    }
}