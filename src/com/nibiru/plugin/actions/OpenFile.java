package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.Log;
import org.apache.commons.lang.StringUtils;


/**
 * 开发指定文件扩展名的方法
 */
public class OpenFile extends AnAction {

    public void update(AnActionEvent e) {
        PsiFile data = PlatformDataKeys.PSI_FILE.getData(e.getDataContext());
        if (data != null) {
            VirtualFile virtualFile = data.getVirtualFile();
            if (virtualFile != null && virtualFile.getPath().endsWith(".nss")) {
                String sdkPath = FileUtils.getSdkPath(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE));
                if (StringUtils.isEmpty(sdkPath)) {
                    e.getPresentation().setEnabled(false);
                } else {
                    e.getPresentation().setEnabled(true);
                }
            } else {
                Editor editor = e.getData(PlatformDataKeys.EDITOR);
                if (editor == null) {
                    e.getPresentation().setEnabled(false);
                } else {
                    PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, e.getProject());
                    PsiFile layoutFile = FileUtils.getLayoutFileFromCaret(editor, file);
                    if (layoutFile != null) {
                        e.getPresentation().setEnabled(true);
                    } else {
                        e.getPresentation().setEnabled(false);
                    }
                }
            }
        } else {
            e.getPresentation().setEnabled(false);
        }
    }


    public void actionPerformed(AnActionEvent e) {
        FileUtils.openNssFile(e, e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE));
    }
}
