package com.hj.app.oschina.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hj.app.oschina.base.BaseListAdapter;


/**
 * Created by huangjie08 on 2016/8/22.
 */
public class NewsListAdapter extends BaseListAdapter {

    public NewsListAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, int position) {

    }
}
