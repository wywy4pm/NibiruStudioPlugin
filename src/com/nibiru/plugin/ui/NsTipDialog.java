package com.nibiru.plugin.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.StringConstants;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class NsTipDialog extends DialogWrapper {
    private boolean isInstall;
    private String sdkPath;
    private JLabel textLabel;

    protected NsTipDialog(boolean isInstall, String sdkPath) {
        super(true);
        this.isInstall = isInstall;
        this.sdkPath = sdkPath;
        init();
        setResizable(false);
        setTitle(StringConstants.TITLE_SDK_SETTING);
        JButton cancelBtn = getButton(getCancelAction());
        if (textLabel != null && cancelBtn != null) {
            if (!isInstall) {
                setOKButtonText(StringConstants.INSTALL);
                setCancelButtonText(StringConstants.CANCEL);
                textLabel.setText(StringConstants.TIP_TO_INSTALL_EXE);
            } else {
                textLabel.setText(StringConstants.TIP_INSTALLED_EXE);
                setOKButtonText(StringConstants.OK);
                cancelBtn.setVisible(false);
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        textLabel = new JLabel(StringConstants.TIP_TO_INSTALL_EXE);
        textLabel.setFont(new Font(null, Font.PLAIN, 14));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dialogPanel.add(textLabel);

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        if (this.getOKAction().isEnabled()) {
            close(0);
        }
        if (!isInstall && !StringUtils.isBlank(sdkPath)) {
            FileUtils.installExe(FileUtils.getExePath(LocalFileSystem.getInstance().findFileByPath(sdkPath)));
        }
    }
}
