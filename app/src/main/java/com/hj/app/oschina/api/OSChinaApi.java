package com.hj.app.oschina.api;

import com.hj.app.oschina.bean.Banner;
import com.hj.app.oschina.bean.News;
import com.hj.app.oschina.bean.base.PageBean;
import com.hj.app.oschina.bean.base.ResultBean;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by huangjie08 on 2016/8/22.
 */
public interface OSChinaApi {

    public static final int CATALOG_BANNER_NEWS = 1; // 资讯Banner
    public static final int CATALOG_BANNER_BLOG = 2; // 博客Banner
    public static final int CATALOG_BANNER_EVENT = 3; // 活动Banner

    @GET("action/apiv2/news")
    Observable<ResultBean<PageBean<News>>> getNewsList(@Query("pageToken") String pageToken);

    @GET("action/apiv2/banner")
    Observable<ResultBean<PageBean<Banner>>> getBannerList(@Query("catalog") int catalog);
}
