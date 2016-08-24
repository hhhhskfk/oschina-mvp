package com.hj.app.oschina.ui.fragment.viewpager;

import android.support.v4.app.Fragment;

import com.hj.app.oschina.R;
import com.hj.app.oschina.adapter.ViewPageFragmentAdapter;
import com.hj.app.oschina.base.BaseViewPagerFragment;
import com.hj.app.oschina.interf.OnTabReselectListener;
import com.hj.app.oschina.ui.fragment.BlogFragment;
import com.hj.app.oschina.ui.fragment.EventFragment;
import com.hj.app.oschina.ui.fragment.MyInformationFragment;
import com.hj.app.oschina.ui.fragment.NewsFragment;
import com.hj.app.oschina.ui.fragment.QuestionFragment;

/**
 * Created by huangjie08 on 2016/8/18.
 */
public class GeneralViewPagerFragment extends BaseViewPagerFragment implements OnTabReselectListener {

    @Override
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.general_viewpage_arrays);

        adapter.addTab(title[0], "news", NewsFragment.class, null);
        adapter.addTab(title[1], "latest_blog", MyInformationFragment.class, null);
        adapter.addTab(title[2], "question", MyInformationFragment.class, null);
        adapter.addTab(title[3], "activity", MyInformationFragment.class, null);
    }


    @Override
    public void onTabReselect() {
        Fragment fragment = mTabsAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment != null) {

        }
    }
}
