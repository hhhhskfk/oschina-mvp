package com.hj.app.oschina;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.hj.app.oschina.api.ServerAPI;
import com.hj.app.oschina.common.AppConfig;
import com.hj.app.oschina.common.SharePreferencesManager;
import com.hj.app.oschina.util.StringUtils;
import com.hj.app.oschina.util.TLog;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by huangjie08 on 2016/8/18.
 */
public class AppContext extends Application {

    private static String LAST_REFRESH_TIME = "last_refresh_time.pref";

    public static Context context;
    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        init();
    }

    public static AppContext getInstance() {
        return instance;
    }

    private void init() {
        ServerAPI.init();
        TLog.DEBUG = BuildConfig.DEBUG;
    }

    //夜间模式
    public static boolean getNightModeSwitch() {
        //return getPreferences().getBoolean(KEY_NIGHT_MODE_SWITCH, false);
        return false;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }


    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 记录列表上次刷新时间
     *
     * @param key
     * @param value
     * @return void
     * @author 火蚁
     * 2015-2-9 下午2:21:37
     */
    public static void putToLastRefreshTime(String key, String value) {
        SharedPreferences preferences = getPreferences(LAST_REFRESH_TIME);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        apply(editor);
    }

    /**
     * 获取列表的上次刷新时间
     *
     * @param key
     * @return
     * @author 火蚁
     * 2015-2-9 下午2:22:04
     */
    public static String getLastRefreshTime(String key) {
        return getPreferences(LAST_REFRESH_TIME).getString(key, StringUtils.getCurTimeStr());
    }

    /**
     * 放入已读文章列表中
     *
     */
    public static void putReadedPostList(String prefFileName, String key,
                                         String value) {
        SharedPreferences preferences = getPreferences(prefFileName);
        int size = preferences.getAll().size();
        SharedPreferences.Editor editor = preferences.edit();
        if (size >= 100) {
            editor.clear();
        }
        editor.putString(key, value);
        apply(editor);
    }

    /**
     * 读取是否是已读的文章列表
     *
     * @return
     */
    public static boolean isOnReadedPostList(String prefFileName, String key) {
        return SharePreferencesManager.getPreferences(prefFileName).contains(key);
    }

    public static SharedPreferences getPreferences(String prefName) {
        return context.getSharedPreferences(prefName,
                Context.MODE_MULTI_PROCESS);
    }

    public static void apply(SharedPreferences.Editor editor) {
        SharePreferencesManager.apply(editor);
    }
}
