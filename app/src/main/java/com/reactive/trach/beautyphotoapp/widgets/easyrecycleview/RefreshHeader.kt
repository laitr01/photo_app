package com.reactive.trach.beautyphotoapp.widgets.easyrecycleview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.widgets.easyrecycleview.BaseRefreshHeader.Companion.STATE_DONE
import com.reactive.trach.beautyphotoapp.widgets.easyrecycleview.BaseRefreshHeader.Companion.STATE_NORMAL
import com.reactive.trach.beautyphotoapp.widgets.easyrecycleview.BaseRefreshHeader.Companion.STATE_REFRESHING
import com.reactive.trach.beautyphotoapp.widgets.easyrecycleview.BaseRefreshHeader.Companion.STATE_RELEASE_TO_REFRESH
import kotlinx.android.synthetic.main.refresh_header.view.*

class RefreshHeader  : LinearLayout, BaseRefreshHeader {
    private var animationDrawable: AnimationDrawable
    private var mMeasuredHeight: Int = 200
    private var mState = STATE_NORMAL

    init {
        LayoutInflater.from(context).inflate(R.layout.refresh_header, this)
        animationDrawable = img.drawable as AnimationDrawable
        if (animationDrawable.isRunning) {
            animationDrawable.stop()
        }
        //mMeasuredHeight = measuredHeight
        gravity = Gravity.CENTER_HORIZONTAL
        container.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        this.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    @JvmOverloads constructor(context: Context) : super(context)

    override fun getVisiableHeight(): Int =  container.height

    override fun onMove(delta: Float) {
        if (getVisiableHeight() > 0 || delta > 0) {
            setVisiableHeight(delta.toInt() + getVisiableHeight())
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                    if (getVisiableHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH)
                } else {
                    setState(STATE_NORMAL)
                }
            }
        }
    }

    private fun setState(state: Int) {
        if (state == mState) return
        when (state) {
            STATE_NORMAL -> {
                if (animationDrawable.isRunning) {
                    animationDrawable.stop()
                }
                msg.setText(R.string.listview_header_hint_normal)
            }
            STATE_RELEASE_TO_REFRESH -> if (mState != STATE_RELEASE_TO_REFRESH) {
                if (!animationDrawable.isRunning) {
                    animationDrawable.start()
                }
                msg.setText(R.string.listview_header_hint_release)
            }
            STATE_REFRESHING -> msg.setText(R.string.refreshing)
            STATE_DONE -> msg.setText(R.string.refresh_done)
        }
        mState = state
    }
    override fun releaseAction(): Boolean {
        var isOnRefresh = false
        val height = getVisiableHeight()
        if (height == 0)
        // not visible.
            isOnRefresh = false

        if (getVisiableHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING)
            isOnRefresh = true
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        var destHeight = 0 // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight
        }
        smoothScrollTo(destHeight)

        return isOnRefresh
    }

    override fun refreshComplate() {
        setState(STATE_DONE)
        Handler().postDelayed({ reset() }, 500)
    }

    fun reset() {
        smoothScrollTo(0)
        setState(STATE_NORMAL)
    }

    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(getVisiableHeight(), destHeight)
        animator.setDuration(300).start()
        animator.addUpdateListener { animation -> setVisiableHeight(animation.animatedValue as Int) }
        animator.start()
    }

    private fun setVisiableHeight(height: Int) {
        var height = height
        if (height < 0)
            height = 0
        //       `
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        lp.height = height
        container.layoutParams = lp
    }
    fun getState(): Int {
        return mState
    }
}