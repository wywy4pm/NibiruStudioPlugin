package com.nibiru.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.help.HelpManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class CheckforUpdates extends AnAction  {

    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse("https://dev.inibiru.com/#/home");
    }

}
