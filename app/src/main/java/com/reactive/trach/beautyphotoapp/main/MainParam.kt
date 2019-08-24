package com.reactive.trach.beautyphotoapp.main

import android.os.Bundle
import com.reactive.trach.beautyphotoapp.SceneParam

class MainParam(val category: Int): SceneParam {

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putInt(CATEGORY_ID, category)
        }
    }

    companion object {
        const val CATEGORY_ID = "category_id"

        fun fromBundle(args: Bundle): MainParam {
            return MainParam(
                    args.getInt(CATEGORY_ID, -1)
            )
        }
    }

}