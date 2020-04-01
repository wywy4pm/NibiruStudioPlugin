package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.nibiru.plugin.utils.ModifyAndroidManifest;
import com.nibiru.plugin.utils.ModuleUtils;
import com.nibiru.plugin.utils.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class LauncherScene extends AnAction {
    private VirtualFile file;
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        project = anActionEvent.getProject();
        PsiFile data = PlatformDataKeys.PSI_FILE.getData(anActionEvent.getDataContext());
        PsiClass[] classes = ((PsiJavaFile) data).getClasses();
        PsiClass aClass = classes[0];
        String qualifiedName = aClass.getQualifiedName();
        ModifyAndroidManifest manifest = new ModifyAndroidManifest(project, file, qualifiedName);
        manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.LauncherScene);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        String sdkpath = PropertiesUtils.getString(ModuleUtils.getModulePath(e.getProject(), e.getData(PlatformDataKeys.VIRTUAL_FILE)));
        if (StringUtils.isEmpty(sdkpath)){
            e.getPresentation().setEnabled(false);
        }else {
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
                } else {
                    e.getPresentation().setEnabled(false);
                }
            } else {
                e.getPresentation().setEnabled(false);
            }
        }
    }
}
