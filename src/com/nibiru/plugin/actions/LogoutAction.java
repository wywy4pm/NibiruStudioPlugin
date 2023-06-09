package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.NibiruConfig;
import com.nibiru.plugin.utils.NibiruUtils;
import org.jetbrains.annotations.NotNull;

public class LogoutAction extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        NibiruUtils.logout(e);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (NibiruConfig.isLogin) {
            e.getPresentation().setVisible(true);
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}
