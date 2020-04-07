package com.nibiru.plugin.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.nibiru.plugin.utils.StringConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SameLayoutNameDialog extends DialogWrapper {
    private JLabel tipcontent;
    private  boolean isok=false;

    public boolean isOk() {
        return isok;
    }

    public SameLayoutNameDialog() {
        super(true);
        init();
        setTitle(StringConstants.TITLE_CRATE_LAYOUT);
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
        dialogPanel.setPreferredSize(new Dimension(400, 100));
        tipcontent = new JLabel(StringConstants.SAME_LAYOUT_NAME);
        tipcontent.setPreferredSize(new Dimension(400, 50));
        tipcontent.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(tipcontent);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        isok=true;
        super.doOKAction();
    }
}
