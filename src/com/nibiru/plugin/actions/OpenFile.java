package com.nibiru.plugin.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.NibiruUtils;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 开发指定文件扩展名的方法
 */
public class OpenFile extends AnAction {

    public void update(AnActionEvent e) {
        PsiFile data = PlatformDataKeys.PSI_FILE.getData(e.getDataContext());
        if (data != null) {
            VirtualFile virtualFile = data.getVirtualFile();
            if (virtualFile != null && virtualFile.getPath().matches(".*?\\.nss$")) {
                String sdkpath = PropertiesUtils.getString(ModuleUtils.getModulePath(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE)));
                if (StringUtils.isEmpty(sdkpath)) {
                    e.getPresentation().setEnabled(true);
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
