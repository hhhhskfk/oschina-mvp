package com.hj.app.oschina.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huangjie08 on 2016/8/23.
 */
public class SuperSwipeRefreshLayout extends SwipeRefreshLayout
        implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mListView;

    private SuperRefreshLayoutListener mListener;

    private boolean mIsOnLoading = false;

    private boolean mCanLoadMore = false;

    public SuperSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public SuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (mListener != null && !mIsOnLoading) {
            mListener.onRefreshing();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mListView == null) {
            getListView();
        }
    }

    private void getListView() {
        int child = getChildCount();
        if (child > 0) {
            View childView = getChildAt(0);
            if (childView instanceof RecyclerView) {
                mListView = (RecyclerView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (canLoad(dy)) {
                            loadData();
                        }
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    }
                });
            }
        }
    }

    public void setCanLoadMore() {
        this.mCanLoadMore = true;
    }

    public void setNoMoreData() {
        this.mCanLoadMore = false;
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return 是否可以加载更多
     */
    private boolean canLoad(int dy) {
        return isInBottom() && !mIsOnLoading && dy > 0 && mCanLoadMore;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mListener != null) {
            setIsOnLoading(true);
            mListener.onLoadMore();
        }
    }


    /**
     * 设置正在加载
     *
     * @param loading loading
     */
    public void setIsOnLoading(boolean loading) {
        mIsOnLoading = loading;
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isInBottom() {
        if (mListView == null || mListView.getAdapter() == null) {
            return false;
        }

        RecyclerView.LayoutManager mLayoutManager = mListView.getLayoutManager();
        int lastVisibleItem = 0;
        if (mLayoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) mLayoutManager;
            lastVisibleItem = manager.findLastVisibleItemPosition();
        } else if(mLayoutManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) mLayoutManager;
            lastVisibleItem = manager.findLastVisibleItemPosition();
        }

        return lastVisibleItem == mListView.getAdapter().getItemCount() - 1;
    }

    /**
     * 加载结束记得调用
     */
    public void onLoadComplete() {
        setIsOnLoading(false);
        setRefreshing(false);
        setEnabled(true);
    }

    /**
     * set
     *
     * @param loadListener loadListener
     */
    public void setSuperRefreshLayoutListener(SuperRefreshLayoutListener loadListener) {
        mListener = loadListener;
    }

    public interface SuperRefreshLayoutListener {
        void onRefreshing();

        void onLoadMore();
    }
}
