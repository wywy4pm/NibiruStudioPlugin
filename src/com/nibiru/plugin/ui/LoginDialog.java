package com.nibiru.plugin.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.nibiru.plugin.utils.NibiruConfig;
import com.nibiru.plugin.utils.StringConstants;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginDialog extends DialogWrapper {
    private JTextField nameTextField;
    private JTextField pwdTextField;

    public LoginDialog(@Nullable Project project) {
        super(true);
        init();
        setTitle(StringConstants.TITLE_NIBIRU_LOGIN);
        setResizable(false);
        setOKButtonText(StringConstants.LOGIN);
        setCancelButtonText(StringConstants.CANCEL);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(250, 90));

        Box boxName = Box.createHorizontalBox();
        boxName.setPreferredSize(new Dimension(250, 30));
        JLabel nameLabel = new JLabel(StringConstants.USER_NAME);
        nameLabel.setPreferredSize(new Dimension(50, 25));
        nameLabel.setFont(new Font(null, Font.PLAIN, 13));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxName.add(nameLabel);
        boxName.add(Box.createHorizontalStrut(20));
        nameTextField = new JTextField();
        nameTextField.setFont(new Font(null, Font.PLAIN, 13));
        nameTextField.setPreferredSize(new Dimension(180, 25));
        boxName.add(nameTextField);

        Box boxPwd = Box.createHorizontalBox();
        boxPwd.setPreferredSize(new Dimension(250, 30));
        JLabel pwdLabel = new JLabel(StringConstants.USER_PWD);
        pwdLabel.setPreferredSize(new Dimension(50, 25));
        pwdLabel.setFont(new Font(null, Font.PLAIN, 13));
        pwdLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxPwd.add(pwdLabel);
        boxPwd.add(Box.createHorizontalStrut(20));
        pwdTextField = new JPasswordField();
        pwdTextField.setFont(new Font(null, Font.PLAIN, 13));
        pwdTextField.setPreferredSize(new Dimension(180, 25));
        boxPwd.add(pwdTextField);

        Box boxRegister = Box.createHorizontalBox();
        boxRegister.setPreferredSize(new Dimension(250, 20));
        boxRegister.add(Box.createHorizontalGlue());
        JLabel registerLabel = new JLabel(StringConstants.TO_REGISTER);
        registerLabel.setPreferredSize(new Dimension(30, 25));
        registerLabel.setFont(new Font(null, Font.PLAIN, 13));
        registerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        registerLabel.setForeground(JBColor.BLUE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setToolTipText(StringConstants.TO_REGISTER_TIP);
        registerLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse(NibiruConfig.Nibiru_Register);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        boxRegister.add(registerLabel);

        Box vBox = Box.createVerticalBox();
        vBox.add(boxName);
        vBox.add(boxPwd);
        vBox.add(boxRegister);
        dialogPanel.add(vBox);

        return dialogPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return nameTextField;
    }

    @Override
    protected void doOKAction() {
        if (StringUtils.isBlank(nameTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_USER_NAME_EMPTY, StringConstants.TITLE_LOGIN_ERROR, Messages.getInformationIcon());
        } else if (StringUtils.isBlank(pwdTextField.getText())) {
            Messages.showMessageDialog(StringConstants.MSG_USER_PWD_EMPTY, StringConstants.TITLE_LOGIN_ERROR, Messages.getInformationIcon());
        } else {
            if (this.getOKAction().isEnabled()) {
                close(0);
            }
        }
    }
}
