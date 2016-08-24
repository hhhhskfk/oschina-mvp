package com.hj.app.oschina.base;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hj.app.oschina.R;
import com.hj.app.oschina.adapter.ViewPageFragmentAdapter;
import com.hj.app.oschina.widget.PagerSlidingTabStrip;

import butterknife.BindView;

/**
 * Created by huangjie08 on 2016/8/18.
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    @BindView(R.id.pager_tabstrip)
    protected PagerSlidingTabStrip mTabStrip;
    @BindView(R.id.pager)
    protected ViewPager mViewPager;

    protected ViewPageFragmentAdapter mTabsAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.base_viewpage_fragment;
    }

    @Override
    public void initView(View root) {
        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(), mTabStrip, mViewPager);
        setScreenPageLimit();
        onSetupTabAdapter(mTabsAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt("position");
            mViewPager.setCurrentItem(pos, true);
        }
    }

    @Override
    public void initData() {

    }

    protected void setScreenPageLimit(){
    }

    protected abstract void onSetupTabAdapter(ViewPageFragmentAdapter adapter);
}
