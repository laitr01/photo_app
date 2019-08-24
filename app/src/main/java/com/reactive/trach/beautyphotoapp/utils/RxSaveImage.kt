package com.reactive.trach.beautyphotoapp.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.data.network.APIConfig
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object RxSaveImage {
    private fun saveImageAndGetPathObservable(context: Activity, url: String, title: String): Observable<Uri> {
        return Observable.unsafeCreate(Observable.OnSubscribe<Bitmap> { subscriber ->
            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
                subscriber.onError(Exception("Please check the image path"))
            }
            val appDir = File(Environment.getExternalStorageDirectory(), "Cloud reading album")
            if (appDir.exists()) {
                val fileName = title.replace('/', '-') + ".jpg"
                val file = File(appDir, fileName)
                if (file.exists()) {
                    subscriber.onError(Exception("Image already exists"))
                }
            }
            var bitmap: Bitmap? = null
            try {
                bitmap = Glide.with(context)
                        .load(RxSaveImage.parseImageUrl(url))
                        .asBitmap()
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get()

            } catch (e: Exception) {
                subscriber.onError(e)
            }

            if (bitmap == null) {
                subscriber.onError(Exception("Unable to download to image"))
            }
            subscriber.onNext(bitmap)
            subscriber.onCompleted()
        }).flatMap { bitmap ->
            val appDir = File(Environment.getExternalStorageDirectory(), "Cloud reading album")
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = title.replace('/', '-') + ".jpg"
            val file = File(appDir, fileName)
            try {
                val outputStream = FileOutputStream(file)
                assert(bitmap != null)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val uri = Uri.fromFile(file)
            val scannerIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
            context.sendBroadcast(scannerIntent)
            Observable.just(uri)
        }.subscribeOn(Schedulers.io())
    }


    fun saveImageToGallery(context: Activity, mImageUrl: String, mImageTitle: String) {
        RxSaveImage.saveImageAndGetPathObservable(context, mImageUrl, mImageTitle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uri ->
                    val appDir = File(Environment.getExternalStorageDirectory(), "photoapp")
                    val msg = String.format(context.getString(R.string.picture_has_save_to), appDir.absolutePath)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }) { error ->
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
    }

    fun parseImageUrl(url: String): String{
       // /opt/tomcat/webapps/admin-1.0/upload/1565972407534/4-15658797711061614630749.jpg
        val regex = "/opt/tomcat/webapps"
        if(url.contains(regex)){
            return url.replace(regex, APIConfig.BASE_URL)
        }
        return url
    }
}