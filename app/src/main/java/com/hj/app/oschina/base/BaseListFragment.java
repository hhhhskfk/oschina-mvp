package com.hj.app.oschina.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.hj.app.oschina.AppContext;
import com.hj.app.oschina.R;
import com.hj.app.oschina.bean.base.PageBean;
import com.hj.app.oschina.util.StringUtils;
import com.hj.app.oschina.widget.EmptyLayout;
import com.hj.app.oschina.widget.RecyclerRefreshLayout;
import com.hj.app.oschina.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by huangjie08 on 2016/8/19.
 */
public abstract class BaseListFragment<T, P extends BaseListPresenter> extends BaseFragment<P> implements
            RecyclerRefreshLayout.SuperRefreshLayoutListener {

    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    protected String CACHE_NAME = getClass().getName();
    protected PageBean<T> mBean;

    @BindView(R.id.swipeRefreshLayout)
    protected RecyclerRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    protected RecyclerView mListView;
    @BindView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;
    protected boolean mIsRefresh;
    protected BaseListAdapter<T> mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Override
    public void initView(View root) {
        mSwipeRefreshLayout.setSuperRefreshLayoutListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState = STATE_REFRESH;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getPresenter().requestData(mBean.getPrevPageToken());
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mListView.setLayoutManager(linearLayoutManager);
        if (mAdapter == null) {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onTimeRefresh() && mBean != null) {
            onRefreshing();
        }
    }

    // 是否到时间去刷新数据了
    private boolean onTimeRefresh() {
        String lastRefreshTime = AppContext.getLastRefreshTime(CACHE_NAME);
        String currTime = StringUtils.getCurTimeStr();
        long diff = StringUtils.calDateDifferent(lastRefreshTime, currTime);
        return needAutoRefresh() && diff > getAutoRefreshTime();
    }

    /***
     * 自动刷新的时间
     * <p>
     * 默认：自动刷新的时间为半天时间
     *
     * @return
     * @author 火蚁 2015-2-9 下午5:55:11
     */
    protected long getAutoRefreshTime() {
        return 12 * 60 * 60;
    }

    // 是否需要自动刷新
    protected boolean needAutoRefresh() {
        return true;
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    @Override
    public void initData() {
        getPresenter().initData(CACHE_NAME);
    }

    /**
     * 设置顶部正在加载的状态
     */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.onComplete();
        }
    }

    /**
     * 是否需要隐藏listview，显示无数据状态
     *
     * @author 火蚁 2015-1-27 下午6:18:59
     */
    protected boolean needShowEmptyNoData() {
        return true;
    }

    protected void setListData(PageBean<T> pageBean) {
        if (mBean == null) {
            mBean = new PageBean<>();
            mBean.setItems(new ArrayList<T>());
        }
        mBean.setNextPageToken(pageBean.getNextPageToken());
        if (mIsRefresh) {
            mAdapter.clear();
        }
        if (mAdapter.getDataSize() + pageBean.getItems().size() == 0) {
            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            }
            mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
            return;
        } else if (pageBean.getItems().size() < 20) {
            mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
        } else {
            mAdapter.setState(BaseListAdapter.STATE_LOAD_MORE);
        }

        if (mIsRefresh) {
            mBean.setItems(pageBean.getItems());
            mAdapter.addItems(0, mBean.getItems());
            mBean.setPrevPageToken(pageBean.getPrevPageToken());
            mSwipeRefreshLayout.setCanLoadMore(true);
            getPresenter().saveCacheFile(getActivity(), mBean, CACHE_NAME);
            AppContext.putToLastRefreshTime(CACHE_NAME, StringUtils.getCurTimeStr());
        } else {
            mAdapter.addItems(pageBean.getItems());
        }
        if (mAdapter.getDataSize() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    }

    public void executeOnLoadFinish(PageBean<T> resultBean) {
        setListData(resultBean);
        onRequestFinish();
    }

    public void executeOnLoadDataError() {
        if (mAdapter.getDataSize() == 0) {
            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
            mAdapter.setState(BaseListAdapter.STATE_LOAD_ERROR);
        } else {
            Toast.makeText(getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
        onRequestFinish();
    }

    protected void onRequestFinish() {
        setSwipeRefreshLoadedState();
        mIsRefresh = false;
        mState = STATE_NONE;
    }

    public void setPageBean(PageBean<T> bean) {
        this.mBean = bean;
    }

    public PageBean<T> getPageBean() {
        return mBean;
    }

    /**
     * request network data
     */
    protected void requestData() {
        mState = STATE_REFRESH;
        setSwipeRefreshLoadingState();
    }

    @Override
    public void onRefreshing() {
        if (mState == STATE_REFRESH)
            return;

        mIsRefresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        requestData();
    }
}
