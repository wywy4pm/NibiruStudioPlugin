package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ActivateDialog;
import com.nibiru.plugin.ui.LoginDialog;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class RefresgLicense extends AnAction {
    private Project project;
    private VirtualFile virtualFile;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        project = anActionEvent.getProject();
        virtualFile = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        String curModulePath = ModuleUtils.getCurModulePath(anActionEvent.getProject(), virtualFile);
        VirtualFile modulefile = LocalFileSystem.getInstance().findFileByPath(curModulePath);
        if (!NibiruConfig.isLogin) {
            LoginDialog loginDialog = new LoginDialog(anActionEvent,anActionEvent.getProject(), modulefile);
            loginDialog.setIsrefreshLesea(true);
            loginDialog.show();
        } else if (!NibiruConfig.deviceIsActivate) {
            ActivateDialog activateDialog = new ActivateDialog(anActionEvent,anActionEvent.getProject(), modulefile);
            activateDialog.setIsrefreshLesea(true);
            activateDialog.show();
        } else {
            FileUtils.createBinFile(NibiruConfig.loginBean, project, modulefile);
        }
    }
}
