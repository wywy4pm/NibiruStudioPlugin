package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.nibiru.plugin.utils.NibiruConfig;
import org.jetbrains.annotations.NotNull;

public class RefresgLicense extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (NibiruConfig.isLogin || NibiruConfig.isLogin) {
            e.getPresentation().setVisible(true);
        }
    }
}
