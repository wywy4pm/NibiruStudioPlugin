package com.nibiru.plugin.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang.StringUtils;

import java.io.*;

public class FileUtils {
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255) {
            return false;
        } else {
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
    }

    public static boolean isValidJavaName(String fileName) {
        if (!StringUtils.isBlank(fileName)) {
            return fileName.matches("^[a-zA-Z][a-zA-Z0-9_]*");
        }
        return false;
    }

    public static boolean isValidAar(String filePath) {
        if (!StringUtils.isBlank(filePath)) {
            String fileName = getFileName(filePath);
            Log.i("isValidAar fileName = " + fileName);
            if (!StringUtils.isBlank(fileName) && fileName.toLowerCase().contains("nibiru_studio") && fileName.toLowerCase().endsWith("aar")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidSdkFolder(String filePath) {
        boolean isValidAar = false;
        boolean isValidEditor = false;
        if (!StringUtils.isBlank(filePath)) {
            VirtualFile sdkFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (sdkFile != null) {
                VirtualFile[] sdkChildFiles = sdkFile.getChildren();
                if (sdkChildFiles.length > 0) {
                    for (VirtualFile childFile : sdkChildFiles) {
                        if (!StringUtils.isBlank(childFile.getName())) {
                            Log.i("isValidSdkFolder fileName = " + childFile.getName());
                            if (childFile.isDirectory()) {
                                if ("Lib".equals(childFile.getName())) {
                                    VirtualFile[] libs = childFile.getChildren();
                                    if (libs.length > 0) {
                                        for (VirtualFile libFile : libs) {
                                            if (!StringUtils.isBlank(libFile.getName())) {
                                                Log.i("isValidSdkFolder libName = " + libFile.getName());
                                                if (libFile.getName().startsWith("nibiru_studio")
                                                        && libFile.getName().endsWith(".aar")) {
                                                    isValidAar = true;
                                                }
                                            }
                                        }
                                    }
                                } else if ("Editor".equals(childFile.getName())) {
                                    VirtualFile[] edits = childFile.getChildren();
                                    if (edits.length > 0) {
                                        for (VirtualFile editFile : edits) {
                                            if (!StringUtils.isBlank(editFile.getName())) {
                                                Log.i("isValidSdkFolder editName = " + editFile.getName());
                                                if (editFile.getName().startsWith("Nibiru Studio")
                                                        && editFile.getName().endsWith(".exe")) {
                                                    isValidEditor = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isValidAar && isValidEditor;
    }

    public static String getFileName(String filePath) {
        if (!StringUtils.isBlank(filePath)) {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Log.i("isValidAar fileName = " + fileName);
            return fileName;
        }
        return "";
    }

    public static String getAarName(String aarFileName) {
        if (!StringUtils.isBlank(aarFileName)) {
            return aarFileName.replace(".aar", "");
        }
        return "";
    }

    public static VirtualFile getAarFile(VirtualFile sdkFile) {
        if (sdkFile != null) {
            VirtualFile[] sdkChildFiles = sdkFile.getChildren();
            if (sdkChildFiles.length > 0) {
                for (VirtualFile childFile : sdkChildFiles) {
                    if (!StringUtils.isBlank(childFile.getName())) {
                        Log.i("isValidSdkFolder fileName = " + childFile.getName());
                        if (childFile.isDirectory()) {
                            if ("Lib".equals(childFile.getName())) {
                                VirtualFile[] libs = childFile.getChildren();
                                if (libs.length > 0) {
                                    for (VirtualFile libFile : libs) {
                                        if (!StringUtils.isBlank(libFile.getName())) {
                                            Log.i("isValidSdkFolder libName = " + libFile.getName());
                                            if (libFile.getName().startsWith("nibiru_studio")
                                                    && libFile.getName().endsWith(".aar")) {
                                                return libFile;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void copyFile(Project project, VirtualFile source, String destFolder, String destFileName) {
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                if (!StringUtils.isBlank(destFolder)) {
                    InputStream input = null;
                    OutputStream output = null;
                    try {
                        File destFile = new File(destFolder + File.separator + destFileName);
                        if (!destFile.exists()) {
                            destFile.getParentFile().mkdirs();
                            input = source.getInputStream();
                            output = new FileOutputStream(destFile);
                            byte[] buf = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = input.read(buf)) > 0) {
                                output.write(buf, 0, bytesRead);
                            }
                            input.close();
                            output.close();

                            VirtualFileManager.getInstance().syncRefresh();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static String getModuleLibsFolder(VirtualFile moduleFolder) {
        if (moduleFolder != null) {
            Log.i("getModuleLibsFolder moduleFolder = " + moduleFolder.getPath());
            return moduleFolder.getPath() + File.separator + "libs";
        }
        return null;
    }

    public static boolean isAddModuleLib(VirtualFile packageFolder) {
        if (packageFolder != null) {
            Log.i("isAddModuleLib packageFolder = " + packageFolder.getPath());
            if (packageFolder.getPath().contains("/src/main/java")) {
                int index = packageFolder.getPath().indexOf("/src/main/java");
                if (index > -1) {
                    String modulePath = packageFolder.getPath().substring(0, index);
                    Log.i("isAddModuleLib modulePath = " + modulePath);
                    String libsPath = modulePath + File.separator + "libs";
                    VirtualFile libsFile = VirtualFileManager.getInstance().findFileByUrl("file://" + libsPath);
                    if (libsFile != null) {
                        for (VirtualFile libFile : libsFile.getChildren()) {
                            if (!StringUtils.isBlank(libFile.getName())) {
                                Log.i("isAddModuleLib libName = " + libFile.getName());
                                if (libFile.getName().startsWith("nibiru_studio")
                                        && libFile.getName().endsWith(".aar")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getAppLibsFolder(Project project, VirtualFile folder) {
        VirtualFile baseFile = project.getBaseDir();
        VirtualFile[] childFiles = baseFile.getChildren();
        if (childFiles.length > 0) {
            for (VirtualFile childFile : childFiles) {
                String path = childFile.getPath();
                if (folder != null && folder.getPath().contains(path)) {
                    Log.i("getAppLibsFolder path = " + path);
                    for (VirtualFile virtualFile : (childFile.getChildren())) {
                        String name = virtualFile.getName();
                        Log.i("getAppLibsFolder name = " + name);
                        if (!StringUtils.isBlank(name) && name.equalsIgnoreCase("libs")) {
                            return virtualFile.getPath();
                        } else {
                            return childFile.getPath() + File.separator + "libs";
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isAddLib(Project project, VirtualFile folder) {
        VirtualFile baseFile = project.getBaseDir();
        VirtualFile[] childFiles = baseFile.getChildren();
        if (childFiles.length > 0) {
            for (VirtualFile childFile : childFiles) {
                String path = childFile.getPath();
                if (folder != null && folder.getPath().contains(path)) {
                    Log.i("isAddLib path = " + path);
                    for (VirtualFile virtualFile : (childFile.getChildren())) {
                        String name = virtualFile.getName();
                        Log.i("isAddLib name = " + name);
                        if (!StringUtils.isBlank(name) && name.equalsIgnoreCase("libs")) {
                            for (VirtualFile libFile : virtualFile.getChildren()) {
                                if (!StringUtils.isBlank(libFile.getName())) {
                                    Log.i("isAddLib libName = " + libFile.getName());
                                    if (libFile.getName().startsWith("nibiru_studio")
                                            && libFile.getName().endsWith(".aar")) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getModulePath(String moduleImlPath) {
        if (!StringUtils.isBlank(moduleImlPath) && moduleImlPath.endsWith(".iml")) {
            int lastIndex = moduleImlPath.lastIndexOf("/");
            return moduleImlPath.substring(0, lastIndex);
        }
        return "";
    }
}
