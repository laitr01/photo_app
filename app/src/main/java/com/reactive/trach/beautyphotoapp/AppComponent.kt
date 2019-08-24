package com.reactive.trach.beautyphotoapp

import android.app.Application
import com.reactive.trach.beautyphotoapp.data.network.NetworkRepository
import com.reactive.trach.beautyphotoapp.data.network.PhotoInterceptor
import com.reactive.trach.beautyphotoapp.data.network.PhotoServiceApi
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: Application)

    fun networkRepository(): NetworkRepository

    fun photoServiceApi(): PhotoServiceApi

    fun photoInterceptor(): PhotoInterceptor
}