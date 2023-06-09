package com.nibiru.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.beans.GradleReadBean;
import com.nibiru.plugin.utils.*;
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
    private boolean isUpdateSdk;

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
        close();
        SettingUtils.setSdkSetting(project, anActionEvent, file, sdkFile, sdkPath, false);
    }

    private void close() {
        if (this.getOKAction().isEnabled()) {
            close(0);
        }
    }
}
