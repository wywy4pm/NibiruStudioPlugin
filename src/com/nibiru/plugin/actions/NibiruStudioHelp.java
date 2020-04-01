package com.nibiru.plugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.nibiru.plugin.utils.NibiruConfig;
import org.jetbrains.annotations.NotNull;

public class NibiruStudioHelp extends AnAction  {

    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse(NibiruConfig.NibiruHelp_url);
    }
}
