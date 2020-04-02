package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.ui.NsNoexitsTipDialog;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class License extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        String curModulePath = ModuleUtils.getCurModulePath(anActionEvent.getProject(), file);
        VirtualFile modulefile = LocalFileSystem.getInstance().findFileByPath(curModulePath);
        if (!NibiruConfig.isLogin) {
            LoginDialog loginDialog = new LoginDialog(anActionEvent.getProject(), modulefile);
            loginDialog.show();
        } else if (!NibiruConfig.deviceIsActivate) {
            ActivateDialog activateDialog = new ActivateDialog(anActionEvent.getProject(), modulefile);
            activateDialog.show();
        } else {
            NsNoexitsTipDialog noEnoughCountDialog = new NsNoexitsTipDialog(anActionEvent.getProject(), modulefile, false);
            noEnoughCountDialog.show();
        }
    }
}
