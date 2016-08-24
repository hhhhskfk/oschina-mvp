package com.hj.app.oschina.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hj.app.oschina.adapter.NewsAdapter;
import com.hj.app.oschina.base.BaseListAdapter;
import com.hj.app.oschina.base.BaseListFragment;
import com.hj.app.oschina.bean.Banner;
import com.hj.app.oschina.bean.News;
import com.hj.app.oschina.bean.base.PageBean;
import com.hj.app.oschina.bean.base.ResultBean;
import com.hj.app.oschina.presenter.NewsPresenter;
import com.hj.app.oschina.widget.ViewNewsHeader;

import nucleus.factory.RequiresPresenter;

import static com.hj.app.oschina.base.BaseListAdapter.*;

/**
 * Created by huangjie08 on 2016/8/19.
 */
@RequiresPresenter(NewsPresenter.class)
public class NewsFragment extends BaseListFragment<News, NewsPresenter> implements OnLoadingHeaderCallback {

    private ViewNewsHeader mHeaderView;
    private boolean isFirst = true;

    @Override
    public void initData() {
        super.initData();
        getPresenter().getBannerList();
    }

    @Override
    protected BaseListAdapter getListAdapter() {
        NewsAdapter adapter = new NewsAdapter(getContext(), ONLY_FOOTER);
        adapter.setOnLoadingHeaderCallBack(this);
        return adapter;
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (!isFirst) {
            getPresenter().getBannerList();
        }
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
        isFirst = false;
    }

    @Override
    protected void requestData() {
        super.requestData();
        getPresenter().requestData(mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken());
    }

    public void loadBannerList(ResultBean<PageBean<Banner>> resultBean) {
        if (resultBean == null || !resultBean.isSuccess()) {
            return;
        }
        //处理下拉刷新时崩溃
        if (mHeaderView == null) {
            mHeaderView = new ViewNewsHeader(getActivity());
            mHeaderView.setRefreshLayout(mSwipeRefreshLayout);
            mAdapter.setMode(BOTH_HEADER_FOOTER);
            mAdapter.notifyItemChanged(0);
        }
        mHeaderView.initData(resultBean.getResult().getItems());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        if (mHeaderView != null) {
            return new BaseListAdapter.HeaderViewHolder(mHeaderView);
        }
        return null;
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
