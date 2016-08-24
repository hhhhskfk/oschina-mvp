package com.hj.app.oschina.adapter;

import android.os.Bundle;

/**
 * Created by huangjie08 on 2016/8/19.
 */
public final class ViewPageInfo {

    public final String tag;
    public final Class<?> clss;
    public final Bundle args;
    public final String title;

    public ViewPageInfo(String _title, String _tag, Class<?> _class, Bundle _args) {
        title = _title;
        tag = _tag;
        clss = _class;
        args = _args;
    }
}
