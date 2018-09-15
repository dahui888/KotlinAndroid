package com.gfd.crosstalk

import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.gfd.common.ui.fragment.BaseMvpFragment
import com.gfd.crosstalk.adapter.VideoAdapter
import com.gfd.crosstalk.entity.Video
import com.gfd.crosstalk.injection.component.DaggerCrosstalkComponent
import com.gfd.crosstalk.injection.moudle.CrosstalkMoudle
import com.gfd.crosstalk.mvp.contract.CrosstalkContract
import com.gfd.crosstalk.mvp.presenter.CrosstalkPresenter
import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.xiao.nicevideoplayer.NiceVideoPlayerManager
import kotlinx.android.synthetic.main.crosstalk_fragment_crosstalk.*

/**
 * @Author : 郭富东
 * @Date ：2018/9/15 - 10:59
 * @Email：878749089@qq.com
 * @descriptio：
 */
class CrosstalkFragment : BaseMvpFragment<CrosstalkPresenter>() ,CrosstalkContract.View{

    private lateinit var mLRecyclerViewAdapter: LRecyclerViewAdapter
    private lateinit var mAdapter: VideoAdapter
    private var isLoadMore = false
    private var videoParent : ViewGroup? = null
    private var isSwitchVisible = false
    private var page = 1

    override fun injectComponent() {
        DaggerCrosstalkComponent.builder()
                .activityComponent(mActivityComponent)
                .crosstalkMoudle(CrosstalkMoudle(this))
                .build()
                .inject(this)

    }

    override fun getLayoutId(): Int {
        return R.layout.crosstalk_fragment_crosstalk
    }

    override fun initView() {
        mSwipe.setColorSchemeColors(resources.getColor(R.color.common_red))
        mRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = VideoAdapter(activity!!)
        mLRecyclerViewAdapter = LRecyclerViewAdapter(mAdapter)
        mRecycler.adapter = mLRecyclerViewAdapter
        val divider = DividerDecoration.Builder(activity)
                .setHeight(1.toFloat())
                .setPadding(1.toFloat())
                .setColorResource(R.color.common_divider)
                .build()
        mRecycler.setPullRefreshEnabled(false)
        mRecycler.setLoadMoreEnabled(true)
        mRecycler.addItemDecoration(divider)
    }

    override fun initData() {
        mPresenter.getVideoList()
    }

    override fun setListener() {
        mSwipe.setOnRefreshListener {
            page = 1
            mPresenter.getVideoList(page)
        }
        mRecycler.setOnLoadMoreListener {
            if(page > 10){
                mRecycler.setNoMore(true)
            }
            isLoadMore = true
            page++
            mPresenter.getVideoList(page)
        }
    }

    override fun showVideoList(datas: List<Video>) {
        if(isLoadMore){
            isLoadMore = false
            mAdapter.addAll(datas)
            mRecycler.refreshComplete(0)
        }else{
            mAdapter.updateData(datas)
            mSwipe.isRefreshing = false
        }
        mLRecyclerViewAdapter.notifyDataSetChanged()
    }

}