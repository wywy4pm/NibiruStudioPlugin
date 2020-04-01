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
import com.nibiru.plugin.ui.NsNoexitsTipDialog;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.NibiruUtils;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * 开发指定文件扩展名的方法
 */
public class OpenFile extends AnAction {

    public void update(AnActionEvent e) {
        PsiFile data = PlatformDataKeys.PSI_FILE.getData(e.getDataContext());
        if (data!=null){
            VirtualFile virtualFile = data.getVirtualFile();
            if (virtualFile!=null&&virtualFile.getPath().matches(".*?\\.nss$")){
                String sdkpath = PropertiesUtils.getString(ModuleUtils.getModulePath(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE)));
                if (StringUtils.isEmpty(sdkpath)){
                    e.getPresentation().setEnabled(false);
                }else {
                    e.getPresentation().setEnabled(true);
                }
            }else{
                e.getPresentation().setEnabled(false);
            }
        }else{
            e.getPresentation().setEnabled(false);
        }
    }

    public void actionPerformed(AnActionEvent e) {
        String location="HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{01CEE08C-C171-4D18-B3B9-B0CB280836EB}_is1";
        String key="DisplayIcon";
        String exepath = NibiruUtils.readRegistry(location, key);

        VirtualFile current_file = PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        VirtualFile app = VirtualFileManager.getInstance().findFileByUrl("file://" + exepath);
        if (app == null) {

            SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(e.getProject(), current_file);
            sdkSettingDialog.show();

            return;
        }
        if (current_file == null || !current_file.getPath().toString().matches(".*?\\.nss$")) {
            Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", "This is not .nss file.", NotificationType.INFORMATION));
            return;
        }
        Runtime rt = Runtime.getRuntime();
        try {
            String file_path = current_file.getPath();
            int index = file_path.indexOf("/Assets/layout/");
            if (index>0){
                String[] cmd = {exepath, file_path.substring(0,index),file_path};
                rt.exec(cmd);
            }else{
                Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", ".nss file Error!", NotificationType.INFORMATION));
                return;
            }
        } catch (IOException e1) {
            Notifications.Bus.notify(new Notification("Nibiru Studio", "Error", e1.getMessage(), NotificationType.ERROR));
        }
    }
}
