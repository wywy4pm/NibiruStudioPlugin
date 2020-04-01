package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;


/**
 * 开发指定文件扩展名的方法
 */
public class OpenFile extends AnAction {

    public void update(AnActionEvent e) {
        PsiFile data = PlatformDataKeys.PSI_FILE.getData(e.getDataContext());
        if (data != null) {
            VirtualFile virtualFile = data.getVirtualFile();
            if (virtualFile != null && virtualFile.getPath().matches(".*?\\.nss$")) {
                String sdkPath = FileUtils.getSdkPath(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE));
                if (StringUtils.isEmpty(sdkPath)){
                    e.getPresentation().setEnabled(false);
                } else {
                    e.getPresentation().setEnabled(true);
                }
            } else {
                e.getPresentation().setEnabled(false);
            }
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    public void actionPerformed(AnActionEvent e) {
        FileUtils.openNssFile(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE));
    }
}
