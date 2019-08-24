package com.reactive.trach.beautyphotoapp.albums

import android.os.Bundle
import com.reactive.trach.beautyphotoapp.SceneParam

data class AlbumDetailParams(val albumId: Long, val title: String): SceneParam {

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putString(TITLE, title)
            putLong(ALBUM_ID, albumId)
        }
    }

    companion object {
        const val ALBUM_ID = "album_id"
        const val TITLE = "title"

        fun fromBundle(args: Bundle): AlbumDetailParams {
            return AlbumDetailParams(
                    args.getLong(ALBUM_ID),
                    args.getString(TITLE)
            )
        }
    }
}