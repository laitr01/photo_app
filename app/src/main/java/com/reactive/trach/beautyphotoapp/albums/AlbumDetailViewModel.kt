package com.reactive.trach.beautyphotoapp.albums

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.reactive.trach.beautyphotoapp.PhotoApplication
import com.reactive.trach.beautyphotoapp.utils.AppLog
import com.reactive.trach.beautyphotoapp.data.Response
import com.reactive.trach.beautyphotoapp.data.model.Photo
import com.reactive.trach.beautyphotoapp.data.network.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AlbumDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String by lazy { AlbumDetailViewModel::javaClass.name }

    private val compositeDisposable = CompositeDisposable()
    private val networkRepository: NetworkRepository
    private val response = MutableLiveData<Response<List<Photo>>>()
    fun dataLive(): LiveData<Response<List<Photo>>> = response

    var mOffset: Int = 0

    init {
        val appComponent = PhotoApplication.instance.getAppComponent()
        networkRepository = appComponent.networkRepository()
    }

    fun getPhotos(albumId: Long, offset: Int, limit: Int) {
        compositeDisposable.add(
                networkRepository.getPhotos(albumId, offset, limit)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { response.value = Response.loading(false) }
                        .subscribe({ res ->
                            AppLog.debug(TAG, res.type)
                            mOffset = res.next_offset
                            val displayData = res.items
                            response.value = Response.succeed(displayData, false)
                        }, { t: Throwable ->
                            AppLog.error(TAG, t.stackTrace.toString())
                            response.value = Response.error(t)
                        })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}