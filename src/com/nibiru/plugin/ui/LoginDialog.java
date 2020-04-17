package com.nibiru.plugin.ui;

import com.intellij.credentialStore.Credentials;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpManager;
import com.nibiru.plugin.http.NibiruDESUtil;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class LoginDialog extends DialogWrapper {
    private JTextField nameTextField;
    private JTextField pwdTextField;
    private JLabel tipLabel;
    private Project project;
    private VirtualFile virtualFile;
    private boolean isneedSavaLoginInfo = false;
    private AnActionEvent anActionEvent;
    private boolean isrefreshLesea = false;
    private boolean isrefreshsdk = false;
    private boolean islicense = false;

    private boolean relogin = false;

    public void setisrelogin(boolean relogin) {
        this.relogin = relogin;
    }

    public void setIslicense(boolean islicense) {
        this.islicense = islicense;
    }

    public boolean isIsrefreshLesea() {
        return isrefreshLesea;
    }

    public void setIsrefreshLesea(boolean isrefreshLesea) {
        this.isrefreshLesea = isrefreshLesea;
    }

    public boolean isIsrefreshsdk() {
        return isrefreshsdk;
    }

    public void setIsrefreshsdk(boolean isrefreshsdk) {
        this.isrefreshsdk = isrefreshsdk;
    }

    public LoginDialog(AnActionEvent anActionEvent, @Nullable Project project, VirtualFile virtualFile) {
        super(true);
        this.project = project;
        this.virtualFile = virtualFile;
        this.anActionEvent = anActionEvent;
        init();
        setTitle(StringConstants.TITLE_NIBIRU_LOGIN);
        setResizable(false);
        setOKButtonText(StringConstants.LOGIN);
        setCancelButtonText(StringConstants.CANCEL);
//        JButton registerBtn = getButton(getCancelAction());
//        if (registerBtn != null) {
//            registerBtn.setAction(new OkAction() {
//                @Override
//                protected void doAction(ActionEvent e) {
//                    Log.i("registerBtn doAction");
//                    BrowserUtil.browse(NibiruConfig.Nibiru_Register);
//                }
//            });
//            registerBtn.setText(StringConstants.REGISTER);
//        }

    }

    public JComponent getTopView() {
        Box topBox = Box.createHorizontalBox();
        topBox.setPreferredSize(new Dimension(380, 40));

        JLabel iconImage = new JLabel();
        iconImage.setIcon(UiUtils.getImageIcon("/icons/ns.png"));
        iconImage.setPreferredSize(new Dimension(20, 20));
        topBox.add(iconImage);

        topBox.add(Box.createHorizontalStrut(10));

        JLabel textNibiru = new JLabel(StringConstants.TITLE_NO_NA_TIP);
        textNibiru.setPreferredSize(new Dimension(200, 20));
        textNibiru.setFont(new Font(null, Font.BOLD, 18));
        textNibiru.setHorizontalAlignment(SwingConstants.LEFT);
        topBox.add(textNibiru);

        topBox.add(Box.createHorizontalGlue());

        JLabel iconVr = new JLabel();
        iconVr.setIcon(UiUtils.getImageIcon("/icons/vr.png"));
        //iconVr.setPreferredSize(new Dimension(20,20));
        topBox.add(iconVr);

        return topBox;
    }

    public JComponent getNbView() {
        Box topBox = Box.createHorizontalBox();
        topBox.setPreferredSize(new Dimension(280, 60));

//        JLabel iconImage = new JLabel();
//        iconImage.setIcon(UiUtils.getImageIcon("/icons/nb.png"));
//        iconImage.setPreferredSize(new Dimension(20,20));
//        topBox.add(iconImage);

        JLabel textNibiru = new JLabel(StringConstants.TITLE_NIBIRU_LOGON);
        textNibiru.setPreferredSize(new Dimension(170, 20));
        textNibiru.setFont(new Font(null, Font.BOLD, 14));
        textNibiru.setHorizontalAlignment(SwingConstants.LEFT);
        topBox.add(textNibiru);

        topBox.add(Box.createHorizontalGlue());

        return topBox;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(380, 220));

        Box topBox = (Box) getTopView();
        dialogPanel.add(topBox);

        Box boxNb = (Box) getNbView();
        Box boxName = Box.createHorizontalBox();
        boxName.setPreferredSize(new Dimension(280, 30));
        JLabel nameLabel = new JLabel(StringConstants.USER_NAME);
        nameLabel.setPreferredSize(new Dimension(80, 25));
        nameLabel.setFont(new Font(null, Font.PLAIN, 13));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        boxName.add(nameLabel);
        boxName.add(Box.createHorizontalStrut(20));
        nameTextField = new JTextField();
        String userName = PropertiesUtils.getString(PropertiesUtils.LOGIN_NAME);
        if (!StringUtils.isEmpty(userName)) {
            String decuserName = NibiruDESUtil.decryptStr(userName, NibiruDESUtil.DEFAULT_KEY_STR);
            nameTextField.setText(decuserName);
        }
        nameTextField.setFont(new Font(null, Font.PLAIN, 13));
        nameTextField.setPreferredSize(new Dimension(180, 25));
        nameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                clearTip();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
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
        String password = PropertiesUtils.getString(PropertiesUtils.LOGIN_PAASWORD);
        if (!StringUtils.isEmpty(password)) {
            String decpassword = NibiruDESUtil.decryptStr(password, NibiruDESUtil.DEFAULT_KEY_STR);
            pwdTextField.setText(decpassword);
        }
        pwdTextField.setFont(new Font(null, Font.PLAIN, 13));
        pwdTextField.setPreferredSize(new Dimension(180, 25));
        pwdTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                clearTip();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        boxPwd.add(pwdTextField);

        Box boxSave = Box.createHorizontalBox();
        boxSave.setPreferredSize(new Dimension(250, 20));
        boxSave.add(Box.createHorizontalStrut(100));
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
        boxSave.add(isSaveUserCheckBox);
        boxSave.add(Box.createHorizontalGlue());

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
        boxSave.add(registerLabel);


//        Box boxBtn = Box.createHorizontalBox();
//        boxBtn.setPreferredSize(new Dimension(250, 50));
//        boxBtn.add(Box.createHorizontalStrut(100));
//        JButton loginBtn = new JButton(StringConstants.REGISTER);
//        loginBtn.setPreferredSize(new Dimension(80, 50));
//
//        boxBtn.add(loginBtn);
//        JButton registerBtn = new JButton(StringConstants.LOGIN);
//        registerBtn.setPreferredSize(new Dimension(80, 50));
//        boxBtn.add(registerBtn);

        Box boxTips = Box.createHorizontalBox();
        boxTips.setPreferredSize(new Dimension(280, 15));
        tipLabel = new JLabel(" ");
        tipLabel.setPreferredSize(new Dimension(280, 15));
        tipLabel.setFont(new Font(null, Font.PLAIN, 11));
        tipLabel.setHorizontalAlignment(SwingConstants.LEFT);
        tipLabel.setForeground(JBColor.RED);
        boxTips.add(tipLabel);
        boxTips.add(Box.createHorizontalGlue());

        Box vBox = Box.createVerticalBox();
        vBox.add(boxNb);
        vBox.add(boxName);
        vBox.add(boxPwd);
        vBox.add(boxSave);
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(boxTips);
        vBox.add(Box.createVerticalStrut(80));
//        vBox.add(boxBtn);
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
            //Messages.showMessageDialog(StringConstants.MSG_USER_NAME_EMPTY, StringConstants.TITLE_LOGIN_ERROR, UiUtils.getInfoIcon());
            tipLabel.setText(StringConstants.MSG_USER_NAME_EMPTY);
        } else if (StringUtils.isBlank(pwdTextField.getText())) {
            //Messages.showMessageDialog(StringConstants.MSG_USER_PWD_EMPTY, StringConstants.TITLE_LOGIN_ERROR, UiUtils.getInfoIcon());
            tipLabel.setText(StringConstants.MSG_USER_PWD_EMPTY);
        } else {
            HttpManager.Login(nameTextField.getText(), pwdTextField.getText(), new HttpManager.LoginCallback() {
                @Override
                public void onSucceed(LoginBean loginBean) {
                    Toast.make(project, MessageType.INFO, StringConstants.LOGIN_SUCCESS);
                    if (isneedSavaLoginInfo) {
                        PropertiesUtils.setString(PropertiesUtils.LOGIN_NAME, NibiruDESUtil.encryptStr(nameTextField.getText(), NibiruDESUtil.DEFAULT_KEY_STR));
                        PropertiesUtils.setString(PropertiesUtils.LOGIN_PAASWORD, NibiruDESUtil.encryptStr(pwdTextField.getText(), NibiruDESUtil.DEFAULT_KEY_STR));
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
                            ActivateDialog activateDialog = new ActivateDialog(anActionEvent, project, virtualFile);
                            activateDialog.show();
                        } else {
                            NibiruConfig.deviceIsActivate = true;
                            if (relogin) {
                                return;
                            }
                            if (islicense) {
                                NsNoexitsTipDialog noEnoughCountDialog = new NsNoexitsTipDialog(anActionEvent.getProject(), virtualFile, false);
                                noEnoughCountDialog.show();
                            } else {
                                if (isrefreshLesea) {
                                    FileUtils.createBinFile(NibiruConfig.loginBean, project, virtualFile);
                                }
                                if (isrefreshsdk) {
                                    SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent, project, virtualFile);
                                    sdkSettingDialog.show();
//                                    SdkModifyDialog sdkModifyDialog = new SdkModifyDialog(anActionEvent, project, virtualFile);
//                                    sdkModifyDialog.show();
                                } else {
                                    String sdkPath = FileUtils.getSdkPath(project, virtualFile);
                                    if (StringUtils.isBlank(sdkPath)) {
                                        SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent, project, virtualFile);
                                        sdkSettingDialog.show();
//                                        SdkModifyDialog sdkModifyDialog = new SdkModifyDialog(anActionEvent, project, virtualFile);
//                                        sdkModifyDialog.show();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailed(int errorCode) {
                    if (errorCode == 1) {
                        //Toast.make(project, MessageType.INFO, StringConstants.LOGIN_WRONG);
                        tipLabel.setText(StringConstants.LOGIN_WRONG);
                    } else {
                        //Toast.make(project, MessageType.INFO, StringConstants.LOGIN_FAIL + errorCode);
                        tipLabel.setText(StringConstants.LOGIN_FAIL + errorCode);
                    }
                }
            });
        }
    }

    private void clearTip() {
        if (tipLabel != null) {
            tipLabel.setText(" ");
        }
    }
}
