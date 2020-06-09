package com.nibiru.plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
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

public class ImportStudioDialog extends DialogWrapper {
    private VirtualFile folder;
    private Project project;
    private TextFieldWithBrowseButton browseButton;
    private VirtualFile sourceAarFile;
    private VirtualFile desAarFile;

    public ImportStudioDialog(Project project, VirtualFile folder) {
        super(true);
        this.project = project;
        this.folder = folder;
        init();
        setTitle(StringConstants.TITLE_IMPORT_AAR);
        setResizable(false);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return browseButton;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(500, 60));

        JLabel titleLabel = new JLabel(StringConstants.AAR_PATH);
        titleLabel.setPreferredSize(new Dimension(60, 25));
        titleLabel.setFont(new Font(null, Font.PLAIN, 13));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dialogPanel.add(titleLabel);

        browseButton = new TextFieldWithBrowseButton();
        browseButton.setPreferredSize(new Dimension(350, 28));
        browseButton.setFont(new Font(null, Font.PLAIN, 13));
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, true, false);
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser.chooseFiles(descriptor, project, null, new Consumer<List<VirtualFile>>() {
                    @Override
                    public void consume(List<VirtualFile> virtualFiles) {
                        if (virtualFiles != null && virtualFiles.size() > 0) {
                            sourceAarFile = virtualFiles.get(0);
                            browseButton.setText(virtualFiles.get(0).getPath());
                        }
                    }
                });
            }
        });

        dialogPanel.add(browseButton);

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        if (StringUtils.isBlank(browseButton.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_AAR_EMPTY, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else if (!FileUtils.isValidAar(browseButton.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_AAR_INVALID, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else {
            FileUtils.copyFile(project, sourceAarFile, FileUtils.getModuleLibsFolder(folder), FileUtils.getFileName(browseButton.getText()));
            String modulePath = ModuleUtils.getModulePath(project, folder);
            GradleUtils.addAppBuildFile(project, FileUtils.getAarName(FileUtils.getFileName(browseButton.getText())), modulePath);
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
        }
    }
}
