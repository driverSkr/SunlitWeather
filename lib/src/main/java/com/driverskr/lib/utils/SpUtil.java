package com.driverskr.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 共享参数工具类
 */
public class SpUtil {

    private static final String THEME_FLAG = "theme_flag";
    private static final String PLUGIN_PATH = "plugin_path";
    private static final String ACCOUNT_NAME = "account_name";
    private static final String ACCOUNT_AVATAR = "account_avatar";
    private static final String TOKEN = "token";

    private static SpUtil spUtil = null;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor edit;

    private SpUtil(Context context) {
        sp = context.getSharedPreferences("sunlit_sp",Context.MODE_PRIVATE);
        edit = sp.edit();
    }

    public static SpUtil getInstance(Context context) {
        if (spUtil == null) {
            spUtil = new SpUtil(context);
        }
        return spUtil;
    }

    public static int getThemeFlag() {
        return sp.getInt(THEME_FLAG, 0);
    }

    public static void setThemeFlag(int flag) {
        edit.putInt(THEME_FLAG, flag);
        edit.apply();
    }

    public static String getPluginPath() {
        return sp.getString(PLUGIN_PATH, "");
    }

    public static void setPluginPath(String path) {
        edit.putString(PLUGIN_PATH, path);
        edit.apply();
    }

    public static String getAccount() {
        return sp.getString(ACCOUNT_NAME, "");
    }

    public static void setAccount(String name) {
        edit.putString(ACCOUNT_NAME, name);
        edit.apply();
    }

    public static String getAvatar() {
        return sp.getString(ACCOUNT_AVATAR, "");
    }

    public static void setAvatar(String url) {
        edit.putString(ACCOUNT_AVATAR, url);
        edit.apply();
    }

    public static String getToken() {
        return sp.getString(TOKEN, "");
    }

    public static void setToken(String token) {
        edit.putString(TOKEN, token);
        edit.apply();
    }

    public static void logout() {
        setAvatar("");
        setAccount("");
        setToken( "");
    }
}
