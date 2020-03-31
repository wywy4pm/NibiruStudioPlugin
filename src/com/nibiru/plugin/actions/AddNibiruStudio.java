package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.utils.Log;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class AddNibiruStudio extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        LoginDialog loginDialog = new LoginDialog(anActionEvent.getProject(),file);
        loginDialog.show();

//        ActivateDialog activateDialog = new ActivateDialog(anActionEvent.getProject());
//        activateDialog.show();

//        SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent.getProject());
//        sdkSettingDialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        boolean isVisible = false;
        if (e.getProject() != null && virtualFile != null) {
            String modulePath = ModuleUtils.getModulePath(e.getProject(), virtualFile);
            if (!StringUtils.isBlank(modulePath)) {
                String sdkPath = PropertiesUtils.getString(modulePath);
                Log.i("sdkPath = " + sdkPath);
                if (StringUtils.isBlank(sdkPath)) {
                    isVisible = true;
                }
            }
        }
        e.getPresentation().setVisible(isVisible);
    }


}
