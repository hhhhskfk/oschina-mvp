package com.hj.app.oschina.util;

import java.util.concurrent.Callable;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by huangjie08 on 2016/8/22.
 */
public class RxUtils {
    public static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(func.call());
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    public static <T> Observable<T> makeObservableWithError(final Callable<T> func,final Throwable throwable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(func.call());
                    subscriber.onError(throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
}

