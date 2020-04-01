package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Settings extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        String sdkPath = FileUtils.getSdkPath(anActionEvent.getProject(), file);
        if (!StringUtils.isBlank(sdkPath)) {
            SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent.getProject(), file);
            sdkSettingDialog.show();
        } else {
            if (!NibiruConfig.isLogin) {
                LoginDialog loginDialog = new LoginDialog(anActionEvent.getProject(), file);
                loginDialog.show();
            } else if (!NibiruConfig.deviceIsActivate) {
                ActivateDialog activateDialog = new ActivateDialog(anActionEvent.getProject(), file);
                activateDialog.show();
            } else {
                SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent.getProject(), file);
                sdkSettingDialog.show();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        boolean isVisible = false;
        if (e.getProject() != null && virtualFile != null) {
            String modulePath = ModuleUtils.getCurModulePath(e.getProject(), virtualFile);
            if (!StringUtils.isBlank(modulePath)) {
                isVisible = true;
            }
        }
        e.getPresentation().setVisible(isVisible);
    }
}
