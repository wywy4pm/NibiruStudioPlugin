package com.nibiru.plugin.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.CredentialUtils;
import com.nibiru.plugin.utils.NibiruConfig;
import com.nibiru.plugin.utils.StringConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class NsNoexitsTipDialog extends DialogWrapper {
    private VirtualFile folder;
    private Project project;
    private boolean isNoEnoughCount;
    private JLabel tipcontent;

    public NsNoexitsTipDialog(Project project, VirtualFile folder, boolean isNoEnoughCount) {
        super(true);
        this.project = project;
        this.folder = folder;
        this.isNoEnoughCount = isNoEnoughCount;
        init();
        setTitle(StringConstants.TITLE_NO_NA_TIP);
        setResizable(false);

        if (tipcontent != null) {
            if (isNoEnoughCount) {
                tipcontent.setText(StringConstants.TO_ACTIVATE_COUNT_NOT);
                getOKAction().setEnabled(true);
            } else {
                tipcontent = new JLabel(StringConstants.NO_NA_TIP);
                //getOKAction().setEnabled(false);
                setOKButtonText(StringConstants.LOG_OUT);
            }
        }
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setPreferredSize(new Dimension(300, 100));
        tipcontent = new JLabel(StringConstants.NO_NA_TIP);
        tipcontent.setPreferredSize(new Dimension(300, 50));
        tipcontent.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(tipcontent);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (isNoEnoughCount) {
            BrowserUtil.browse(NibiruConfig.device_activate_url);
        } else {
            //TODO 执行退出登录操作
            logout();
        }
    }

    private void logout() {
        NibiruConfig.isLogin=false;
        CredentialUtils.putString(CredentialUtils.LOGIN_INFO, "", "");
    }
}
