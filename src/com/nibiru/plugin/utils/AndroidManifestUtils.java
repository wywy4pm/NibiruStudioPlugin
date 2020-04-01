package com.nibiru.plugin.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

public class AndroidManifestUtils {

    public static boolean ishasLauncherScene(Project project, VirtualFile folder) {
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
                                                    return parseMenifestxml(project, file);
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
        return false;
    }

    private static Boolean parseMenifestxml(Project project, VirtualFile childFile) {
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
                        for (int m = 0; m < applicationtag.length; m++) {
                            String appsubname = applicationtag[m].getName();
                            if (appsubname.equalsIgnoreCase("meta-data")) {
                                XmlAttribute attribute = applicationtag[m].getAttribute("android:name");
                                if (attribute != null) {
                                    if (attribute.getValue().equalsIgnoreCase("LauncherScene")) {
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }
}
