package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.Log;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class AppLicense extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        boolean isVisible = false;
        if (e.getProject() != null && virtualFile != null) {
            String modulePath = ModuleUtils.getModulePath(e.getProject(), virtualFile);
            if (!StringUtils.isBlank(modulePath)) {
                String sdkPath = PropertiesUtils.getString(ModuleUtils.getModulePath(e.getProject(), virtualFile));
                Log.i("sdkPath = " + sdkPath);
                if (!StringUtils.isBlank(sdkPath)) {
                    isVisible = true;
                }
            }
        }
        e.getPresentation().setVisible(isVisible);
    }
}
