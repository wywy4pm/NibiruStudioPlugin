package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.ui.SdkModifyDialog;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Settings extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (!NibiruConfig.isLogin) {
            LoginDialog loginDialog = new LoginDialog(anActionEvent, anActionEvent.getProject(), file);
            loginDialog.setIsrefreshsdk(true);
            loginDialog.show();
        } else if (!NibiruConfig.deviceIsActivate) {
            ActivateDialog activateDialog = new ActivateDialog(anActionEvent, anActionEvent.getProject(), file);
            activateDialog.setIsrefreshsdk(true);
            activateDialog.show();
        } else {
            SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent, anActionEvent.getProject(), file);
            sdkSettingDialog.show();
//            SdkModifyDialog sdkModifyDialog = new SdkModifyDialog(anActionEvent, anActionEvent.getProject(), file);
//            sdkModifyDialog.show();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile operationFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String sdkPath = FileUtils.getSdkPath(e.getProject(), operationFile);
        if (StringUtils.isBlank(sdkPath)) {
            e.getPresentation().setVisible(false);
        } else {
            e.getPresentation().setVisible(true);
        }
    }
}
