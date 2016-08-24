package com.hj.app.oschina.base;

import android.content.Context;

import com.hj.app.oschina.cache.CacheManager;
import com.hj.app.oschina.util.RxUtils;
import com.hj.app.oschina.util.TLog;

import java.io.Serializable;
import java.util.concurrent.Callable;

import rx.Observable;
import nucleus.presenter.RxPresenter;
import rx.functions.Action1;

/**
 * Created by huangjie08 on 2016/8/19.
 */
public abstract class BaseListPresenter<View> extends RxPresenter<View> {

    protected String CACHE_NAME = "";

    protected void initData(String key) {
        CACHE_NAME = key;
    }

    /**
     * 获取请求数据
     * @param pageToken
     */
    public abstract void requestData(String pageToken);

    public <T> Observable<T> getCacheFile(final Context context, final String key) {
        return RxUtils.makeObservable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                if (!CacheManager.isExistDataCache(context, key)) {
                    return null;
                }
                return CacheManager.<T>readObject(context, key);
            }
        });
    }

    public void saveCacheFile(final Context context, final Serializable ser, final String key) {
        RxUtils.makeObservable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return CacheManager.saveObject(context, ser, key);
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (!aBoolean) {
                    TLog.log("缓存文件失败");
                }
            }
        });
    }
}
