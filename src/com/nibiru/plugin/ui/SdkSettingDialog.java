package com.nibiru.plugin.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
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
    private AnActionEvent anActionEvent;
    private TextFieldWithBrowseButton browseButton;
    private VirtualFile folder;
    private VirtualFile sdkFile;
    private boolean isUpdateSdk;

    public SdkSettingDialog(AnActionEvent anActionEvent, Project project, VirtualFile folder) {
        super(true);
        this.project = project;
        this.anActionEvent = anActionEvent;
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
        } else {
            isUpdateSdk = true;
        }
        if (!StringUtils.isBlank(sdkPath) && browseButton != null) {
            LocalFileSystem.getInstance().refreshWithoutFileWatcher(true);
            sdkFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(sdkPath);
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
            close();
            if (!isUpdateSdk) {
                if (folder != null && sdkFile != null) {
                    SdkModifyDialog sdkModifyDialog = new SdkModifyDialog(anActionEvent, project, folder, sdkFile, browseButton.getText());
                    sdkModifyDialog.show();
                }
            } else {
                SettingUtils.setSdkSetting(project, anActionEvent, folder, sdkFile, browseButton.getText(), isUpdateSdk);
            }
        }
    }

    private void close(){
        if (this.getOKAction().isEnabled()) {
            close(0);
        }
    }
}
