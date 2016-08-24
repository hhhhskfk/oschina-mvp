package com.hj.app.oschina.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.hj.app.oschina.api.OSChinaApi;
import com.hj.app.oschina.api.ServerAPI;
import com.hj.app.oschina.base.BaseListPresenter;
import com.hj.app.oschina.bean.Banner;
import com.hj.app.oschina.bean.News;
import com.hj.app.oschina.bean.base.PageBean;
import com.hj.app.oschina.bean.base.ResultBean;
import com.hj.app.oschina.ui.fragment.NewsFragment;

import java.util.ArrayList;

import nucleus.presenter.delivery.DeliverFirst;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * Created by huangjie08 on 2016/8/22.
 */
public class NewsPresenter extends BaseListPresenter<NewsFragment> {

    private static final int REQUEST_REMOTE_PAGE_DATA = 1;
    private static final int REQUEST_REMOTE_BANNER = 2;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

    }

    @Override
    protected void initData(final String key) {
        super.initData(key);
        add(afterTakeView().subscribe(new Action1<NewsFragment>() {
            @Override
            public void call(NewsFragment fragment) {
                init(fragment);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        }));

        this.<String, Object, Object, Object>restartable(REQUEST_REMOTE_PAGE_DATA, new Func4<String, Object, Object, Object, Subscription>() {
            @Override
            public Subscription call(String pageToken, Object o, Object o2, Object o3) {
                if (TextUtils.isEmpty(pageToken)) {
                    pageToken = "";
                }
                return ServerAPI.getOSChinaApi().getNewsList(pageToken)
                        .compose(NewsPresenter.this.<ResultBean<PageBean<News>>>deliverFirst())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(split(new Action2<NewsFragment, ResultBean<PageBean<News>>>() {
                            @Override
                            public void call(NewsFragment fragment, ResultBean<PageBean<News>> result) {
                                if (result == null || !result.isSuccess()) {
                                    fragment.executeOnLoadDataError();
                                    return;
                                }
                                fragment.executeOnLoadFinish(result.getResult());
                            }
                        }, new Action2<NewsFragment, Throwable>() {
                            @Override
                            public void call(NewsFragment fragment, Throwable throwable) {
                                throwable.printStackTrace();
                                fragment.executeOnLoadDataError();
                            }
                        }));
            }
        });

        this.restartable(REQUEST_REMOTE_BANNER, new Func0<Subscription>() {
            @Override
            public Subscription call() {
                return ServerAPI.getOSChinaApi().getBannerList(OSChinaApi.CATALOG_BANNER_NEWS)
                        .compose(NewsPresenter.this.<ResultBean<PageBean<Banner>>>deliverFirst())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(split(new Action2<NewsFragment, ResultBean<PageBean<Banner>>>() {
                            @Override
                            public void call(NewsFragment fragment, ResultBean<PageBean<Banner>> resultBean) {
                                fragment.loadBannerList(resultBean);
                            }
                        }, new Action2<NewsFragment, Throwable>() {
                            @Override
                            public void call(NewsFragment fragment, Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }));

            }
        });
    }

    @Override
    public void requestData(String pageToken) {
        start(REQUEST_REMOTE_PAGE_DATA, pageToken, null, null, null);
    }

    private void init(final NewsFragment fragment) {
        this.<PageBean<News>>getCacheFile(fragment.getContext(), CACHE_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PageBean<News>>() {
                    @Override
                    public void call(PageBean<News> pageBean) {
                        if (pageBean == null) {
                            pageBean = new PageBean<>();
                            pageBean.setItems(new ArrayList<News>());
                            fragment.setPageBean(pageBean);
                            fragment.onRefreshing();
                        } else {
                            fragment.executeOnLoadFinish(pageBean);
                        }
                    }
                });
    }

    public void getBannerList() {
        start(REQUEST_REMOTE_BANNER);
    }
}
