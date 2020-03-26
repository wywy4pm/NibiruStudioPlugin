package com.nibiru.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.ImportStudioDialog;
import com.nibiru.plugin.utils.NibiruConfig;
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
        if (operationFile != null) {
            String dirPath = operationFile.getPath();
            boolean isSourceFolder = operationFile.isDirectory();
            //boolean contains = dirPath.contains("/libs");
            e.getPresentation().setVisible((isSourceFolder));//该action 的可见性
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}
