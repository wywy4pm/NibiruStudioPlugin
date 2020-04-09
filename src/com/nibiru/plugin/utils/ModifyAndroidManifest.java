package com.nibiru.plugin.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

/**
 * 修改清单文件的类
 */
public class ModifyAndroidManifest {
    private VirtualFile folder;
    private Project project;
    private String scenePath;
    private String packagepath;

    public enum ModifyManifestType {
        LauncherScene,
        NIBIRU_PLUGIN_IDS,
        APP_KEY,
        REMOVE_THEME
    }


    public ModifyAndroidManifest(Project project, VirtualFile folder, String scenepath) {
        this.folder = folder;
        this.project = project;
        this.scenePath = scenepath;
    }

    public void modifyManifestXml(ModifyManifestType modifyManifestType) {
        String curModulePath = ModuleUtils.getCurModulePath(project, folder);
        VirtualFile modulefile = LocalFileSystem.getInstance().findFileByPath(curModulePath);
        if (modulefile != null && modulefile.exists()) {
            VirtualFile[] children = modulefile.getChildren();
            for (VirtualFile virtualFile : children) {
                if (virtualFile.isDirectory()) {
                    String name = virtualFile.getName();
                    if (name.equalsIgnoreCase("src")) {
                        VirtualFile[] srcChildren = virtualFile.getChildren();
                        for (VirtualFile srcChild : srcChildren) {
                            String childName = srcChild.getName();
                            if (childName.equalsIgnoreCase("main")) {
                                VirtualFile[] childVirtualFile = srcChild.getChildren();
                                for (VirtualFile file : childVirtualFile) {
                                    if (!file.isDirectory()) {
                                        if (file.getName().equalsIgnoreCase("AndroidManifest.xml")) {
                                            if (modifyManifestType == ModifyManifestType.LauncherScene) {
                                                modifyLuancherScene(file);
                                            } else if (modifyManifestType == ModifyManifestType.NIBIRU_PLUGIN_IDS) {
                                                modifyPro(file);
                                            } else if (modifyManifestType == ModifyManifestType.APP_KEY) {
                                                modifyAppkey(file);
                                            }else if (modifyManifestType==ModifyManifestType.REMOVE_THEME){
                                                removeTheme(file);
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 去除掉theme主题配置
     * @param file
     */
    private void removeTheme(VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getCachedDocument(file);
        if ((document != null) && (document.isWritable()) && (file.getPresentableName().toLowerCase().equals("androidmanifest.xml"))) {
            String androidManifest = document.getCharsSequence().toString();
            XmlFile psiFile = (XmlFile) PsiFileFactory.getInstance(project).createFileFromText("androidManifest", StdFileTypes.XML, androidManifest);
            XmlDocument xmlDocument = psiFile.getDocument();
            if (xmlDocument != null && xmlDocument.getRootTag() != null) {
                XmlTag rootTag = xmlDocument.getRootTag();
                XmlTag[] subTags = rootTag.getSubTags();
                for (int k = 0; k < subTags.length; k++) {
                    String name = subTags[k].getName();
                    //删除掉theme主题配置
                    if (name.equalsIgnoreCase("application")) {
                        XmlAttribute themeAttribute = subTags[k].getAttribute("android:theme");
                        if (themeAttribute != null) {
                            themeAttribute.delete();
                        }
                        CodeStyleManager.getInstance(psiFile.getProject()).reformat(psiFile);
                        break;
                    }
                }
            }
            Runnable writeAction = new WriteAction(xmlDocument.getText(), document);
            ApplicationManager.getApplication().runWriteAction(writeAction);
        }
    }

    /**
     * 修改pro版本配置
     *
     * @param childFile
     */
    private void modifyAppkey(VirtualFile childFile) {
        Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
        if ((document != null) && (document.isWritable()) && (childFile.getPresentableName().toLowerCase().equals("androidmanifest.xml"))) {
            String androidManifest = document.getCharsSequence().toString();
            XmlFile psiFile = (XmlFile) PsiFileFactory.getInstance(project).createFileFromText("androidManifest", StdFileTypes.XML, androidManifest);
            XmlDocument xmlDocument = psiFile.getDocument();
            if (xmlDocument != null && xmlDocument.getRootTag() != null) {
                XmlTag rootTag = xmlDocument.getRootTag();
                XmlTag[] subTags = rootTag.getSubTags();
                for (int k = 0; k < subTags.length; k++) {
                    String name = subTags[k].getName();
                    if (name.equalsIgnoreCase("application")) {
                        XmlTag[] applicationtag = subTags[k].getSubTags();
                        boolean pro_isexist = false;
                        for (int m = 0; m < applicationtag.length; m++) {
                            String appsubname = applicationtag[m].getName();
                            if (appsubname.equalsIgnoreCase("meta-data")) {
                                XmlAttribute attribute = applicationtag[m].getAttribute("android:name");
                                if (attribute != null) {
                                    if (attribute.getValue().equalsIgnoreCase("NIBIRU_SDK_KEY")) {
                                        pro_isexist = true;
                                        XmlAttribute attributevalue = applicationtag[m].getAttribute("android:value");
                                        attributevalue.setValue(NibiruConfig.appkey);
                                        break;
                                    }
                                }
                            }
                        }
                        if (!pro_isexist) {
                            XmlTag metadataTag = subTags[k].createChildTag("meta-data", null, null, false);
                            if (metadataTag != null) {
                                metadataTag.setAttribute("android:name", "NIBIRU_SDK_KEY");
                                metadataTag.setAttribute("android:value", NibiruConfig.appkey);
                                subTags[k].addSubTag(metadataTag, true);
                            }
                        }
                        CodeStyleManager.getInstance(psiFile.getProject()).reformat(psiFile);
                        break;
                    }
                }
            }
            Runnable writeAction = new WriteAction(xmlDocument.getText(), document);
            ApplicationManager.getApplication().runWriteAction(writeAction);
        }
    }

    /**
     * 修改pro版本配置
     *
     * @param childFile
     */
    private void modifyPro(VirtualFile childFile) {
        Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
        if ((document != null) && (document.isWritable()) && (childFile.getPresentableName().toLowerCase().equals("androidmanifest.xml"))) {
            String androidManifest = document.getCharsSequence().toString();
            XmlFile psiFile = (XmlFile) PsiFileFactory.getInstance(project).createFileFromText("androidManifest", StdFileTypes.XML, androidManifest);
            XmlDocument xmlDocument = psiFile.getDocument();
            if (xmlDocument != null && xmlDocument.getRootTag() != null) {
                XmlTag rootTag = xmlDocument.getRootTag();
                XmlTag[] subTags = rootTag.getSubTags();
                for (int k = 0; k < subTags.length; k++) {
                    String name = subTags[k].getName();
                    if (name.equalsIgnoreCase("application")) {
                        XmlTag[] applicationtag = subTags[k].getSubTags();
                        boolean pro_isexist = false;
                        for (int m = 0; m < applicationtag.length; m++) {
                            String appsubname = applicationtag[m].getName();
                            if (appsubname.equalsIgnoreCase("meta-data")) {
                                XmlAttribute attribute = applicationtag[m].getAttribute("android:name");
                                if (attribute != null) {
                                    if (attribute.getValue().equalsIgnoreCase("NIBIRU_PLUGIN_IDS")) {
                                        pro_isexist = true;
                                        XmlAttribute attributevalue = applicationtag[m].getAttribute("android:value");
                                        if (!attributevalue.getValue().contains("BASIS")) {
                                            attributevalue.setValue("BASIS");
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if (!pro_isexist) {
                            XmlTag metadataTag = subTags[k].createChildTag("meta-data", null, null, false);
                            if (metadataTag != null) {
                                metadataTag.setAttribute("android:name", "NIBIRU_PLUGIN_IDS");
                                metadataTag.setAttribute("android:value", "BASIS");
                                subTags[k].addSubTag(metadataTag, true);
                            }
                        }
                        CodeStyleManager.getInstance(psiFile.getProject()).reformat(psiFile);
                        break;
                    }
                }
            }
            Runnable writeAction = new WriteAction(xmlDocument.getText(), document);
            ApplicationManager.getApplication().runWriteAction(writeAction);
        }
    }

    /**
     * 修改launcherScene配置
     *
     * @param childFile
     */
    private void modifyLuancherScene(VirtualFile childFile) {
        Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
        if ((document != null) && (document.isWritable()) && (childFile.getPresentableName().toLowerCase().equals("androidmanifest.xml"))) {
            String androidManifest = document.getCharsSequence().toString();
            XmlFile psiFile = (XmlFile) PsiFileFactory.getInstance(project).createFileFromText("androidManifest", StdFileTypes.XML, androidManifest);
            XmlDocument xmlDocument = psiFile.getDocument();
            if (xmlDocument != null && xmlDocument.getRootTag() != null) {
                XmlTag rootTag = xmlDocument.getRootTag();
                XmlAttribute aPackage = rootTag.getAttribute("package");
                if (aPackage != null) {
                    packagepath = aPackage.getValue();
                }
                XmlTag[] subTags = rootTag.getSubTags();
                for (int k = 0; k < subTags.length; k++) {
                    String name = subTags[k].getName();
                    //删除掉theme主题配置
                    if (name.equalsIgnoreCase("application")) {
                        XmlAttribute themeAttribute = subTags[k].getAttribute("android:theme");
                        if (themeAttribute != null) {
                            themeAttribute.delete();
                        }
                        XmlTag[] applicationtag = subTags[k].getSubTags();
                        boolean LauncherScene_isexist = false;
                        for (int m = 0; m < applicationtag.length; m++) {
                            String appsubname = applicationtag[m].getName();

                            if (appsubname.equalsIgnoreCase("activity")) {
                                XmlAttribute attribute = applicationtag[m].getAttribute("android:name");
                                String value = attribute.getValue();
                                if (value.startsWith(".")) {
                                    packagepath = packagepath + value;
                                } else {
                                    packagepath = value;
                                }
                                PsiClass activityClass = JavaPsiFacade.getInstance(project).findClass(packagepath, GlobalSearchScope.allScope(project));
                                boolean result = ClassUtils.changeSuperClass(activityClass);

                                if (result) {
                                    XmlTag[] activitysubTags = applicationtag[m].getSubTags();
                                    for (int i = 0; i < activitysubTags.length; i++) {
                                        if (activitysubTags[i].getName().equalsIgnoreCase("intent-filter")) {
                                            activitysubTags[i].delete();
                                        }
                                    }
                                }
                            }
                            if (appsubname.equalsIgnoreCase("meta-data")) {
                                XmlAttribute attribute = applicationtag[m].getAttribute("android:name");
                                if (attribute != null) {
                                    if (attribute.getValue().equalsIgnoreCase("LauncherScene")) {
                                        LauncherScene_isexist = true;
                                        XmlAttribute attributevalue = applicationtag[m].getAttribute("android:value");
                                        attributevalue.setValue(scenePath);
                                    }
                                }
                            }
                        }
                        if (!LauncherScene_isexist) {
                            XmlTag metadataTag = subTags[k].createChildTag("meta-data", null, null, false);
                            if (metadataTag != null) {
                                metadataTag.setAttribute("android:name", "LauncherScene");
                                metadataTag.setAttribute("android:value", scenePath);
                                subTags[k].addSubTag(metadataTag, true);
                            }
                        }
                        CodeStyleManager.getInstance(psiFile.getProject()).reformat(psiFile);
                        break;
                    }
                }
            }
            Runnable writeAction = new WriteAction(xmlDocument.getText(), document);
            ApplicationManager.getApplication().runWriteAction(writeAction);
        }
    }

}