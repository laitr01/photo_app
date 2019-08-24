package com.reactive.trach.beautyphotoapp.data.network

import com.reactive.trach.beautyphotoapp.data.model.AlbumResponse
import com.reactive.trach.beautyphotoapp.data.model.CategoryResponse
import com.reactive.trach.beautyphotoapp.data.model.PhotoResponse
import io.reactivex.Single

interface NetworkRepository {

    fun getCategories(): Single<CategoryResponse>

    fun getAlbums(catId: Int, offset: Int, limit: Int): Single<AlbumResponse>

    fun getPhotos( albumId: Long, offset: Int, limit: Int): Single<PhotoResponse>

}