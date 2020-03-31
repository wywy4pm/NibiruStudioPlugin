package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class AddNibiruStudio extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
//        LoginDialog loginDialog = new LoginDialog(anActionEvent.getProject());
//        loginDialog.show();

//        ActivateDialog activateDialog = new ActivateDialog(anActionEvent.getProject());
//        activateDialog.show();

        SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent.getProject());
        sdkSettingDialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        String sdkPath = PropertiesUtils.getString(PropertiesUtils.KEY_SDK_PATH);
        if (!StringUtils.isBlank(sdkPath)) {
            e.getPresentation().setVisible(true);
        } else {
            e.getPresentation().setVisible(false);
        }
    }


}
