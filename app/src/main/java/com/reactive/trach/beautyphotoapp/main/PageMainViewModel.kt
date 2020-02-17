package com.reactive.trach.beautyphotoapp.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reactive.trach.beautyphotoapp.PhotoApplication
import com.reactive.trach.beautyphotoapp.data.NoNetworkException
import com.reactive.trach.beautyphotoapp.utils.AppLog
import com.reactive.trach.beautyphotoapp.data.Response
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import com.reactive.trach.beautyphotoapp.data.model.Album
import com.reactive.trach.beautyphotoapp.data.network.NetworkRepository

class PageMainViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG: String by lazy { PageMainViewModel::javaClass.name }
    private val compositeDisposable = CompositeDisposable()
    private val networkRepository: NetworkRepository

    private val _dataLive = MutableLiveData<Response<List<Album>>>()
    val dataLive: LiveData<Response<List<Album>>>
        get() = _dataLive

    var offset: Int = 0

    init {
        val appComponent = PhotoApplication.instance.getAppComponent()
        networkRepository = appComponent.networkRepository()
    }

    fun fetchFirst(cateId: Int, isFirst: Boolean, limit: Int) {
        getAlbums(cateId, 0, limit, isFirst)
    }

    fun fetchNext(cateId: Int, offset: Int, limit: Int) {
        if (offset > 0) getAlbums(cateId, offset, limit, false)
    }

    private fun getAlbums(catID: Int, offset: Int, limit: Int, isFirst: Boolean) {
        compositeDisposable.add(
                networkRepository.getAlbums(catID, offset, limit)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            _dataLive.value =
                                    if (isFirst) Response.loading(isFirst) else Response.refreshing()
                        }
                        .subscribe({ res ->
                            AppLog.error(TAG, res.type)
                            this.offset = res.nextOffset
                            _dataLive.value =
                                    if (res.items.isEmpty()) Response.empty() else Response.succeed(res.items, isFirst)
                        }, { t: Throwable ->
                            AppLog.error(TAG, t.stackTrace.toString())
                            _dataLive.value = when (t) {
                                is NoNetworkException -> Response.networkLost()
                                else -> Response.error(t)
                            }
                        })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}