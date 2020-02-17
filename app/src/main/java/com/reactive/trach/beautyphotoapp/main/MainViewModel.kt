package com.reactive.trach.beautyphotoapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reactive.trach.beautyphotoapp.PhotoApplication
import com.reactive.trach.beautyphotoapp.data.NoNetworkException
import com.reactive.trach.beautyphotoapp.utils.AppLog
import com.reactive.trach.beautyphotoapp.data.Response
import com.reactive.trach.beautyphotoapp.data.model.Category
import com.reactive.trach.beautyphotoapp.data.network.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private val TAG: String by lazy { MainViewModel::javaClass.name }

    private val compositeDisposable = CompositeDisposable()
    private val networkRepository: NetworkRepository

    private val response = MutableLiveData<Response<List<Category>>>()
    fun dataLive(): LiveData<Response<List<Category>>> = response

    init {
        val appComponent = PhotoApplication.instance.getAppComponent()
        networkRepository = appComponent.networkRepository()
        getCategories()
    }

    private fun getCategories() {
        compositeDisposable.add(
                networkRepository.getCategories()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { response.value = Response.loading(false) }
                        .subscribe({ res ->
                            AppLog.debug(TAG, res.type)
                            response.value = if (res.items.isEmpty()) Response.empty() else Response.succeed(res.items, false)
                        }, { t: Throwable ->
                            AppLog.error(TAG, t.stackTrace.toString())
                            response.value = when (t) {
                                is NoNetworkException -> Response.networkLost()
                                else -> Response.error(t)
                            }
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}