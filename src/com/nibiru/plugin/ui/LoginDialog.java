package com.nibiru.plugin.ui;

import com.intellij.credentialStore.Credentials;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpManager;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginDialog extends DialogWrapper {
    private JTextField nameTextField;
    private JTextField pwdTextField;
    private Project project;
    private VirtualFile virtualFile;
    private boolean isneedSavaLoginInfo = false;

    public LoginDialog(@Nullable Project project, VirtualFile virtualFile) {
        super(true);
        this.project = project;
        this.virtualFile = virtualFile;
        init();
        setTitle(StringConstants.TITLE_NIBIRU_LOGIN);
        setResizable(false);
        setOKButtonText(StringConstants.REGITER);
        setCancelButtonText(StringConstants.LOGIN);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(280, 90));
        Credentials loginInfo = CredentialUtils.getString(CredentialUtils.LOGIN_INFO);
        Box boxName = Box.createHorizontalBox();
        boxName.setPreferredSize(new Dimension(280, 30));
        JLabel nameLabel = new JLabel(StringConstants.USER_NAME);
        nameLabel.setPreferredSize(new Dimension(80, 25));
        nameLabel.setFont(new Font(null, Font.PLAIN, 13));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxName.add(nameLabel);
        boxName.add(Box.createHorizontalStrut(20));
        nameTextField = new JTextField();
        if (loginInfo != null) {
            String userName = loginInfo.getUserName();
            if (!StringUtils.isEmpty(userName)) {
                nameTextField.setText(userName);
            }
        }
        nameTextField.setFont(new Font(null, Font.PLAIN, 13));
        nameTextField.setPreferredSize(new Dimension(180, 25));
        boxName.add(nameTextField);

        Box boxPwd = Box.createHorizontalBox();
        boxPwd.setPreferredSize(new Dimension(280, 30));
        JLabel pwdLabel = new JLabel(StringConstants.USER_PWD);
        pwdLabel.setPreferredSize(new Dimension(80, 25));
        pwdLabel.setFont(new Font(null, Font.PLAIN, 13));
        pwdLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxPwd.add(pwdLabel);
        boxPwd.add(Box.createHorizontalStrut(20));
        pwdTextField = new JPasswordField();
        if (loginInfo != null) {
            String password = loginInfo.getPasswordAsString();
            if (!StringUtils.isEmpty(password)) {
                pwdTextField.setText(password);
            }
        }
        pwdTextField.setFont(new Font(null, Font.PLAIN, 13));
        pwdTextField.setPreferredSize(new Dimension(180, 25));
        boxPwd.add(pwdTextField);

//        Box boxCheck = Box.createHorizontalBox();
//        boxCheck.setPreferredSize(new Dimension(280, 30));
//        boxCheck.add(Box.createHorizontalGlue());
//        JCheckBox isSaveUserCheckBox = new JCheckBox(StringConstants.SAVE_USER);
//        isSaveUserCheckBox.setPreferredSize(new Dimension(180, 20));
//        isSaveUserCheckBox.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                JCheckBox checkBox = (JCheckBox) e.getSource();
//                if (checkBox != null) {
//                    isneedSavaLoginInfo = checkBox.isSelected();
//                }
//            }
//        });
//        boxCheck.add(isSaveUserCheckBox);

        Box boxRegister = Box.createHorizontalBox();
        boxRegister.setPreferredSize(new Dimension(250, 20));
        boxRegister.add(Box.createHorizontalStrut(100));

        JCheckBox isSaveUserCheckBox = new JCheckBox(StringConstants.SAVE_USER);
        isSaveUserCheckBox.setPreferredSize(new Dimension(130, 25));
        isSaveUserCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox != null) {
                    isneedSavaLoginInfo = checkBox.isSelected();
                }
            }
        });
        boxRegister.add(isSaveUserCheckBox);

        JLabel registerLabel = new JLabel(StringConstants.TO_REGISTER);
        registerLabel.setPreferredSize(new Dimension(30, 25));
        registerLabel.setFont(new Font(null, Font.PLAIN, 12));
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
        //vBox.add(boxCheck);
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
            HttpManager.Login(nameTextField.getText(), pwdTextField.getText(), new HttpManager.LoginCallback() {
                @Override
                public void onSucceed(LoginBean loginBean) {
                    Toast.make(project, MessageType.INFO, "登录成功!");
                    if (isneedSavaLoginInfo) {
                        CredentialUtils.putString(CredentialUtils.LOGIN_INFO, nameTextField.getText(), pwdTextField.getText());
                    }
                    if (getOKAction().isEnabled()) {
                        close(0);
                    }
                    Log.i(loginBean.toString());
                    NibiruConfig.isLogin = true;
                    NibiruConfig.loginBean = loginBean;
                    if (loginBean.getAccount() != null) {
                        LoginBean.AccountBean account = loginBean.getAccount();
                        if (!account.isActiveStatus()) {
                            ActivateDialog activateDialog = new ActivateDialog(project, virtualFile);
                            activateDialog.show();
                        } else {
                            NibiruConfig.deviceIsActivate = true;
                            FileUtils.createBinFile(loginBean, project, virtualFile);
                            String sdkPath = FileUtils.getSdkPath(project, virtualFile);
                            if (StringUtils.isBlank(sdkPath)) {
                                SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(project, virtualFile);
                                sdkSettingDialog.show();
                            }
                        }
                    }
                }

                @Override
                public void onFailed(int errorCode) {
                    if (errorCode == 1) {
                        Toast.make(project, MessageType.INFO, "用户名或密码错误");
                    } else {
                        Toast.make(project, MessageType.INFO, "登录失败错误码: " + errorCode);
                    }
                }
            });
        }
    }
}
