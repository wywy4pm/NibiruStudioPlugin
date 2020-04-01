package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
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
        if (!NibiruConfig.isLogin) {
            LoginDialog loginDialog = new LoginDialog(anActionEvent.getProject(), virtualFile);
            loginDialog.show();
        } else if (!NibiruConfig.deviceIsActivate) {
            ActivateDialog activateDialog = new ActivateDialog(anActionEvent.getProject(), virtualFile);
            activateDialog.show();
        } else {
            FileUtils.createBinFile(NibiruConfig.loginBean, project, virtualFile);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        boolean isVisible = false;
        if (e.getProject() != null && virtualFile != null) {
            String modulePath = ModuleUtils.getModulePath(e.getProject(), virtualFile);
            if (!StringUtils.isBlank(modulePath)) {
                isVisible = true;
            }
        }
        e.getPresentation().setVisible(isVisible);
    }
}
