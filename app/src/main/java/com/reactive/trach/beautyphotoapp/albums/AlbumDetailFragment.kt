package com.reactive.trach.beautyphotoapp.albums

import android.app.ActivityOptions
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.reactive.trach.beautyphotoapp.MainActivity
import com.reactive.trach.beautyphotoapp.PinchZoomItemTouchListener
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.data.Response
import com.reactive.trach.beautyphotoapp.data.Status
import com.reactive.trach.beautyphotoapp.data.model.Photo
import com.reactive.trach.beautyphotoapp.data.network.APIConfig
import com.reactive.trach.beautyphotoapp.photoview.PhotoViewActivity
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.loading_view.*
import kotlinx.android.synthetic.main.photo_list_layout.*

class AlbumDetailFragment : Fragment(), PinchZoomItemTouchListener.PinchZoomListener {

    override fun onPinchZoom(position: Int) {
        Toast.makeText(context, "Pinched $position", Toast.LENGTH_SHORT).show();
    }

    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var adapter: AlbumDetailAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(AlbumDetailViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        val params = AlbumDetailParams.fromBundle(arguments!!)
        (activity as? MainActivity)?.hideHome(params.title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.photo_list_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        setLayoutManager()
    }


    private fun setLayoutManager() {
        adapter = AlbumDetailAdapter { pos, photo, imageView -> /* i will handle this in next article */
            val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity!!,
                    imageView,
                    "imageMain"
            )

            startActivity(Intent(context, PhotoViewActivity::class.java).apply {
                putExtra("photo_url", photo.imageName)
                putExtra("RESULT_EXTRA_SHOT_ID", photo.albumId)
            }, options.toBundle())
        }
        val spanSizeLookup = context?.resources?.getInteger(R.integer.span_size) ?: 2
        recycleView.layoutManager = StaggeredGridLayoutManager(spanSizeLookup, StaggeredGridLayoutManager.VERTICAL)
        (recycleView.layoutManager as? StaggeredGridLayoutManager)?.apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        recycleView.setHasFixedSize(true)

        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                (recyclerView.layoutManager as? StaggeredGridLayoutManager)?.invalidateSpanAssignments()
            }
        })

        recycleView.adapter = adapter

        viewModel.dataLive().observe(this, Observer {
            this@AlbumDetailFragment.handleResponse(it)
        })
        viewModel.getPhotos(params().albumId, 0, APIConfig.ITEMS_REQUEST)
    }

    private fun handleResponse(res: Response<List<Photo>>?) {
        res?.let {
            val animate = img_progress.drawable as AnimationDrawable
            when (it.status) {
                Status.LOADING -> {
                    switchStateUI(View.GONE, View.VISIBLE, View.GONE)
                    animate.start()
                    adapter.setList(arrayListOf())
                }

                Status.REFRESHING -> {
                    switchStateUI(View.GONE, View.GONE, View.GONE)
                    animate.stop()
                }

                Status.EMPTY -> {
                    switchStateUI(View.GONE, View.GONE, View.VISIBLE)
                    animate.stop()
                }

                Status.SUCCEED -> {
                    switchStateUI(View.GONE, View.GONE, View.GONE)
                    animate.stop()
                    it.data?.let { data ->
                        adapter.setList(data)

                    }
                }

                Status.FAILED -> {
                    switchStateUI(View.VISIBLE, View.GONE, View.GONE)
                    message.text = getString(R.string.error_notify)
                    animate.stop()
                }

                Status.NO_CONNECTION -> {
                    switchStateUI(View.VISIBLE, View.GONE, View.GONE)
                    message.text = getString(R.string.network_lost_notify)
                    animate.stop()
                }
            }
        }
    }

    private fun switchStateUI(error: Int, loading: Int, empty: Int) {
        ll_progress_bar?.visibility = loading
        vs_error?.visibility = error
        vs_empty?.visibility = empty
    }

    override fun onStop() {
        super.onStop()
        (activity as? MainActivity)?.showHome()
    }

    private fun params(): AlbumDetailParams {
        return AlbumDetailParams.fromBundle(arguments!!)
    }

    companion object {
        fun instance(paramsPage: AlbumDetailParams): AlbumDetailFragment {
            return AlbumDetailFragment().apply {
                arguments = paramsPage.toBundle()
            }
        }
    }
}