package com.reactive.trach.beautyphotoapp.widgets.easyrecycleview

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

class WrapAdapter(
        val headerViews: SparseArray<View>,
        val footViews: SparseArray<View>,
        val adapter: androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>
) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private val TYPE_REFRESH_HEADER = -5
    private val TYPE_HEADER = -4
    private val TYPE_NORMAL = 0
    private val TYPE_FOOTER = -3

    private var headerPosition = 1

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is androidx.recyclerview.widget.GridLayoutManager) {
            val gridManager = manager
            gridManager.spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeader(position) || isFooter(position))
                        gridManager.spanCount
                    else
                        1
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp != null
                && lp is androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.layoutPosition) || isFooter(holder.layoutPosition))) {
            lp.isFullSpan = true
        }
    }

    fun isHeader(position: Int): Boolean {
        return position >= 0 && position < headerViews.size()
    }

    fun isFooter(position: Int): Boolean {
        return position < itemCount && position >= itemCount - footViews.size()
    }

    fun isRefreshHeader(position: Int): Boolean {
        return position == 0
    }

    fun getHeadersCount(): Int {
        return headerViews.size()
    }

    fun getFootersCount(): Int {
        return footViews.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        if (viewType == TYPE_REFRESH_HEADER) {
            return SimpleViewHolder(headerViews.get(0))
        } else if (viewType == TYPE_HEADER) {
            return SimpleViewHolder(headerViews.get(headerPosition++))
        } else if (viewType == TYPE_FOOTER) {
            return SimpleViewHolder(footViews.get(0))
        }
        return adapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position)) {
            return
        }
        val adjPosition = position - getHeadersCount()
        val adapterCount: Int = adapter.itemCount
        if (adjPosition < adapterCount) {
            adapter.onBindViewHolder(holder, adjPosition)
            return
        }
    }

    override fun getItemCount(): Int {
        return if (adapter != null) {
            getHeadersCount() + getFootersCount() + adapter.itemCount
        } else {
            getHeadersCount() + getFootersCount()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isRefreshHeader(position)) {
            return TYPE_REFRESH_HEADER
        }
        if (isHeader(position)) {
            return TYPE_HEADER
        }
        if (isFooter(position)) {
            return TYPE_FOOTER
        }
        val adjPosition = position - getHeadersCount()
        val adapterCount: Int
        if (adapter != null) {
            adapterCount = adapter.itemCount
            if (adjPosition < adapterCount) {
                return adapter.getItemViewType(adjPosition)
            }
        }
        return TYPE_NORMAL
    }

    override fun getItemId(position: Int): Long {
        if (position >= getHeadersCount()) {
            val adjPosition = position - getHeadersCount()
            val adapterCount = adapter.itemCount
            if (adjPosition < adapterCount) {
                return adapter.getItemId(adjPosition)
            }
        }
        return -1
    }

    override fun unregisterAdapterDataObserver(observer: androidx.recyclerview.widget.RecyclerView.AdapterDataObserver) {
        adapter.unregisterAdapterDataObserver(observer)
    }

    override fun registerAdapterDataObserver(observer: androidx.recyclerview.widget.RecyclerView.AdapterDataObserver) {
        adapter.registerAdapterDataObserver(observer)
    }

    private inner class SimpleViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)

}