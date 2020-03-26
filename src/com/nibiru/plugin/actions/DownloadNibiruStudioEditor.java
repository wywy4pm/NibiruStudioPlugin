package com.nibiru.plugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.help.HelpManager;
import com.intellij.openapi.project.DumbAware;
import com.nibiru.plugin.utils.NibiruConfig;
import org.jetbrains.annotations.NotNull;

public class DownloadNibiruStudioEditor extends AnAction  {
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse(NibiruConfig.DownloadEditor_url);
    }
}
