package com.nibiru.plugin.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.Consumer;
import com.intellij.util.ui.UIUtil;
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
    private AnActionEvent anActionEvent;
    private TextFieldWithBrowseButton browseButton;
    private VirtualFile folder;
    private VirtualFile sdkFile;

    public SdkSettingDialog(AnActionEvent anActionEvent, Project project, VirtualFile folder) {
        super(true);
        this.project = project;
        this.anActionEvent = anActionEvent;
        //this.folder = folder;
        String modulePath = ModuleUtils.getCurModulePath(project, folder);
        if (!StringUtils.isBlank(modulePath)) {
            this.folder = LocalFileSystem.getInstance().findFileByPath(modulePath);
        }
        init();
        setTitle(StringConstants.TITLE_SDK_SETTING);
        setResizable(false);

        String sdkPath = FileUtils.getSdkPath(project, folder);
        if (StringUtils.isBlank(sdkPath)) {
            sdkPath = PropertiesUtils.getString(PropertiesUtils.KEY_SDK_PATH);
        }
        Log.i("sdkPath = " + sdkPath);
        if (!StringUtils.isBlank(sdkPath) && browseButton != null) {
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
            sdkFile = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + sdkPath);
            browseButton.setText(sdkPath);
        }
        setOKButtonText(StringConstants.SDK_OK);

    }

    public JComponent getTopView() {
        Box topBox = Box.createHorizontalBox();
        topBox.setPreferredSize(new Dimension(600, 40));

        JLabel iconImage = new JLabel();
        iconImage.setIcon(UiUtils.getImageIcon("/icons/ns.png"));
        iconImage.setPreferredSize(new Dimension(20, 20));
        topBox.add(iconImage);

        topBox.add(Box.createHorizontalStrut(10));

        JLabel textNibiru = new JLabel(StringConstants.TITLE_NO_NA_TIP);
        textNibiru.setPreferredSize(new Dimension(200, 20));
        textNibiru.setFont(new Font(null, Font.BOLD, 18));
        textNibiru.setHorizontalAlignment(SwingConstants.LEFT);
        topBox.add(textNibiru);

        topBox.add(Box.createHorizontalGlue());

        JLabel iconVr = new JLabel();
        iconVr.setIcon(UiUtils.getImageIcon("/icons/vr.png"));
        //iconVr.setPreferredSize(new Dimension(20,20));
        topBox.add(iconVr);

        return topBox;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(600, 160));

        Box topBox = (Box) getTopView();
        dialogPanel.add(topBox);

        Box boxText = Box.createHorizontalBox();
        boxText.setPreferredSize(new Dimension(510, 40));
        JLabel textLabel = new JLabel(StringConstants.SDK_TIPS);
        textLabel.setPreferredSize(new Dimension(400, 25));
        textLabel.setFont(new Font(null, Font.BOLD, 14));
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxText.add(textLabel);
        boxText.add(Box.createHorizontalGlue());

        Box boxLocation = Box.createHorizontalBox();
        boxLocation.setPreferredSize(new Dimension(510, 30));
        JLabel titleLabel = new JLabel(StringConstants.SDK_LOCATION);
        titleLabel.setPreferredSize(new Dimension(140, 25));
        titleLabel.setFont(new Font(null, Font.PLAIN, 12));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxLocation.add(titleLabel);

        browseButton = new TextFieldWithBrowseButton();
        browseButton.setPreferredSize(new Dimension(370, 28));
        browseButton.setFont(new Font(null, Font.PLAIN, 12));
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, true, false);
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser.chooseFiles(descriptor, project, null, new Consumer<java.util.List<VirtualFile>>() {
                    @Override
                    public void consume(List<VirtualFile> virtualFiles) {
                        if (virtualFiles != null && virtualFiles.size() > 0) {
                            sdkFile = virtualFiles.get(0);
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
            Messages.showMessageDialog(StringConstants.MSG_FILE_SDK_EMPTY, StringConstants.TITLE_FILE_ERROR, UiUtils.getInfoIcon());
        } else if (!FileUtils.isValidSdkFolder(browseButton.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_SDK_INVALID, StringConstants.TITLE_FILE_ERROR, UiUtils.getErrorIcon());
        } else {
            if (folder != null) {
                String modulePath = ModuleUtils.getCurModulePath(project, folder);
                if (!StringUtils.isBlank(modulePath)) {
//                    String preSdkPath = PropertiesUtils.getString(modulePath);
//                    if (StringUtils.isBlank(preSdkPath) || !StringUtils.equals(preSdkPath, browseButton.getText())) {
                    PropertiesUtils.setString(PropertiesUtils.KEY_SDK_PATH, browseButton.getText());
                    PropertiesUtils.setString(modulePath, browseButton.getText());
                    VirtualFile aarFile = FileUtils.getAarFile(sdkFile);
                    FileUtils.copyFile(project, aarFile, FileUtils.getModuleLibsFolder(folder), FileUtils.getAarFileName(aarFile));
                    GradleUtils.addAppBuildFile(project, FileUtils.getAarName(FileUtils.getAarFileName(aarFile)), modulePath);
                    VirtualFileManager.getInstance().syncRefresh();
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            ModifyAndroidManifest manifest = new ModifyAndroidManifest(project, folder, "");
                            manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.NIBIRU_PLUGIN_IDS);
                        }
                    });
                    if (this.getOKAction().isEnabled()) {
                        close(0);
                    }
//                    }
                    FileUtils.createBinFile(NibiruConfig.loginBean, project, folder);
                    Messages.showMessageDialog("Module " + folder.getName() + " has updated Nibiru Studio SDK successfully.", StringConstants.TITLE_NO_NA_TIP, UiUtils.getCompleteIcon());

//                    if (!FileUtils.isInstallExe()) {
//                        FileUtils.installExe(FileUtils.getExePath(LocalFileSystem.getInstance().findFileByPath(browseButton.getText())));
//                    }
//                    NsTipDialog nsTipDialog = new NsTipDialog(FileUtils.isInstallExe(), browseButton.getText());
//                    nsTipDialog.show();
                    if (!FileUtils.isInstallExe()) {
                        int okCancel = Messages.showOkCancelDialog(StringConstants.TIP_TO_INSTALL_EXE, StringConstants.TITLE_SDK_SETTING, StringConstants.INSTALL, StringConstants.CANCEL, UiUtils.getCompleteIcon());
                        Log.i("okCancel = " + okCancel);
                        if (okCancel == 0) {
                            FileUtils.installExe(FileUtils.getExePath(LocalFileSystem.getInstance().findFileByPath(browseButton.getText())));
                        }
                    } else {
                        Messages.showMessageDialog(StringConstants.TIP_INSTALLED_EXE, StringConstants.TITLE_SDK_SETTING, UiUtils.getCompleteIcon());
                    }
                    GradleUtils.syncProject(anActionEvent);
                }
            }
        }
    }
}
