package com.nibiru.plugin.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.NibiruConfig;
import com.nibiru.plugin.utils.NibiruUtils;
import com.nibiru.plugin.utils.StringConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class NsNoexitsTipDialog extends DialogWrapper {
    private VirtualFile folder;
    private Project project;
    private boolean isNoEnoughCount;
    private JLabel tipcontent;

    public NsNoexitsTipDialog(Project project, VirtualFile folder, boolean isNoEnoughCount) {
        super(true);
        this.project = project;
        this.folder = folder;
        this.isNoEnoughCount = isNoEnoughCount;
        init();
        setTitle(StringConstants.TITLE_NIBIRU_ACTIVATE);
        setResizable(false);

        if (tipcontent != null) {
            if (isNoEnoughCount) {
                tipcontent.setText(StringConstants.TO_ACTIVATE_COUNT_NOT);
                getOKAction().setEnabled(true);
            } else {
                tipcontent = new JLabel(StringConstants.NO_NA_TIP);
                setOKButtonText(StringConstants.LOG_OUT);
                getButton(getOKAction()).setVisible(false);
                setCancelButtonText("OK");
            }
        }
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(450, 100));
        tipcontent = new JLabel(StringConstants.NO_NA_TIP);
        tipcontent.setPreferredSize(new Dimension(450, 50));
        tipcontent.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(tipcontent);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (isNoEnoughCount) {
            BrowserUtil.browse(NibiruConfig.device_activate_url);
        }
    }
}
