package com.reactive.trach.beautyphotoapp.widgets.easyrecycleview

internal interface BaseRefreshHeader {

    fun getVisiableHeight(): Int

    fun onMove(delta: Float)

    fun releaseAction(): Boolean

    fun refreshComplate()

    companion object {
        const val STATE_NORMAL = 0
        const val STATE_RELEASE_TO_REFRESH = 1
        const val STATE_REFRESHING = 2
        const val STATE_DONE = 3
    }
}
