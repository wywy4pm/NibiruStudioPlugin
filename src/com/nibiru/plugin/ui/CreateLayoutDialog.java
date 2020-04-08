package com.nibiru.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class CreateLayoutDialog extends DialogWrapper {
    private Callback callback;
    private JTextField layoutTextField;
    private JCheckBox isNssCheckBox;
    private boolean isEditWithNss;

    public CreateLayoutDialog(Project project, VirtualFile folder) {
        super(true);
        init();
        setTitle(StringConstants.TITLE_CRATE_LAYOUT);
        setResizable(false);
        setOKButtonText(StringConstants.SDK_OK);
        isEditWithNss = true;
        isNssCheckBox.setSelected(true);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(500, 300));

        Box topBox = (Box) getTopView();
        dialogPanel.add(topBox);

        Box titleBox = Box.createHorizontalBox();
        titleBox.setPreferredSize(new Dimension(400, 80));
        JLabel titleLabel = new JLabel(StringConstants.P_CREATE_LAYOUT);
        titleLabel.setPreferredSize(new Dimension(250, 80));
        titleLabel.setFont(new Font(null, Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleBox.add(titleLabel);
        titleBox.add(Box.createHorizontalGlue());

        Box boxCheck = Box.createHorizontalBox();
        boxCheck.setPreferredSize(new Dimension(400, 30));
        boxCheck.add(Box.createHorizontalGlue());

        Box boxLayout = Box.createHorizontalBox();
        boxLayout.setPreferredSize(new Dimension(400, 30));
        JLabel layoutLabel = new JLabel(StringConstants.LAYOUT_NAME);
        layoutLabel.setPreferredSize(new Dimension(90, 30));
        layoutLabel.setFont(new Font(null, Font.PLAIN, 13));
        layoutLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxLayout.add(layoutLabel);
        boxLayout.add(Box.createHorizontalStrut(20));
        layoutTextField = new JTextField();
        layoutTextField.setFont(new Font(null, Font.PLAIN, 13));
        layoutTextField.setPreferredSize(new Dimension(290, 25));
        layoutTextField.setText(StringConstants.DEFAULT_lAYOUT_NAME);
        boxLayout.add(layoutTextField);

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
        vBox.add(titleBox);
        vBox.add(boxLayout);
        vBox.add(boxCheck);
        vBox.add(boxNssCheck);
        dialogPanel.add(vBox);

        return dialogPanel;
    }

    public JComponent getTopView() {
        Box topBox = Box.createHorizontalBox();
        topBox.setPreferredSize(new Dimension(500, 40));

        JLabel iconImage = new JLabel();
        iconImage.setIcon(UiUtils.getImageIcon("/icons/ns.png"));
        iconImage.setPreferredSize(new Dimension(20,20));
        topBox.add(iconImage);

        JLabel textNibiru = new JLabel(StringConstants.TITLE_NO_NA_TIP);
        textNibiru.setPreferredSize(new Dimension(200, 20));
        textNibiru.setFont(new Font(null, Font.BOLD, 14));
        textNibiru.setHorizontalAlignment(SwingConstants.LEFT);
        topBox.add(textNibiru);

        topBox.add(Box.createHorizontalGlue());

        JLabel iconVr = new JLabel();
        iconVr.setIcon(UiUtils.getImageIcon("/icons/vr.png"));
        //iconVr.setPreferredSize(new Dimension(20,20));
        topBox.add(iconVr);

        return topBox;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return layoutTextField;
    }

    @Override
    protected void doOKAction() {
       if (StringUtils.isBlank(layoutTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_lAYOUT_EMPTY, StringConstants.TITLE_FILE_ERROR, UiUtils.getInfoIcon());
        } else if (!FileUtils.isValidFileName(layoutTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_FILE_lAYOUT_INVALID, StringConstants.TITLE_FILE_ERROR, UiUtils.getErrorIcon());
        } else {
            if (this.getOKAction().isEnabled()) {
                close(0);
            }
            if (callback != null) {
                callback.showDialogResult( layoutTextField.getText(), isEditWithNss);
            }
        }
    }

    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void showDialogResult(String layoutName, boolean isEditWithNss);
    }
}
