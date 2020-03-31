package com.nibiru.plugin.ui;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpClientUtil;
import com.nibiru.plugin.http.HttpManager;
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
                HttpManager.DeviceAuth(uid + "", GradleUtils.getBuildpagename(project, virtualFile));
            }
        }
        if (this.getOKAction().isEnabled()) {
            close(0);
        }
    }
}
