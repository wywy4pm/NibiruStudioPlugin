package com.nibiru.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.StringConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class NsNoexitsTipDialog extends DialogWrapper {
    private VirtualFile folder;
    private Project project;

    public NsNoexitsTipDialog(Project project, VirtualFile folder) {
        super(true);
        this.project = project;
        this.folder = folder;
        init();
        setTitle(StringConstants.TITLE_NO_NA_TIP);
        getOKAction().setEnabled(false);
        setResizable(false);
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
        dialogPanel.setPreferredSize(new Dimension(300, 100));
        JLabel tipcontent = new JLabel(StringConstants.NO_NA_TIP);
        tipcontent.setPreferredSize(new Dimension(300, 50));
        tipcontent.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(tipcontent);
        return dialogPanel;
    }
}
