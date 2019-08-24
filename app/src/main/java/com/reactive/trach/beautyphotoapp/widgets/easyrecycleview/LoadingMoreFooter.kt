package com.reactive.trach.beautyphotoapp.widgets.easyrecycleview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.reactive.trach.beautyphotoapp.R
import kotlinx.android.synthetic.main.refresh_footer.view.*

class LoadingMoreFooter @JvmOverloads constructor(context: Context) : LinearLayout(context) {

    companion object {
        val STATE_LOADING = 0
        val STATE_COMPLETE = 1
        val STATE_NO_MORE = 2
    }

    var mAnimationDrawable: AnimationDrawable

    init {
        LayoutInflater.from(context).inflate(R.layout.refresh_footer, this)
        mAnimationDrawable = iv_progress.drawable as AnimationDrawable
        if (!mAnimationDrawable.isRunning) {
            mAnimationDrawable.start()
        }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setState(state: Int) {
        when (state) {
            STATE_LOADING -> {
                if (!mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start()
                }
                iv_progress.visibility = View.VISIBLE
                msg.text = context.getText(R.string.loading)
                this.visibility = View.VISIBLE
            }
            STATE_COMPLETE -> {
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop()
                }
                msg.text = context.getText(R.string.loading)
                this.visibility = View.GONE
            }
            STATE_NO_MORE -> {
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop()
                }
                msg.text = context.getText(R.string.loading)
                iv_progress.visibility = View.GONE
                this.visibility = View.GONE
            }
        }
    }

    fun reset() {
        this.visibility = View.GONE
    }

}