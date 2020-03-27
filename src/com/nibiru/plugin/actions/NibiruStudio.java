package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ImportStudioDialog;
import com.nibiru.plugin.utils.Log;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.NibiruConfig;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class NibiruStudio extends AnAction {
    private VirtualFile folder;
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        folder = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        project = anActionEvent.getProject();

        ImportStudioDialog studioDialog = new ImportStudioDialog(project, folder);
        studioDialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile operationFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        boolean isModuleFolder = false;
        if (operationFile != null) {
            String selectFolderPath = operationFile.getPath();
            isModuleFolder = ModuleUtils.isModuleFolder(e.getProject(), selectFolderPath);
        }
        e.getPresentation().setVisible((isModuleFolder));//该action 的可见性
    }
}
