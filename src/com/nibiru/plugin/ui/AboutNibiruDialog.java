package com.nibiru.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.StringConstants;
import com.nibiru.plugin.utils.UiUtils;
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
        JButton okBtn = getButton(getOKAction());
        JButton cancelBtn = getButton(getCancelAction());
        if (okBtn != null) {
            okBtn.setVisible(false);
        }
        if (cancelBtn != null) {
            cancelBtn.setVisible(false);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(356, 238));
        JLabel title = new JLabel();
        title.setPreferredSize(new Dimension(356, 50));
        title.setIcon(UiUtils.getImageIcon("/icons/ns.png"));
        title.setText(StringConstants.TITLE_NO_NA_TIP);
        title.setIconTextGap(10);
        title.setFont(new Font(null, Font.PLAIN, 25));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        dialogPanel.add(title);

        Box powerBox = Box.createHorizontalBox();
        powerBox.setPreferredSize(new Dimension(250, 30));
        powerBox.add(Box.createHorizontalGlue());
        JLabel labelPower = new JLabel(StringConstants.POWER);
        labelPower.setFont(new Font(null, Font.PLAIN, 15));
        powerBox.add(labelPower);
        dialogPanel.add(powerBox);

        Box box = Box.createVerticalBox();
        box.setPreferredSize(new Dimension(250, 150));
        box.add(Box.createVerticalStrut(20));
        JLabel labelVersion = new JLabel("Version: v" + FileUtils.getPluginVersion());
        labelVersion.setFont(new Font(null, Font.PLAIN, 18));
        box.add(labelVersion);
        box.add(Box.createVerticalStrut(20));
        JLabel labelRelease = new JLabel("Release Data: " + StringConstants.RELEASE_DATA);
        labelRelease.setFont(new Font(null, Font.PLAIN, 18));
        box.add(labelRelease);
        dialogPanel.add(box);

        return dialogPanel;
    }
}
