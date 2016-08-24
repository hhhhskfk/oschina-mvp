package com.hj.app.oschina.api;

import android.content.SharedPreferences;

import com.hj.app.oschina.AppContext;
import com.hj.app.oschina.common.AppConfig;
import com.hj.app.oschina.common.SharePreferencesManager;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by huangjie08 on 2016/8/22.
 */
public class ServerAPI {

    private static ServerAPI instance;
    private static OSChinaApi osChinaApi;
    private OkHttpClient httpClient;
    private static String cookies;

    public static ServerAPI init() {
        if (instance == null) {
            instance = new ServerAPI();
        }
        return instance;
    }

    public static OSChinaApi getOSChinaApi() {
        return osChinaApi;
    }

    private ServerAPI() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Accept-Language", Locale.getDefault().toString())
                            .header("Host", "www.oschina.net")
                            .header("Connection", "Keep-Alive")
                            .header("Cookie", cookies == null ? getCookies() : cookies)
                            .header("User-Agent", getUserAgent())
                            .build();
                    return chain.proceed(request);
                }
            });
            httpClient.setConnectTimeout(5000, TimeUnit.MILLISECONDS);

            osChinaApi = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl("http://www.oschina.net/")
                    .client(httpClient)
                    .build()
                    .create(OSChinaApi.class);

        }
    }

    private static String getCookies(){
        if (cookies == null) {
            SharedPreferences preferences = SharePreferencesManager.getPreferences();
            return cookies = preferences.getString(AppConfig.CONF_COOKIE, "");
        }
        return cookies;
    }

    public static String getUserAgent() {
        return new StringBuilder("OSChina.NET")
                .append('/' + AppContext.getInstance().getPackageInfo().versionName + '_' + AppContext.getInstance().getPackageInfo().versionCode)
                .append("/Android")
                .append("/" + android.os.Build.VERSION.RELEASE)// 手机系统版本
                .append("/" + android.os.Build.MODEL) // 手机型号
                .append("/" + AppContext.getInstance().getAppId())// 客户端唯一标识
                .toString();
    }
}
