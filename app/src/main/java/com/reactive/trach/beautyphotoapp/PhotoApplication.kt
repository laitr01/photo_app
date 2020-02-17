package com.reactive.trach.beautyphotoapp

import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import com.squareup.leakcanary.LeakCanary
import androidx.multidex.MultiDex
import com.jakewharton.threetenabp.AndroidThreeTen

class PhotoApplication: MultiDexApplication() {


    lateinit var component: AppComponent

    fun getAppComponent(): AppComponent {
        return component
    }

    companion object {
        lateinit var instance: PhotoApplication private set
    }
    operator fun get(context: Context): PhotoApplication {
        return context.applicationContext as PhotoApplication
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        //
        // DI
        //
        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        component.inject(this)

        AndroidThreeTen.init(this)
        LeakCanary.install(this)
        initTextSize()

    }

    private fun initTextSize() {
        val res = resources
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
    }
}