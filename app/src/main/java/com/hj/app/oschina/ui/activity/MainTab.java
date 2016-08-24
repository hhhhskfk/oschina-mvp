package com.hj.app.oschina.ui.activity;

import com.hj.app.oschina.R;
import com.hj.app.oschina.ui.fragment.ExploreFragment;
import com.hj.app.oschina.ui.fragment.MyInformationFragment;
import com.hj.app.oschina.ui.fragment.viewpager.GeneralViewPagerFragment;
import com.hj.app.oschina.ui.fragment.viewpager.TweetsViewPagerFragment;

/**
 * Created by huangjie08 on 2016/8/18.
 */
public enum MainTab {

    NEWS(0, R.string.main_tab_name_news, R.drawable.tab_icon_new, GeneralViewPagerFragment.class),
    TWEET(1, R.string.main_tab_name_tweet, R.drawable.tab_icon_tweet, TweetsViewPagerFragment.class),
    QUICK(2, R.string.main_tab_name_quick, R.drawable.tab_icon_new, null),
    EXPLORE(3, R.string.main_tab_name_explore, R.drawable.tab_icon_explore, ExploreFragment.class),
    ME(4, R.string.main_tab_name_my, R.drawable.tab_icon_me, MyInformationFragment.class);

    private int index;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainTab(int index, int resName, int resIcon, Class<?> clz) {
        this.index = index;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
