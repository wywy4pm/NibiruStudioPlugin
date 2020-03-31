package com.nibiru.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.nibiru.plugin.utils.StringConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ActivateDialog extends DialogWrapper {
    public ActivateDialog(@Nullable Project project) {
        super(true);
        init();
        setTitle(StringConstants.TITLE_NIBIRU_ACTIVATE);
        setResizable(false);
        setOKButtonText(StringConstants.ACTIVATE);
        setCancelButtonText(StringConstants.CANCEL);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(200, 50));
        JLabel tipLabel = new JLabel(StringConstants.TO_ACTIVATE);
        tipLabel.setPreferredSize(new Dimension(200, 25));
        tipLabel.setFont(new Font(null, Font.PLAIN, 13));
        tipLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dialogPanel.add(tipLabel);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        //
        if (this.getOKAction().isEnabled()) {
            close(0);
        }
    }
}
