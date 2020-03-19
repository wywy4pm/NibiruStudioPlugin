package com.nibiru.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.help.HelpManager;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class DownloadNibiruStudioEditor extends AnAction  {
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse("http://www.inibiru.com/studio.html");
    }
}
