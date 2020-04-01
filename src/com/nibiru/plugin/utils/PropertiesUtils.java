package com.nibiru.plugin.utils;

import com.intellij.ide.util.PropertiesComponent;

public class PropertiesUtils {
    //public static final String KEY_SDK_PATH = "path_sdk";
    //是否登录了
    public static final String LOGIN_STATE = "isLogin";
    //登录成功后的数据
    public static final String LOGIN_DATA = "loginData";

    public static final String ACTIVATE_STATE = "isActivate";


    public static void setString(String key, String value) {
        PropertiesComponent.getInstance().setValue(key, value);
    }

    public static void setBoolean(String key, boolean value) {
        PropertiesComponent.getInstance().setValue(key, value);
    }

    public static boolean getBoolean(String key) {
        return PropertiesComponent.getInstance().getBoolean(key);
    }

    public static String getString(String key) {
        return PropertiesComponent.getInstance().getValue(key);
    }
}
