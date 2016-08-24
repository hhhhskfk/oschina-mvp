package com.hj.app.oschina.base;

import com.hj.app.oschina.interf.OnTabReselectListener;

/**
 * Created by huangjie08 on 2016/8/22.
 */
public abstract class BaseGeneralListFragment<T, P extends BaseListPresenter> extends BaseListFragment<T, P>
                        implements OnTabReselectListener {

    @Override
    public void onTabReselect() {
        mListView.smoothScrollToPosition(0);
        mSwipeRefreshLayout.setRefreshing(true);
        onRefreshing();
    }
}
