package com.nibiru.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.nibiru.plugin.beans.GradleReadBean;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SdkModifyDialog extends DialogWrapper {
    private AnActionEvent anActionEvent;
    private VirtualFile file;
    private VirtualFile sdkFile;
    private String sdkPath;
    private Project project;
    private JPanel dialogPanel;
    private Box verticalBox;
    private static String[] MODIFY_TEXTS = new String[]{
            "Add dependency libraries in libs",
            "Add dependency configuration in build.gradle",
            "Modify main activity to inherit XBaseActivity",
            "Add support configuration in AndroidManifest"
    };
    private static String INCREASE_SDK = "Increase minSdkVersion to 19";
    private static String MODIFY_GRADLE = "Modify android gradle tools version under 3.5.0";

    public SdkModifyDialog(AnActionEvent anActionEvent, Project project, VirtualFile file, VirtualFile sdkFile, String sdkPath) {
        super(true);
        this.anActionEvent = anActionEvent;
        this.project = project;
        this.file = file;
        this.sdkFile = sdkFile;
        this.sdkPath = sdkPath;
        init();
        setTitle(StringConstants.TITLE_SDK_SETTING);
        setResizable(false);
        setOKButtonText(StringConstants.EXECUTE);
        setCancelButtonText(StringConstants.CANCEL);

        addView(GradleUtils.readBuildFile(project, ModuleUtils.getCurModulePath(project, file)));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        dialogPanel = new JPanel();
        //dialogPanel.setPreferredSize(new Dimension(500, 200));

        verticalBox = Box.createVerticalBox();
        JLabel label = new JLabel();
        label.setFont(new Font(null, Font.PLAIN, 13));
        label.setText(StringConstants.CONFIG_TITLE);
        verticalBox.add(label);
        verticalBox.add(Box.createVerticalStrut(10));
        for (int i = 0; i < MODIFY_TEXTS.length; i++) {
            addLine(MODIFY_TEXTS[i]);
        }

        dialogPanel.add(verticalBox);
        return dialogPanel;
    }

    private void addView(GradleReadBean readBean) {
        if (readBean != null) {
            if (readBean.isModifyClasspath) {
                addLine(MODIFY_GRADLE);
            }
            if (readBean.isModifyMinSdkVersion) {
                addLine(INCREASE_SDK);
            }
        }
    }

    private void addLine(String text) {
        JLabel label = new JLabel();
        label.setFont(new Font(null, Font.PLAIN, 12));
        label.setText("\uF06C   " + text);
        verticalBox.add(label);
    }

    @Override
    protected void doOKAction() {
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
                if (this.getOKAction().isEnabled()) {
                    close(0);
                }
                FileUtils.createBinFile(NibiruConfig.loginBean, project, file);
                Messages.showMessageDialog("Module " + file.getName() + " has updated Nibiru Studio SDK successfully.", StringConstants.TITLE_NO_NA_TIP, UiUtils.getCompleteIcon());

                if (!FileUtils.isInstallExe()) {
                    int okCancel = Messages.showOkCancelDialog(StringConstants.TIP_TO_INSTALL_EXE, StringConstants.TITLE_SDK_SETTING, StringConstants.INSTALL, StringConstants.CANCEL, UiUtils.getCompleteIcon());
                    Log.i("okCancel = " + okCancel);
                    if (okCancel == 0) {
                        FileUtils.installExe(FileUtils.getExePath(LocalFileSystem.getInstance().findFileByPath(sdkPath)));
                    }
                } else {
                    Messages.showMessageDialog(StringConstants.TIP_INSTALLED_EXE, StringConstants.TITLE_SDK_SETTING, UiUtils.getCompleteIcon());
                }
                GradleUtils.syncProject(anActionEvent);
            }
        }
    }
}
