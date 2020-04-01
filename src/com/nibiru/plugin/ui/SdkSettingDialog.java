package com.nibiru.plugin.ui;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.Consumer;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SdkSettingDialog extends DialogWrapper {
    private Project project;
    private TextFieldWithBrowseButton browseButton;
    private VirtualFile folder;
    private VirtualFile sdkFile;

    public SdkSettingDialog(Project project, VirtualFile folder) {
        super(true);
        this.project = project;
        //this.folder = folder;
        String modulePath = ModuleUtils.getCurModulePath(project, folder);
        if (!StringUtils.isBlank(modulePath)) {
            this.folder = LocalFileSystem.getInstance().findFileByPath(modulePath);
        }
        init();
        setTitle(StringConstants.TITLE_SDK_SETTING);
        setResizable(false);

        String sdkPath = FileUtils.getSdkPath(project, folder);
        Log.i("sdkPath = " + sdkPath);
        if (!StringUtils.isBlank(sdkPath) && browseButton != null) {
            sdkFile = LocalFileSystem.getInstance().findFileByPath(sdkPath);
            browseButton.setText(sdkPath);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(600, 80));

        Box boxText = Box.createHorizontalBox();
        boxText.setPreferredSize(new Dimension(600, 40));
        JLabel textLabel = new JLabel(StringConstants.SDK_TIPS);
        textLabel.setPreferredSize(new Dimension(400, 25));
        textLabel.setFont(new Font(null, Font.PLAIN, 13));
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxText.add(textLabel);
        boxText.add(Box.createHorizontalGlue());

        Box boxLocation = Box.createHorizontalBox();
        boxLocation.setPreferredSize(new Dimension(600, 30));
        JLabel titleLabel = new JLabel(StringConstants.SDK_LOCATION);
        titleLabel.setPreferredSize(new Dimension(180, 25));
        titleLabel.setFont(new Font(null, Font.PLAIN, 13));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxLocation.add(titleLabel);

        browseButton = new TextFieldWithBrowseButton();
        browseButton.setPreferredSize(new Dimension(420, 28));
        browseButton.setFont(new Font(null, Font.PLAIN, 13));
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, true, false);
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser.chooseFiles(descriptor, project, null, new Consumer<java.util.List<VirtualFile>>() {
                    @Override
                    public void consume(List<VirtualFile> virtualFiles) {
                        if (virtualFiles != null && virtualFiles.size() > 0) {
                            sdkFile = virtualFiles.get(0);
                            Log.i("chooseFile = " + virtualFiles.get(0).getPath());
                            browseButton.setText(virtualFiles.get(0).getPath());
                        }
                    }
                });
            }
        });
        boxLocation.add(browseButton);

        Box vBox = Box.createVerticalBox();
        vBox.add(boxText);
        vBox.add(boxLocation);
        dialogPanel.add(vBox);

        return dialogPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return browseButton;
    }

    @Override
    protected void doOKAction() {
        if (StringUtils.isBlank(browseButton.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_SDK_EMPTY, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else if (!FileUtils.isValidSdkFolder(browseButton.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_SDK_INVALID, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else {
            if (folder != null) {
                String modulePath = ModuleUtils.getModulePath(project, folder);
                if (!StringUtils.isBlank(modulePath)) {
                    String preSdkPath = PropertiesUtils.getString(modulePath);
//                    if (StringUtils.isBlank(preSdkPath) || !StringUtils.equals(preSdkPath, browseButton.getText())) {
                    PropertiesUtils.setString(modulePath, browseButton.getText());
                    VirtualFile aarFile = FileUtils.getAarFile(sdkFile);
                    FileUtils.copyFile(project, aarFile, FileUtils.getModuleLibsFolder(folder), FileUtils.getAarFileName(aarFile));
                    Log.i("modulePath = " + modulePath);
                    GradleUtils.addAppBuildFile(project, FileUtils.getAarName(FileUtils.getAarFileName(aarFile)), modulePath);
                    VirtualFileManager.getInstance().syncRefresh();
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            ModifyAndroidManifest manifest = new ModifyAndroidManifest(project, folder, "");
                            manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.NIBIRU_PLUGIN_IDS);
                        }
                    });
//                    }

                    if (!FileUtils.isInstallExe()) {
                        FileUtils.installExe(FileUtils.getExePath(LocalFileSystem.getInstance().findFileByPath(browseButton.getText())));
                    }

                    if (this.getOKAction().isEnabled()) {
                        close(0);
                    }
                }
            }
        }
    }
}
