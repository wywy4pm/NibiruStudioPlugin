package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
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
            if (virtualFile != null && virtualFile.getPath().matches(".*?\\.java$")) {
                PsiClass[] classes = ((PsiJavaFile) data).getClasses();
                if (classes.length > 0) {
                    PsiClass sceneClass = classes[0];
                    PsiReferenceList psiReferenceList = sceneClass.getExtendsList();
                    if (psiReferenceList == null) {
                        e.getPresentation().setEnabled(false);
                    } else {
                        PsiReferenceList.Role role = psiReferenceList.getRole();
                        if (role == PsiReferenceList.Role.EXTENDS_LIST) {
                            if (psiReferenceList.getReferenceElements().length != 0) {
                                PsiJavaCodeReferenceElement[] referenceElements = psiReferenceList.getReferenceElements();
                                PsiJavaCodeReferenceElement referenceElement = referenceElements[0];
                                String referenceName = referenceElement.getReferenceName();
                                if (referenceName.equalsIgnoreCase("XBaseScene")) {
                                    e.getPresentation().setEnabled(true);
                                } else {
                                    e.getPresentation().setEnabled(false);
                                }
                            } else {
                                e.getPresentation().setEnabled(false);
                            }
                        } else {
                            e.getPresentation().setEnabled(false);
                        }
                    }
                } else {
                    e.getPresentation().setEnabled(false);
                }
            } else if (virtualFile != null && virtualFile.getPath().matches(".*?\\.nss$")) {
                String sdkPath = FileUtils.getSdkPath(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE));
                if (StringUtils.isEmpty(sdkPath)) {
                    e.getPresentation().setEnabled(false);
                } else {
                    e.getPresentation().setEnabled(true);
                }
            } else {
                e.getPresentation().setEnabled(false);
            }
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    public void actionPerformed(AnActionEvent e) {
        FileUtils.openNssFile(e, e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE));
    }
}
