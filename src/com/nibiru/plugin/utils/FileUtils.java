package com.nibiru.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.NibiruDESUtil;
import com.nibiru.plugin.ui.SdkSettingDialog;
import com.nibiru.plugin.ui.Toast;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.*;

public class FileUtils {
    /**
     * 打开nss文件的逻辑
     * @param project
     * @param current_file
     */
    public static void openNssFile(Project project,VirtualFile current_file) {
        String location="HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{01CEE08C-C171-4D18-B3B9-B0CB280836EB}_is1";
        String key="DisplayIcon";
        String exepath = NibiruUtils.readRegistry(location, key);

        VirtualFile app = VirtualFileManager.getInstance().findFileByUrl("file://" + exepath);
        if (app == null) {
            SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(project, current_file);
            sdkSettingDialog.show();
            return;
        }
        if (current_file == null || !current_file.getPath().toString().matches(".*?\\.nss$")) {
            Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", "This is not .nss file.", NotificationType.INFORMATION));
            return;
        }
        Runtime rt = Runtime.getRuntime();
        try {
            String file_path = current_file.getPath();
            int index = file_path.indexOf("/Assets/layout/");
            if (index>0){
                String[] cmd = {exepath, file_path.substring(0,index),file_path};
                rt.exec(cmd);
            }else{
                Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", ".nss file Error!", NotificationType.INFORMATION));
                return;
            }
        } catch (IOException e1) {
            Notifications.Bus.notify(new Notification("Nibiru Studio", "Error", e1.getMessage(), NotificationType.ERROR));
        }
    }


    //创建assets下面的bin文件
    public static void createBinFile(LoginBean loginBean, Project project, VirtualFile virtualFile) {
        int uid = loginBean.getAccount().getId();
        String pagename = GradleUtils.getBuildpagename(project, virtualFile);
        String encryptStr = NibiruDESUtil.encryptStr("Nibiru," + pagename + "," + uid,pagename);
        NibiruConfig.appkey = NibiruDESUtil.encryptStr("Nibiru",pagename);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                createFileInAssets(project, virtualFile, encryptStr);
                ModifyAndroidManifest modifyAndroidManifest=new ModifyAndroidManifest(project,virtualFile,null);
                modifyAndroidManifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.APP_KEY);
                Toast.make(project, MessageType.INFO, StringConstants.REFRESH);
            }
        });
    }

    /**
     * 在assets下面创建NibiruSDKKey.bin文件
     *
     * @param project
     * @param folder
     * @param content
     */
    public static void createFileInAssets(Project project, VirtualFile folder, String content) {
        VirtualFile baseFile = project.getBaseDir();
        VirtualFile[] childFiles = baseFile.getChildren();
        if (childFiles.length > 0) {
            for (VirtualFile childFile : childFiles) {
                String path = childFile.getPath();
                if (folder.getPath().contains(path)) {
                    getOutputPath(childFile.getChildren(), project, folder, content);
                    break;
                }
            }
        }
    }

    private static void getOutputPath(VirtualFile[] virtualFiles, Project project, VirtualFile folder, String content) {
        for (VirtualFile virtualFile : virtualFiles) {
            String name = virtualFile.getName();
            if (virtualFile.isDirectory()) {
                if (name.equals("src")) {
                    VirtualFile[] srcChildren = virtualFile.getChildren();
                    for (VirtualFile srcChild : srcChildren) {
                        String childName = srcChild.getName();
                        if (childName.equals("main")) {
                            VirtualFile[] children = srcChild.getChildren();
                            boolean assetsisExit = false;
                            for (VirtualFile child : children) {
                                if (child.isDirectory()) {
                                    if (child.getName().equalsIgnoreCase("Assets")) {
                                        VirtualFile binfile = child.findChild("NibiruSDKKey.bin");
                                        if (binfile!=null){
                                            try {
                                                binfile.delete(null);
                                                VirtualFileManager.getInstance().syncRefresh();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        assetsisExit = true;
                                        VirtualFile writeableFile = null;
                                        try {
                                            writeableFile = child.createChildData(project, "NibiruSDKKey.bin");
                                            writeableFile.setBinaryContent(content.getBytes());
                                            VirtualFileManager.getInstance().syncRefresh();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                }
                            }
                            if (!assetsisExit) {
                                try {
                                    srcChild.createChildDirectory(project, "Assets");
                                    VirtualFileManager.getInstance().syncRefresh();
                                    createFileInAssets(project, folder, content);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }


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
            Log.i("getFileName fileName = " + fileName);
            return fileName;
        }
        return "";
    }

    public static String getAarFileName(VirtualFile aarFile) {
        if (aarFile != null) {
            String fileName = aarFile.getName();
            Log.i("getAarFileName fileName = " + fileName);
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
                        Log.i("getAarFile fileName = " + childFile.getName());
                        if (childFile.isDirectory()) {
                            if ("Lib".equals(childFile.getName())) {
                                VirtualFile[] libs = childFile.getChildren();
                                if (libs.length > 0) {
                                    for (VirtualFile libFile : libs) {
                                        if (!StringUtils.isBlank(libFile.getName())) {
                                            Log.i("getAarFile libName = " + libFile.getName());
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

    public static String getExePath(VirtualFile sdkFile) {
        if (sdkFile != null) {
            VirtualFile[] sdkChildFiles = sdkFile.getChildren();
            if (sdkChildFiles.length > 0) {
                for (VirtualFile childFile : sdkChildFiles) {
                    if (!StringUtils.isBlank(childFile.getName())) {
                        Log.i("getExePath fileName = " + childFile.getName());
                        if (childFile.isDirectory()) {
                            if ("Editor".equals(childFile.getName())) {
                                VirtualFile[] edits = childFile.getChildren();
                                if (edits.length > 0) {
                                    for (VirtualFile editFile : edits) {
                                        if (!StringUtils.isBlank(editFile.getName())) {
                                            Log.i("isValidSdkFolder editName = " + editFile.getName());
                                            if (editFile.getName().startsWith("Nibiru Studio")
                                                    && editFile.getName().endsWith(".exe")) {
                                                return editFile.getPath();
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

    public static boolean isInstallExe() {
        String location = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{01CEE08C-C171-4D18-B3B9-B0CB280836EB}_is1";
        String key = "DisplayIcon";
        String exePath = NibiruUtils.readRegistry(location, key);
        VirtualFile app = VirtualFileManager.getInstance().findFileByUrl("file://" + exePath);
        if (app != null) {
            return true;
        }
        return false;
    }

    public static void installExe(String exePath) {
        try {
            Desktop.getDesktop().open(new File(exePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSdkPath(Project project, VirtualFile file) {
        String sdkPath = "";
        String modulePath = ModuleUtils.getCurModulePath(project, file);
        if (!StringUtils.isBlank(modulePath)) {
            sdkPath = PropertiesUtils.getString(modulePath);
            Log.i("getSdkPath sdkPath = " + sdkPath);
        }
        return sdkPath;
    }
}
