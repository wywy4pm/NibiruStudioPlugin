package com.nibiru.plugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang.StringUtils;

public class SettingUtils {
    public static void setSdkSetting(Project project, AnActionEvent anActionEvent, VirtualFile file, VirtualFile sdkFile, String sdkPath, boolean isUpdateSdk) {
        if (file != null && sdkFile != null && !StringUtils.isBlank(sdkPath)) {
            String modulePath = ModuleUtils.getCurModulePath(project, file);
            if (!StringUtils.isBlank(modulePath)) {
                PropertiesUtils.setString(PropertiesUtils.KEY_SDK_PATH, sdkPath);
                PropertiesUtils.setString(modulePath, sdkPath);
                VirtualFile aarFile = FileUtils.getAarFile(sdkFile);
                FileUtils.copyFile(project, aarFile, FileUtils.getModuleLibsFolder(file), FileUtils.getAarFileName(aarFile));
                GradleUtils.addAppBuildFile(project, FileUtils.getAarName(FileUtils.getAarFileName(aarFile)), modulePath);
                VirtualFileManager.getInstance().syncRefresh();
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        ModifyAndroidManifest manifest = new ModifyAndroidManifest(project, file, "");
                        manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.REMOVE_THEME);
                        manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.NIBIRU_PLUGIN_IDS);
                    }
                });
                FileUtils.createBinFile(NibiruConfig.loginBean, project, file, false);
                if (isUpdateSdk) {
                    Messages.showMessageDialog("Module " + file.getName() + " has updated Nibiru Studio SDK successfully.", StringConstants.TITLE_NO_NA_TIP, UiUtils.getCompleteIcon());
                }
                if (!FileUtils.isInstallExe()) {
                    int okCancel = Messages.showOkCancelDialog(StringConstants.TIP_TO_INSTALL_EXE, StringConstants.TITLE_SDK_SETTING, StringConstants.INSTALL, StringConstants.CANCEL, UiUtils.getCompleteIcon());
                    Log.i("okCancel = " + okCancel);
                    if (okCancel == 0) {
                        FileUtils.installExe(FileUtils.getExePath(LocalFileSystem.getInstance().findFileByPath(sdkPath)));
                    }
                } else {
                    if (!isUpdateSdk) {
                        Messages.showMessageDialog(StringConstants.TIP_INSTALLED_EXE, StringConstants.TITLE_SDK_SETTING, UiUtils.getCompleteIcon());
                    }
                }
                GradleUtils.syncProject(anActionEvent);
            }
        }
    }
}
