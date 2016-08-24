package com.hj.app.oschina.util;

import android.content.Context;

import com.hj.app.oschina.AppContext;
import com.hj.app.oschina.R;

/**
 * 白天和夜间模式切换
 * Created by 火蚁 on 15/5/26.
 */
public class ThemeSwitchUtils {

    public static void switchTheme(Context context) {

    }

    public static int getTitleReadedColor() {
        if (AppContext.getNightModeSwitch()) {
            return R.color.night_infoTextColor;
        } else {
            return R.color.day_infoTextColor;
        }
    }

    public static int getTitleUnReadedColor() {
        if (AppContext.getNightModeSwitch()) {
            return R.color.night_textColor;
        } else {
            return R.color.day_textColor;
        }
    }

    public static String getWebViewBodyString() {
        if (AppContext.getNightModeSwitch()) {
            return "<body class='night'><div class='contentstyle' id='article_body'>";
        } else {
            return "<body style='background-color: #FFF'><div class='contentstyle' id='article_body' >";
        }
    }
}
