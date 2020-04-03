package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.ui.AboutNibiruDialog;
import com.nibiru.plugin.ui.AboutNibiruView;


public class NibiruAbout extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        //AboutNibiruView.showAbout();
        AboutNibiruDialog aboutNibiruDialog = new AboutNibiruDialog(e.getProject(), file);
        aboutNibiruDialog.show();
    }

}
