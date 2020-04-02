package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.nibiru.plugin.utils.CredentialUtils;
import com.nibiru.plugin.utils.NibiruConfig;
import org.jetbrains.annotations.NotNull;

public class LogoutAction extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        logout();
    }
    private void logout() {
        NibiruConfig.isLogin=false;
        CredentialUtils.putString(CredentialUtils.LOGIN_INFO, "", "");
        Messages.showMessageDialog("已退出登录!", "退出登录", Messages.getInformationIcon());
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
