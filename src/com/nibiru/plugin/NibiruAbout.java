package com.nibiru.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.nibiru.plugin.ui.AboutNibiruView;



public class NibiruAbout extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        AboutNibiruView.showAbout();
    }

}
