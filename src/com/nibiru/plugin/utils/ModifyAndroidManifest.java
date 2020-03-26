package com.nibiru.plugin.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
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

    enum ModifyManifestType {
        LauncherScene,
        NIBIRU_PLUGIN_IDS
    }


    public ModifyAndroidManifest(Project project, VirtualFile folder, String scenepath) {
        this.folder = folder;
        this.project = project;
        this.scenePath = scenepath;
    }

    public void modifyManifestXml(ModifyManifestType ModifyManifestType) {
        VirtualFile baseFile = project.getBaseDir();
        VirtualFile[] childFiles = baseFile.getChildren();
        if (childFiles.length > 0) {
            for (VirtualFile childFile : childFiles) {
                String path = childFile.getPath();
                if (folder.getPath().contains(path)) {
                    for (VirtualFile virtualFile : childFile.getChildren()) {
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
                                                    modifyxml(file);
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
                    break;
                }
            }
        }
    }

    private void modifyxml(VirtualFile childFile) {
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
                        if (themeAttribute!=null){
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