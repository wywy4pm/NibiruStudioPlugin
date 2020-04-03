package com.nibiru.plugin.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpManager;
import com.nibiru.plugin.json.JSONObject;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ActivateDialog extends DialogWrapper {
    private Project project;
    private VirtualFile virtualFile;

    public ActivateDialog(@Nullable Project project, VirtualFile virtualFile) {
        super(true);
        this.project = project;
        this.virtualFile = virtualFile;
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
        dialogPanel.setPreferredSize(new Dimension(400, 50));
        JLabel tipLabel = new JLabel(StringConstants.TO_ACTIVATE);
        tipLabel.setPreferredSize(new Dimension(400, 25));
        tipLabel.setFont(new Font(null, Font.PLAIN, 13));
        tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(tipLabel);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        LoginBean loginBean = NibiruConfig.loginBean;
        int uid = loginBean.getAccount().getId();
        HttpManager.DeviceAuth(uid + "", new HttpManager.DeviceAuthCallback() {
            @Override
            public void onResult(String result) {
                if (!StringUtils.isEmpty(result)) {
                    JSONObject json = new JSONObject(result);
                    int resCode = json.getInt("resCode");
                    if (resCode == -2) {
                        Toast.make(project, MessageType.INFO, StringConstants.ACTIVATE_ERROR_1);
                    } else if (resCode == -1) {
                        Toast.make(project, MessageType.INFO, StringConstants.ACTIVATE_ERROR_2);
                    } else if (resCode == 0) {
                        Toast.make(project, MessageType.INFO,StringConstants.ACTIVATE_SUCCESS);
                        NibiruConfig.deviceIsActivate = true;
                        if (getOKAction().isEnabled()) {
                            close(0);
                        }
                        String sdkPath = FileUtils.getSdkPath(project, virtualFile);
                        if (StringUtils.isBlank(sdkPath)) {
                            SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(project, virtualFile);
                            sdkSettingDialog.show();
                        }
                    } else if (resCode == 1) {
                        Toast.make(project, MessageType.INFO, StringConstants.ACTIVATE_FAIL);
                    } else if (resCode == 2) {
                        NsNoexitsTipDialog noEnoughCountDialog = new NsNoexitsTipDialog(project, virtualFile, true);
                        noEnoughCountDialog.show();
                    } else if (resCode == 3) {
                        Toast.make(project, MessageType.INFO, StringConstants.ACTIVATE_ERROR_3);
                        BrowserUtil.browse(NibiruConfig.Update_url);
                    } else if (resCode == 4) {
                        Toast.make(project, MessageType.INFO, StringConstants.ACTIVATE_ERROR_4);
                    }
                }
            }
        });

        if (getOKAction().isEnabled()) {
            close(0);
        }
    }
}
