package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.ui.NsNoexitsTipDialog;
import com.nibiru.plugin.utils.NibiruConfig;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.jetbrains.annotations.NotNull;

public class License extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (!NibiruConfig.isLogin) {
            LoginDialog loginDialog = new LoginDialog(anActionEvent.getProject(), file);
            loginDialog.show();
        } else if (!NibiruConfig.deviceIsActivate) {
            ActivateDialog activateDialog = new ActivateDialog(anActionEvent.getProject(), file);
            activateDialog.show();
        } else {
            //显示激活成功界面
            NsNoexitsTipDialog studioDialog = new NsNoexitsTipDialog(anActionEvent.getProject(), file);
            studioDialog.show();
        }
    }
}
