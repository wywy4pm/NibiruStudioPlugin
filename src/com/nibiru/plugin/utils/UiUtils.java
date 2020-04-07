package com.nibiru.plugin.utils;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;

public class UiUtils {
    public static boolean isIntelliJ() {
        boolean isIntelliJ = UIUtil.isUnderIntelliJLaF();
        boolean isDarcula = UIUtil.isUnderDarcula();
        Log.i("isIntelliJ = " + isIntelliJ + " isDarcula = " + isDarcula + " lookName = " + UIManager.getLookAndFeel().getName());
        if (isIntelliJ && !isDarcula) {
            return true;
        }
        return false;
    }

    public static String getImageName(String imageName) {
        if (isIntelliJ()) {
            if (imageName.contains(".")) {
                int index = imageName.lastIndexOf(".");
                String imgName = imageName.substring(0, index);
                String imgLast = imageName.substring(index + 1);
                imageName = imgName + "_light." + imgLast;
            }
        }
        Log.i("imageName = " + imageName);
        return imageName;
    }

    public static Icon getImageIcon(String imageName) {
        if (imageName.contains(".")) {
            int index = imageName.lastIndexOf(".");
            String imgName = imageName.substring(0, index);
            String imgLast = imageName.substring(index + 1);
            if (isIntelliJ()) {
                imageName = imgName + "_light." + imgLast;
                Log.i("imageName = " + imageName);
                return IconLoader.getIcon(imageName);
            } else {
//                imageName = imgName + "_dark." + imgLast;
//                Log.i("imageName = " + imageName);
                return IconLoader.getDarkIcon(IconLoader.getIcon(imageName), true);
            }
        }
        return null;
    }
}
