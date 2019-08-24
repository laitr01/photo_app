package com.reactive.trach.beautyphotoapp.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reactive.trach.beautyphotoapp.R
import com.reactive.trach.beautyphotoapp.data.Response
import com.reactive.trach.beautyphotoapp.data.Status
import com.reactive.trach.beautyphotoapp.data.model.Category
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.loading_view.*
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ViewPagerAdapter

    private val disposable = CompositeDisposable()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.setupWithViewPager(viewPager)
        adapter = ViewPagerAdapter(childFragmentManager)

        viewPager.adapter = adapter

        viewModel.dataLive().observe(this, Observer { this@MainFragment.handleResponse(it) })

    }

    private fun handleResponse(res: Response<List<Category>>?) {
        res?.let {
            val animate = img_progress.drawable as AnimationDrawable
            when (it.status) {
                Status.LOADING -> {
                    switchStateUI(View.GONE, View.VISIBLE, View.GONE)
                    animate.start()
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
                        val index = MainParam.fromBundle(arguments!!).category
                        if(index > -1){
                            viewPager.setCurrentItem(index, true)
                        }
                    }
                }

                Status.FAILED -> {
                    switchStateUI(View.VISIBLE, View.GONE, View.GONE)
                    message.text = getString(R.string.error_notify)
                    img_err.setImageResource(R.drawable.load_err)
                    animate.stop()
                }

                Status.NO_CONNECTION -> {
                    switchStateUI(View.VISIBLE, View.GONE, View.GONE)
                    message.text = getString(R.string.network_lost_notify)
                    img_err.setImageResource(R.drawable.load_err)
                    animate.stop()
                }
            }
        }
    }

    private fun switchStateUI(error: Int, loading: Int, empty: Int) {
        vs_loading?.visibility = loading
        vs_error?.visibility = error
        vs_empty?.visibility = empty
    }
    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private val catList: ArrayList<Category> = ArrayList()
        private var mainPage: PageMainFragment? = null

        override fun getItem(position: Int): Fragment {
            return PageMainFragment.instance(PageMainParams(
                    catList[position].catId,
                    catList[position].catName
            ))
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return catList[position].catName
        }

        override fun getCount(): Int {
            return catList.size
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            mainPage = `object` as PageMainFragment
        }

        fun setList(data: List<Category>) {
            catList.clear()
            catList.addAll(data)
            notifyDataSetChanged()
        }

    }

    companion object {
        fun instance(params: MainParam): MainFragment {
            return MainFragment().apply {
                arguments = params.toBundle()
            }
        }
    }
}