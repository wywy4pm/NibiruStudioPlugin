package com.nibiru.plugin.ui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SdkCompleteDialog extends DialogWrapper {
    protected SdkCompleteDialog() {
        super(true);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(500, 200));
        Box box1 = Box.createHorizontalBox();
//        box1.add();

        return null;
    }
}
