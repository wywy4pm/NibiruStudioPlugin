package com.nibiru.plugin;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SampleDialogWrapper extends DialogWrapper {

    public SampleDialogWrapper() {
        super(true); // use current window as parent
        init();
        setTitle("Create New Scene");
        setResizable(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(500,400));

        JLabel titleLabel = new JLabel("Create a new empty Scene");
        titleLabel.setPreferredSize(new Dimension(500, 50));
        titleLabel.setFont(new Font(null, Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(titleLabel);

        Box boxScene = Box.createHorizontalBox();
        boxScene.setPreferredSize(new Dimension(350, 30));

        JLabel nameLabel = new JLabel("Scene Name:");
        nameLabel.setPreferredSize(new Dimension(90, 25));
        nameLabel.setFont(new Font(null, Font.PLAIN, 13));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxScene.add(nameLabel);

        boxScene.add(Box.createHorizontalStrut(20));

        JTextField nameTextArea = new JTextField();
        nameTextArea.setFont(new Font(null, Font.PLAIN, 13));
        nameTextArea.setPreferredSize(new Dimension(240, 25));
        nameTextArea.setText("MainScene");
        //nameTextArea.setBackground(JBColor.BLUE);
        Border nameBorder = BorderFactory.createLineBorder(JBColor.WHITE);
        nameTextArea.setBorder(BorderFactory.createCompoundBorder(nameBorder, BorderFactory.createEmptyBorder(6,5,6,5)));
        boxScene.add(nameTextArea);

        dialogPanel.add(boxScene);

        Box boxLayout = Box.createHorizontalBox();
        boxLayout.setPreferredSize(new Dimension(350, 30));

        JLabel layoutLabel = new JLabel("Layout Name:");
        layoutLabel.setPreferredSize(new Dimension(90, 25));
        layoutLabel.setFont(new Font(null, Font.PLAIN, 13));
        layoutLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxLayout.add(layoutLabel);

        boxLayout.add(Box.createHorizontalStrut(20));

        JTextArea layoutTextArea = new JTextArea();
        layoutTextArea.setFont(new Font(null, Font.PLAIN, 13));
        layoutTextArea.setPreferredSize(new Dimension(240, 25));
        layoutTextArea.setText("scene_main");
        Border layoutBorder = BorderFactory.createLineBorder(JBColor.WHITE);
        layoutTextArea.setBorder(BorderFactory.createCompoundBorder(layoutBorder, BorderFactory.createEmptyBorder(6,5,6,5)));
        boxLayout.add(layoutTextArea);

        dialogPanel.add(boxLayout);


        return dialogPanel;
    }
}
