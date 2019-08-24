package com.reactive.trach.beautyphotoapp.data.network

import com.reactive.trach.beautyphotoapp.data.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoServiceApi {
    @GET("category/")
    fun getCategories(): Single<CategoryResponse>

    @GET("album")
    fun getAlbums(
            @Query("cat_id") catId: Int,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int
    ): Single<AlbumResponse>

    @GET("photo")
    fun getPhotos(
            @Query("album_id") albumId: Long,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int
    ): Single<PhotoResponse>

}