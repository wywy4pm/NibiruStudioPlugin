package com.nibiru.plugin.utils;

import com.intellij.ide.util.PropertiesComponent;

public class PropertiesUtils {
    public static final String KEY_SDK_PATH = "path_sdk";

    public static void setString(String key, String value) {
        PropertiesComponent.getInstance().setValue(key, value);
    }

    public static String getString(String key) {
        return PropertiesComponent.getInstance().getValue(key);
    }
}
