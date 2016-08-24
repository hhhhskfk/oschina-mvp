package com.hj.app.oschina.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hj.app.oschina.R;
import com.hj.app.oschina.bean.Banner;
import com.hj.app.oschina.cache.ImageLoader;

/**
 * Created by huangjie08 on 2016/8/23.
 */
public class ViewNewsBanner extends RelativeLayout implements View.OnClickListener {
    private Banner banner;
    private ImageView iv_banner;
    //private TextView tv_title;

    public ViewNewsBanner(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_news_banner, this, true);
        iv_banner = (ImageView) findViewById(R.id.iv_banner);
        //tv_title = (TextView) findViewById(R.id.tv_title);
        setOnClickListener(this);
    }

    public void initData(Banner banner) {
        this.banner = banner;
        //tv_title.setText(banner.getName());
        ImageLoader.loadImage(iv_banner, banner.getImg());
    }

    @Override
    public void onClick(View v) {
        if (banner != null) {
            int type = banner.getType();
            long id = banner.getId();
            //UIHelper.showDetail(getContext(), type, id,banner.getHref());
        }
    }


    public String getTitle() {
        return banner.getName();
    }
}
