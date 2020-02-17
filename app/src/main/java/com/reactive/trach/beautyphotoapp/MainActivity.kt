package com.reactive.trach.beautyphotoapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.reactive.trach.beautyphotoapp.main.MainParam
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.disposables.CompositeDisposable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.reactive.trach.beautyphotoapp.albums.AlbumDetailAdapter
import com.reactive.trach.beautyphotoapp.albums.AlbumDetailFragment
import com.reactive.trach.beautyphotoapp.albums.AlbumDetailParams
import com.reactive.trach.beautyphotoapp.data.model.AlbumResponse

class MainActivity : AppCompatActivity() {

    private val REQUEST_WRITE_STORAGE_REQUEST_CODE: Int = 10000

    private val sceneParam: PublishSubject<SceneParam> = PublishSubject.create()
    private val disposable = CompositeDisposable()

    fun sceneParam(): PublishSubject<SceneParam> {
        return sceneParam
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }

        requestAppPermissions()

        disposable.add(sceneParam
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val fm = FragmentFactory.create(it)
                    supportFragmentManager
                            .beginTransaction()
                            .add(R.id.container, fm!!, fm::class.java.name)
                            .addToBackStack(null)
                            .commitAllowingStateLoss()
                })

        val intent = intent
        val bundle = intent.extras

        bundle?.run {
            for (key in bundle.keySet()) {
                //AppLog.error(TAG, key + "->" + bundle.getString(key))
            }

            if (bundle.containsKey("text")) run {

                val albumResponse: AlbumResponse? = try {
                    Gson().fromJson<AlbumResponse>(
                            bundle.getString("text"),
                            AlbumResponse::class.java
                    )
                } catch (e: JsonSyntaxException) {
                    null
                }

                albumResponse?.let {
                    val album = albumResponse.items[0]
                    sceneParam.onNext(AlbumDetailParams(album.albumId, album.album_name))
                } ?: let {
                    sceneParam.onNext(MainParam(0))
                }
            } else {
                sceneParam.onNext(MainParam(0))
            }
        } ?: run {
            sceneParam.onNext(MainParam(0))
        }
    }


    fun hideHome(title: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        ivTitleMenu.setImageDrawable(null)
        ivTitleMenu.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        ivTitleMenu.tag = R.drawable.ic_arrow_back_black_24dp
        iv_title_app_name.text = title
    }

    fun showHome() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        ivTitleMenu.setImageDrawable(null)
        ivTitleMenu.setImageResource(R.drawable.titlebar_menu)
        ivTitleMenu.tag = R.drawable.titlebar_menu
        iv_title_app_name.text = getString(R.string.app_name)
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount > 1 -> supportFragmentManager.popBackStackImmediate()
            supportFragmentManager.backStackEntryCount == 1 -> {
                val fragment = supportFragmentManager.findFragmentByTag("com.reactive.trach.beautyphotoapp.albums.AlbumDetailFragment")
                if (fragment != null && fragment.isVisible) {
                    sceneParam.onNext(MainParam(0))
                } else {
                    supportFragmentManager.popBackStack()
                    finish()
                }
            }
            else -> super.onBackPressed()
        }
    }

    private fun requestAppPermissions() {

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }

        val hasPermission = (ContextCompat.checkSelfPermission(baseContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQUEST_WRITE_STORAGE_REQUEST_CODE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}
