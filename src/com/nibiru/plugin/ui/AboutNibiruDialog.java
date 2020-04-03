package com.nibiru.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AboutNibiruDialog extends DialogWrapper {
    private Project project;
    private VirtualFile virtualFile;

    public AboutNibiruDialog(@Nullable Project project, VirtualFile virtualFile) {
        super(true);
        this.project = project;
        this.virtualFile = virtualFile;
        init();
        setTitle("About");
        setResizable(false);
        getButton(getCancelAction()).setVisible(false);
        getButton(getOKAction()).setVisible(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(356, 238));
        JLabel title = new JLabel();
        title.setIcon(IconLoader.getIcon("/icons/ns.svg", AboutNibiruDialog.class));
        title.setText("Nibiru Studio");
        title.setIconTextGap(10);
        title.setFont(new Font(null, Font.PLAIN, 25));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        dialogPanel.add(title);

//        Box titlebox = Box.createVerticalBox();
//        titlebox.setPreferredSize(new Dimension(356,238));
//        titlebox.add(Box.createVerticalStrut(20));
//        JLabel title = new JLabel();
//        title.setIcon(IconLoader.getIcon("/icons/ns.svg", AboutNibiruDialog.class));
//        title.setText("Nibiru Studio");
//        title.setIconTextGap(10);
//        title.setHorizontalTextPosition(SwingConstants.RIGHT);
//        titlebox.add(title);
//        Box tipbox=Box.createHorizontalBox();
////        tipbox.add(Box.createHorizontalStrut(250));
//        tipbox.add(new JLabel("Powered by Nibiru"));
//        titlebox.add(tipbox);
//        titlebox.add(Box.createVerticalStrut(42));
//        titlebox.add(new JLabel("Nibiru Studio 3.5.0"));
//        titlebox.add(Box.createVerticalStrut(34));
//        titlebox.add(new JLabel("Nibiru Studio 3.5.0"));
//        dialogPanel.add(titlebox);
        return dialogPanel;
    }
}
