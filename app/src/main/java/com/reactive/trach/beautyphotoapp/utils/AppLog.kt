package com.reactive.trach.beautyphotoapp.utils

import android.util.Log
import com.reactive.trach.beautyphotoapp.BuildConfig

object AppLog {
    fun error(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun debug(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }
}