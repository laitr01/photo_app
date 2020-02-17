package com.reactive.trach.beautyphotoapp.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reactive.trach.beautyphotoapp.*
import com.reactive.trach.beautyphotoapp.albums.AlbumDetailParams
import com.reactive.trach.beautyphotoapp.data.Response
import com.reactive.trach.beautyphotoapp.data.Status
import com.reactive.trach.beautyphotoapp.widgets.easyrecycleview.EasyRecycleView
import com.reactive.trach.beautyphotoapp.data.model.Album
import com.reactive.trach.beautyphotoapp.data.network.APIConfig
import com.reactive.trach.beautyphotoapp.utils.AppLog
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.loading_view.*
import kotlinx.android.synthetic.main.page_main_fragment.*

class PageMainFragment : Fragment() {

    private lateinit var viewModel: PageMainViewModel
    private lateinit var adapter: MainAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(PageMainViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        (activity as? MainActivity)?.showHome()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setLayoutManager()

        viewModel.dataLive.observe(this, Observer {
            this@PageMainFragment.handleResponse(it)
        })
        viewModel.fetchFirst(mainParams().category, true, APIConfig.ITEMS_REQUEST)
    }

    private fun setLayoutManager() {
        adapter = MainAdapter {
            (activity as MainActivity)
                    .sceneParam()
                    .onNext(AlbumDetailParams(it.albumId, it.album_name))
        }

        val spanSizeLookup = context?.resources?.getInteger(R.integer.span_size) ?: 2
        recycleView.layoutManager = StaggeredGridLayoutManager(spanSizeLookup, StaggeredGridLayoutManager.VERTICAL)
        recycleView.itemAnimator = null

        (recycleView.layoutManager as? StaggeredGridLayoutManager)?.apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        recycleView.setHasFixedSize(true)
        recycleView.adapter = adapter

        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                (recyclerView.layoutManager as? StaggeredGridLayoutManager)?.invalidateSpanAssignments()
            }
        })
        recycleView.setLoadingListener(object : EasyRecycleView.LoadingListener {
            override fun onRefresh() {
                AppLog.debug("PageMainFragment", "onRefresh()")
                viewModel.fetchFirst(mainParams().category, false, APIConfig.ITEMS_REQUEST)
            }

            override fun onLoadMore() {
                AppLog.debug("PageMainFragment", "onLoadMore()")
                viewModel.fetchNext(mainParams().category, viewModel.offset, APIConfig.ITEMS_REQUEST)
            }

        })
    }


    private fun handleResponse(res: Response<List<Album>>?) {
        res?.let {
            val animate = img_progress.drawable as AnimationDrawable
            when (it.status) {
                Status.LOADING -> {
                    switchStateUI(View.GONE, View.VISIBLE, View.GONE)
                    animate.start()
                    adapter.setList(arrayListOf())
                }

                Status.REFRESHING -> {
                    if (it.isFirst) {
                        switchStateUI(View.GONE, View.GONE, View.GONE)
                        animate.stop()
                    } else { }
                }

                Status.EMPTY -> {
                    switchStateUI(View.GONE, View.GONE, View.VISIBLE)
                    recycleView.refreshComplete()
                    animate.stop()
                }

                Status.SUCCEED -> {
                    switchStateUI(View.GONE, View.GONE, View.GONE)
                    recycleView.refreshComplete()
                    animate.stop()
                    it.data?.let { data ->
                        if (!res.isFirst) {
                            adapter.updateList(data)
                        } else {
                            adapter.setList(data)
                        }
                        recycleView.refreshComplete()
                        if (viewModel.offset == 0 && data.size <= 10) {
                            recycleView.noMoreLoading()
                        }

                    }
                }

                Status.FAILED -> {
                    switchStateUI(View.VISIBLE, View.GONE, View.GONE)
                    recycleView.refreshComplete()
                    message.text = getString(R.string.error_notify)
                    img_err.setImageResource(R.drawable.load_err)
                    animate.stop()
                }

                Status.NO_CONNECTION -> {
                    switchStateUI(View.VISIBLE, View.GONE, View.GONE)
                    recycleView.refreshComplete()
                    message.text = getString(R.string.network_lost_notify)
                    img_err.setImageResource(R.drawable.load_err)
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

    private fun mainParams(): MainParam {
        return MainParam.fromBundle(arguments!!)
    }

    companion object {
        fun instance(paramsPage: PageMainParams): PageMainFragment {
            return PageMainFragment().apply {
                arguments = paramsPage.toBundle()
            }
        }
    }
}