package com.reactive.trach.beautyphotoapp

import android.app.Application
import com.reactive.trach.beautyphotoapp.data.network.*
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(val app: Application) {



    @Provides
    @Singleton
    fun photoInterceptor(app: Application): PhotoInterceptor {
        return PhotoInterceptor(app)
    }

    @Provides
    fun app(): Application {
        return app
    }

    @Provides
    @Singleton
    fun photoServiceApi(app: Application, photoInterceptor: PhotoInterceptor): PhotoServiceApi {

        val client = OkHttpClient.Builder()
                .addInterceptor(photoInterceptor)
                .build()

        return Retrofit.Builder()
                .client(client)
                .baseUrl(APIConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(PhotoServiceApi::class.java)
    }

    @Provides
    @Singleton
    fun networkRepository(app: Application, photoServiceApi: PhotoServiceApi): NetworkRepository {
        return NetworkRepositoryImpl(photoServiceApi)
    }

}