package com.nibiru.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.AndroidManifestUtils;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.Log;
import com.nibiru.plugin.utils.StringConstants;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

public class CreateSceneDialog extends DialogWrapper {
    private Callback callback;
    private JTextField nameTextField;
    private JTextField layoutTextField;
    private JCheckBox isLauncherCheckBox;
    private JCheckBox isNssCheckBox;
    private boolean isLauncherScene;
    private boolean isEditWithNss;
    private boolean hasLauncherScene;

    public CreateSceneDialog(Project project, VirtualFile folder) {
        super(true); // use current window as parent
        init();
        setTitle(StringConstants.TITLE_CRATE_SCENE);
        setResizable(false);

        hasLauncherScene = AndroidManifestUtils.ishasLauncherScene(project, folder);
        if (isLauncherCheckBox != null && !hasLauncherScene) {
            //isLauncherCheckBox.setEnabled(false);
            isLauncherCheckBox.setSelected(true);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(400, 220));

        JLabel titleLabel = new JLabel(StringConstants.P_CREATE_SCENE);
        titleLabel.setPreferredSize(new Dimension(400, 50));
        titleLabel.setFont(new Font(null, Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(titleLabel);

        Box boxScene = Box.createHorizontalBox();
        boxScene.setPreferredSize(new Dimension(400, 30));
        JLabel nameLabel = new JLabel(StringConstants.SCENE_NAME);
        nameLabel.setPreferredSize(new Dimension(90, 25));
        nameLabel.setFont(new Font(null, Font.PLAIN, 13));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxScene.add(nameLabel);
        boxScene.add(Box.createHorizontalStrut(20));
        nameTextField = new JTextField();
        nameTextField.setFont(new Font(null, Font.PLAIN, 13));
        nameTextField.setPreferredSize(new Dimension(290, 25));
        nameTextField.setText(StringConstants.DEFAULT_SCENE_NAME);
        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLayoutText(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLayoutText(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        boxScene.add(nameTextField);
        //dialogPanel.add(boxScene);

        Box boxCheck = Box.createHorizontalBox();
        boxCheck.setPreferredSize(new Dimension(400, 30));
        boxCheck.add(Box.createHorizontalGlue());
        isLauncherCheckBox = new JCheckBox(StringConstants.IS_LAUNCHER_SCENE);
        isLauncherCheckBox.setPreferredSize(new Dimension(290, 20));
        isLauncherCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox != null) {
                    isLauncherScene = checkBox.isSelected();
                }
            }
        });
        boxCheck.add(isLauncherCheckBox);
        //dialogPanel.add(boxCheck);

        Box boxLayout = Box.createHorizontalBox();
        boxLayout.setPreferredSize(new Dimension(400, 30));
        JLabel layoutLabel = new JLabel(StringConstants.LAYOUT_NAME);
        layoutLabel.setPreferredSize(new Dimension(90, 25));
        layoutLabel.setFont(new Font(null, Font.PLAIN, 13));
        layoutLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxLayout.add(layoutLabel);
        boxLayout.add(Box.createHorizontalStrut(20));
        layoutTextField = new JTextField();
        layoutTextField.setFont(new Font(null, Font.PLAIN, 13));
        layoutTextField.setPreferredSize(new Dimension(290, 25));
        layoutTextField.setText(StringConstants.DEFAULT_lAYOUT_NAME);
        boxLayout.add(layoutTextField);
        //dialogPanel.add(boxLayout);

        Box boxNssCheck = Box.createHorizontalBox();
        boxNssCheck.setPreferredSize(new Dimension(400, 30));
        boxNssCheck.add(Box.createHorizontalGlue());
        isNssCheckBox = new JCheckBox(StringConstants.IS_EDIT_WITH_NSS);
        isNssCheckBox.setPreferredSize(new Dimension(290, 20));
        isNssCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox != null) {
                    isEditWithNss = checkBox.isSelected();
                }
            }
        });
        boxNssCheck.add(isNssCheckBox);

        Box vBox = Box.createVerticalBox();
        vBox.add(boxScene);
        vBox.add(boxCheck);
        vBox.add(boxLayout);
        vBox.add(boxNssCheck);
        dialogPanel.add(vBox);

        return dialogPanel;
    }

    public void updateLayoutText(DocumentEvent e) {
        try {
            String text = e.getDocument().getText(0, e.getDocument().getLength());
            Log.i("insertUpdate text = " + text);
            if (layoutTextField != null) {
                if (!StringUtils.isBlank(text)) {
                    String newText = text.toLowerCase();
                    if (newText.contains("_")) {
                        newText = newText.replaceAll("_", "");
                    }
                    if (newText.contains("scene")) {
                        newText = newText.replaceFirst("scene", "");
                    } /*else {
                        newText = newText.replaceAll("[s]|[c]|[e]|[n]", "");
                    }*/
                    layoutTextField.setText("scene_" + newText);
                } else {
                    layoutTextField.setText("");
                }
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return nameTextField;
    }

    @Override
    protected void doOKAction() {
        //super.doOKAction();
        if (StringUtils.isBlank(nameTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_SCENE_EMPTY, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else if (StringUtils.isBlank(layoutTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_lAYOUT_EMPTY, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else if (!FileUtils.isValidFileName(nameTextField.getText()) || !FileUtils.isValidJavaName(nameTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_SCENE_INVALID, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else if (!FileUtils.isValidFileName(layoutTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_lAYOUT_INVALID, StringConstants.TITLE_FILE_ERROR, Messages.getInformationIcon());
        } else {
            if (this.getOKAction().isEnabled()) {
                close(0);
            }
            if (callback != null) {
                callback.showDialogResult(nameTextField.getText(), layoutTextField.getText(), isLauncherScene,isEditWithNss);
            }
        }
    }

    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void showDialogResult(String sceneName, String layoutName, boolean isLauncherScene ,boolean isEditWithNss);
    }
}
