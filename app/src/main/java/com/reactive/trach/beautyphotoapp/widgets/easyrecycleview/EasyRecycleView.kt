package com.reactive.trach.beautyphotoapp.widgets.easyrecycleview

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View

class EasyRecycleView : RecyclerView {
    private lateinit var mLoadingListener: LoadingListener
    private lateinit var mWrapAdapter: WrapAdapter
    private val mHeaderViews = SparseArray<View>()
    private val mFootViews = SparseArray<View>()
    private var pullRefreshEnabled = true
    private var loadingMoreEnabled = true
    private var mRefreshHeader: RefreshHeader? = null
    private var isLoadingData: Boolean = false
    var previousTotal: Int = 0
    var isnomore: Boolean = false
    private var mLastY = -1f
    private val DRAG_RATE = 1.75f
    private var isOther = false

    @JvmOverloads
    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        if (pullRefreshEnabled) {
            val refreshHeader = RefreshHeader(context)
            mHeaderViews.put(0, refreshHeader)
            mRefreshHeader = refreshHeader
        }
        val footView = LoadingMoreFooter(context)
        addFootView(footView, false)
        mFootViews.get(0).visibility = View.GONE
    }

    /**
     * 改为公有。供外添加view使用,使用标识
     * 注意：使用后不能使用 上拉加载，否则添加无效
     * 使用时 isOther 传入 true，然后调用 noMoreLoading即可。
     */
    fun addFootView(view: View, isOther: Boolean) {
        mFootViews.clear()
        mFootViews.put(0, view)
        this.isOther = isOther
    }

    private fun loadMoreComplete() {
        isLoadingData = false
        val footView = mFootViews.get(0)
        if (previousTotal <= layoutManager.itemCount) {
            if (footView is LoadingMoreFooter) {
                footView.setState(LoadingMoreFooter.STATE_COMPLETE)
            } else {
                footView.visibility = View.GONE
            }
        } else {
            if (footView is LoadingMoreFooter) {
                footView.setState(LoadingMoreFooter.STATE_NO_MORE)
            } else {
                footView.visibility = View.GONE
            }
            isnomore = true
        }
        previousTotal = layoutManager.itemCount
    }

    fun noMoreLoading() {
        isLoadingData = false
        val footView = mFootViews.get(0)
        isnomore = true
        if (footView is LoadingMoreFooter) {
            footView.setState(LoadingMoreFooter.STATE_NO_MORE)
        } else {
            footView.visibility = View.GONE
        }
        // 额外添加的footView
        if (isOther) {
            footView.visibility = View.VISIBLE
        }
    }

    fun refreshComplete() {
        //  mRefreshHeader.refreshComplate();
        if (isLoadingData) {
            loadMoreComplete()
        } else {
            mRefreshHeader?.refreshComplate()
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        mWrapAdapter = WrapAdapter(mHeaderViews, mFootViews, adapter)
        super.setAdapter(mWrapAdapter)
        adapter.registerAdapterDataObserver(mDataObserver)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            val layoutManager = layoutManager
            val lastVisibleItemPosition: Int
            if (layoutManager is GridLayoutManager) {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            } else if (layoutManager is StaggeredGridLayoutManager) {
                val into = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(into)
                lastVisibleItemPosition = findMax(into)
            } else {
                lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            if (layoutManager.childCount > 0
                    && lastVisibleItemPosition >= layoutManager.itemCount - 1
                    && layoutManager.itemCount > layoutManager.childCount
                    && !isnomore
                    && mRefreshHeader!!.getState() < BaseRefreshHeader.STATE_REFRESHING) {

                val footView = mFootViews.get(0)
                isLoadingData = true
                if (footView != null) {
                    if (footView is LoadingMoreFooter) {
                        footView.setState(LoadingMoreFooter.STATE_LOADING)
                    } else {
                        footView.visibility = View.VISIBLE
                    }
                }
                mLoadingListener.onLoadMore()
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = ev.rawY
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mLastY = ev.rawY
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.rawY - mLastY
                mLastY = ev.rawY
                if (isOnTop() && pullRefreshEnabled) {
                    mRefreshHeader!!.onMove(deltaY / DRAG_RATE)
                    if (mRefreshHeader!!.getVisiableHeight() > 0 && mRefreshHeader!!.getState() < BaseRefreshHeader.STATE_REFRESHING) {
                        return false
                    }
                }
            }
            else -> {
                mLastY = -1f // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mRefreshHeader!!.releaseAction()) {
                        mLoadingListener.onRefresh()
                        isnomore = false
                        previousTotal = 0
                        val footView = mFootViews.get(0)
                        if (footView is LoadingMoreFooter) {
                            if (footView.getVisibility() != View.GONE) {
                                footView.setVisibility(View.GONE)
                            }
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    private fun findMin(firstPositions: IntArray): Int {
        var min = firstPositions[0]
        for (value in firstPositions) {
            if (value < min) {
                min = value
            }
        }
        return min
    }

    fun isOnTop(): Boolean {
        if (mHeaderViews.size() == 0) {
            return false
        }

        val view = mHeaderViews.get(0)
        return view.parent != null
    }

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition)
        }
    }


    fun setLoadingListener(listener: LoadingListener) {
        mLoadingListener = listener
    }


    interface LoadingListener {

        fun onRefresh()

        fun onLoadMore()
    }

}