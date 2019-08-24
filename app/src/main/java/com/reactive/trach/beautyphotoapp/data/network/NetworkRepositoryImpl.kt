package com.reactive.trach.beautyphotoapp.data.network

import com.reactive.trach.beautyphotoapp.data.NoNetworkException
import com.reactive.trach.beautyphotoapp.data.model.AlbumResponse
import com.reactive.trach.beautyphotoapp.data.model.CategoryResponse
import com.reactive.trach.beautyphotoapp.data.model.PhotoResponse
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

class NetworkRepositoryImpl(private val photoServiceApi: PhotoServiceApi) : NetworkRepository {

    private val RETRIES_LIMIT: Int = 3

    override fun getCategories(): Single<CategoryResponse> {
        return photoServiceApi.getCategories()
                .retryWhen { retry(it) }
    }

    override fun getAlbums(catId: Int, offset: Int, limit: Int): Single<AlbumResponse> {
        return photoServiceApi.getAlbums(catId, offset, limit)
                .retryWhen { retry(it) }
    }

    override fun getPhotos(albumId: Long, offset: Int, limit: Int): Single<PhotoResponse> {
        return photoServiceApi.getPhotos(albumId, offset, limit)
                .retryWhen { retry(it) }
    }

    private fun retry(error: Flowable<Throwable>): Flowable<Long>? {

        return error.zipWith(
                Flowable.range(1, 3),
                BiFunction { t: Throwable, retryCount: Int ->
                    if (t is NoNetworkException || retryCount > RETRIES_LIMIT) {
                        throw t
                    } else {
                        retryCount
                    }
                }
        ).flatMap { retryCount: Int ->
            Flowable.timer(
                    Math.pow(2.0, retryCount.toDouble()).toLong(),
                    TimeUnit.SECONDS
            )
        }
    }

}