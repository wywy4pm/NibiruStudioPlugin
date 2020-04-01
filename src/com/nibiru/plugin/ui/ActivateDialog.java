package com.nibiru.plugin.ui;

import com.google.gson.Gson;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpClientUtil;
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
        dialogPanel.setPreferredSize(new Dimension(200, 50));
        JLabel tipLabel = new JLabel(StringConstants.TO_ACTIVATE);
        tipLabel.setPreferredSize(new Dimension(200, 25));
        tipLabel.setFont(new Font(null, Font.PLAIN, 13));
        tipLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dialogPanel.add(tipLabel);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        String string = PropertiesUtils.getString(PropertiesUtils.LOGIN_DATA);
        if (!StringUtils.isEmpty(string)) {
            Gson gson = new Gson();
            LoginBean loginBean = gson.fromJson(string, LoginBean.class);
            int uid = loginBean.getAccount().getId();
            String pagename = GradleUtils.getBuildpagename(project, virtualFile);
            if (!StringUtils.isEmpty(pagename)) {
                HttpManager.DeviceAuth(uid + "", GradleUtils.getBuildpagename(project, virtualFile), new HttpManager.DeviceAuthCallback() {
                    @Override
                    public void onResult(String result) {
                        if (!StringUtils.isEmpty(result)) {
                            JSONObject json = new JSONObject(result);
                            Log.i(result);
                            int resCode = json.getInt("resCode");
                            if (resCode==0){
                                PropertiesUtils.setBoolean(PropertiesUtils.ACTIVATE_STATE,true);
                            }else {
                                PropertiesUtils.setBoolean(PropertiesUtils.ACTIVATE_STATE,false);
                            }
                            if (resCode == -2) {
                                Toast.make(project, MessageType.INFO, "服务器异常!");
                            } else if (resCode == -1) {
                                Toast.make(project, MessageType.INFO, "请求激活参数缺失!");
                            } else if (resCode == 0) {
                                Toast.make(project, MessageType.INFO, "设备激活成功!");
//                                String certUrl = json.getString("certUrl");
                                if (getOKAction().isEnabled()) {
                                    close(0);
                                }
                                String modulePath = ModuleUtils.getModulePath(project, virtualFile);
                                if (!StringUtils.isBlank(modulePath)) {
                                    String sdkPath = PropertiesUtils.getString(modulePath);
                                    Log.i("sdkPath = " + sdkPath);
                                    if (StringUtils.isBlank(sdkPath)) {
                                        SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(project, virtualFile);
                                        sdkSettingDialog.show();
                                    }
                                }
                            } else if (resCode == 1) {
                                Toast.make(project, MessageType.INFO, "设备激活失败!");
                            } else if (resCode == 2) {
                                Toast.make(project, MessageType.INFO, "激活码不足!");
                                BrowserUtil.browse(NibiruConfig.Update_url);
                            } else if (resCode == 3) {
                                Toast.make(project, MessageType.INFO, "开发者不存在!");
                                BrowserUtil.browse(NibiruConfig.Update_url);
                            } else if (resCode == 4) {
                                Toast.make(project, MessageType.INFO, "keystore生成失败!");
                            }
                        }
                    }
                });
            }
        }

    }
}
