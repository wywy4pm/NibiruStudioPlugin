package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.SdkSettingDialog;
import org.jetbrains.annotations.NotNull;

public class Settings extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile folder = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent.getProject(),folder);
        sdkSettingDialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }
}
