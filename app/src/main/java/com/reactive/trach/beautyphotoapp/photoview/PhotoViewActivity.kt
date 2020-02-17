/**
 * Created by laivantrach1190@gmail.com
 * Copyright (c) 2020 . All rights reserved.
 */
package com.reactive.trach.beautyphotoapp.photoview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.data.network.APIConfig
import com.reactive.trach.beautyphotoapp.widgets.ElasticDragDismissFrameLayout
import kotlinx.android.synthetic.main.photo_view_layout.*


class PhotoViewActivity : AppCompatActivity() {

    internal lateinit var chromeFader: ElasticDragDismissFrameLayout.SystemChromeFader
    private var sharedViewId: String? = null

    val LayerDrawable.layers: List<Drawable>
        get() = (0 until numberOfLayers).map { getDrawable(it) }

    fun Drawable.getBitmap(): Bitmap? {
        if (this is TransitionDrawable) {
            layers.forEach {
                val bmp = it.getBitmap()
                if (bmp != null) return bmp
            }
        }
        if (this is BitmapDrawable) {
            return bitmap
        } else if (this is GifDrawable) {
            return firstFrame
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_view_layout)

        val url = intent.getStringExtra("photo_url")
        sharedViewId = intent.getStringExtra("RESULT_EXTRA_SHOT_ID")

        if(url != null){
            Glide.with(this)
                    .load(parseImageUrl(url = url.toString()))
                    .placeholder(R.drawable.img_default_meizi)
                    .error(R.drawable.img_default_meizi)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .override(600, 800)
                    .into(photo)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        chromeFader = object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {

            override fun onPreDrag() {
                background.background = null
            }

            override fun onDrag(elasticOffset: Float, elasticOffsetPixels: Float, rawOffset: Float, rawOffsetPixels: Float) {
                if(elasticOffset == 0f && elasticOffsetPixels == 0f && rawOffset == 0f && rawOffsetPixels == 0f ){
                    background.setBackgroundColor(resources.getColor(R.color.colorWhite))
                }
            }
            override fun onDragDismissed() {
                setResultAndFinish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setResultAndFinish() {
        val resultData = Intent().apply {
           putExtra("RESULT_EXTRA_SHOT_ID", sharedViewId)
        }
        setResult(Activity.RESULT_OK, resultData)
        finishAfterTransition()
    }

    override fun onResume() {
        super.onResume()
        draggableFrame.addListener(chromeFader)
    }

    override fun onPause() {
        super.onPause()
        draggableFrame.removeListener(chromeFader)
    }
    fun parseImageUrl(url: String): String{
        // /opt/tomcat/webapps/admin-1.0/upload/1565972407534/4-15658797711061614630749.jpg
        val regex = "/opt/tomcat/webapps"
        if(url.contains(regex)){
            return url.replace(regex, APIConfig.BASE_URL)
        }
        return url
    }

    companion object {
        private const val SCRIM_ADJUSTMENT = 0.075f
    }

}