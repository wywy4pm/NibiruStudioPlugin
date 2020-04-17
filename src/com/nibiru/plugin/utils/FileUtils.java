package com.nibiru.plugin.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.NibiruDESUtil;
import com.nibiru.plugin.ui.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;

public class FileUtils {
    /**
     * 打开nss文件的逻辑
     *
     * @param project
     * @param current_file
     */
    public static void openNssFile(AnActionEvent anActionEvent, Project project, VirtualFile current_file) {
        String location = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{01CEE08C-C171-4D18-B3B9-B0CB280836EB}_is1";
        String key = "DisplayIcon";
        String exepath = NibiruUtils.readRegistry(location, key);
        VirtualFile app = VirtualFileManager.getInstance().findFileByUrl("file://" + exepath);
        if (app == null) {
            if (!NibiruConfig.isLogin) {
                LoginDialog loginDialog = new LoginDialog(anActionEvent, anActionEvent.getProject(), current_file);
                loginDialog.show();
            } else if (!NibiruConfig.deviceIsActivate) {
                ActivateDialog activateDialog = new ActivateDialog(anActionEvent, anActionEvent.getProject(), current_file);
                activateDialog.show();
            } else {
                SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent, project, current_file);
                sdkSettingDialog.show();
//                SdkModifyDialog sdkModifyDialog = new SdkModifyDialog(anActionEvent, project, current_file);
//                sdkModifyDialog.show();
            }
            return;
        }
        Runtime rt = Runtime.getRuntime();
        try {
            String file_path = current_file.getPath();
            if (current_file.getPath().matches(".*?\\.java$")) {
                //这里需要获取到java文件中nss的路径
//                Object nav = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
//                if (nav==null){
                Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
                if (editor != null) {
                    PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
                    String layoutName = getLayoutName(editor, file);
                    if (layoutName != null) {
                        String modulePath = ModuleUtils.getCurModulePath(project, current_file);
                        file_path = modulePath + "/src/main/Assets/layout/" + layoutName;
                    }
                } else {
                    Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", ".nss file Error!", NotificationType.INFORMATION));
                    return;
                }
            }
//                if (nav instanceof PsiClass) {
//                    PsiClass pis= ((PsiClass) nav);
//                    PsiMethod[] methods = pis.getMethods();
//                    if (methods != null && methods.length > 0) {
//                        for (int i = 0; i < methods.length; i++) {
//                            String name = methods[i].getName();
//                            if (name.equals("onCreate")) {
//                                PsiCodeBlock body = methods[i].getBody();
//                                String text = body.getText();
//                                int index = text.indexOf("layout/");
//                                int end = text.indexOf(".nss");
//                                if (index>0){
//                                    String substring = text.substring(index, end+".nss".length());
//                                    String modulePath = ModuleUtils.getCurModulePath(project, current_file);
//                                    file_path=modulePath+"/src/main/Assets/"+substring;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            int index = file_path.indexOf("/Assets/layout/");
            if (index > 0) {
                String[] cmd = {exepath, file_path.substring(0, index), file_path};
                rt.exec(cmd);
            } else {
                Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", ".nss file Error!", NotificationType.INFORMATION));
                return;
            }
        } catch (IOException e1) {
            Notifications.Bus.notify(new Notification("Nibiru Studio", "Error", e1.getMessage(), NotificationType.ERROR));
        }
    }

    public static String getLayoutName(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);
        String layout = findLayoutResourcenew(candidateA);
        if (layout != null) {
            return layout;
        }
        return findLayoutResourcenew(candidateB);
    }

    public static String findLayoutResourcenew(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement layout = element.getParent().getFirstChild();
        if (layout == null) {
            return null;
        }
        if (!layout.getText().contains(".nss")) {
            return null;
        }
        Project project = element.getProject();
        String text = element.getText();
        String replace = text.replace("\"", "");
        String[] split = replace.split("/");
        return split[split.length - 1];
    }


    public static PsiFile getLayoutFileFromCaret(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);

        PsiFile layout = findLayoutResource(candidateA);
        if (layout != null) {
            return layout;
        }
        return findLayoutResource(candidateB);
    }

    public static PsiFile findLayoutResource(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement layout = element.getParent().getFirstChild();
        if (layout == null) {
            return null;
        }
        if (!layout.getText().contains(".nss")) {
            return null;
        }
        Project project = element.getProject();
        String text = element.getText();
        String replace = text.replace("\"", "");
        String[] split = replace.split("/");
        return resolveLayoutResourceFile(element, project, split[split.length - 1]);
    }

    private static PsiFile resolveLayoutResourceFile(PsiElement element, Project project, String name) {
        Module module = ModuleUtil.findModuleForPsiElement(element);
        PsiFile[] files = null;
        if (module != null) {
            GlobalSearchScope moduleScope = module.getModuleWithDependenciesScope();
            files = FilenameIndex.getFilesByName(project, name, moduleScope);
            if (files == null || files.length <= 0) {
                moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
                files = FilenameIndex.getFilesByName(project, name, moduleScope);
            }
        }
        if (files == null || files.length <= 0) {
            files = FilenameIndex.getFilesByName(project, name, new EverythingGlobalScope(project));
            if (files.length <= 0) {
                return null;
            }
        }
        return files[0];
    }

    protected static PsiClass getTargetClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return null;
        } else {
            PsiClass target = (PsiClass) PsiTreeUtil.getParentOfType(element, PsiClass.class);
            return target instanceof SyntheticElement ? null : target;
        }
    }

    //创建assets下面的bin文件
    public static void createBinFile(LoginBean loginBean, Project project, VirtualFile virtualFile) {
        int uid = loginBean.getAccount().getId();
        String pagename = GradleUtils.getBuildpagename(project, virtualFile);
        String encryptStr = NibiruDESUtil.encryptStr("Nibiru," + pagename + "," + uid, pagename);
        NibiruConfig.appkey = NibiruDESUtil.encryptStr("Nibiru", pagename);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                createFileInAssets(project, virtualFile, encryptStr);
                ModifyAndroidManifest modifyAndroidManifest = new ModifyAndroidManifest(project, virtualFile, null);
                modifyAndroidManifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.APP_KEY);
            }
        });
        Messages.showMessageDialog("Module " + virtualFile.getName() + " has updated Nibiru Studio App License successfully.", StringConstants.TITLE_NO_NA_TIP, UiUtils.getCompleteIcon());
    }

    /**
     * 在assets下面创建NibiruSDKKey.bin文件
     *
     * @param project
     * @param folder
     * @param content
     */
    public static void createFileInAssets(Project project, VirtualFile folder, String content) {
        String curModulePath = ModuleUtils.getCurModulePath(project, folder);
        VirtualFile modulefile = LocalFileSystem.getInstance().findFileByPath(curModulePath);
        if (modulefile != null && modulefile.exists()) {
            VirtualFile[] children = modulefile.getChildren();
            getOutputPath(children, project, folder, content);
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
                                        if (binfile != null) {
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

//    public static String getAppLibsFolder(Project project, VirtualFile folder) {
////        VirtualFile baseFile = project.getBaseDir();
////        VirtualFile[] childFiles = baseFile.getChildren();
////        if (childFiles.length > 0) {
////            for (VirtualFile childFile : childFiles) {
////                String path = childFile.getPath();
////                if (folder != null && folder.getPath().contains(path)) {
////                    Log.i("getAppLibsFolder path = " + path);
////                    for (VirtualFile virtualFile : (childFile.getChildren())) {
////                        String name = virtualFile.getName();
////                        Log.i("getAppLibsFolder name = " + name);
////                        if (!StringUtils.isBlank(name) && name.equalsIgnoreCase("libs")) {
////                            return virtualFile.getPath();
////                        } else {
////                            return childFile.getPath() + File.separator + "libs";
////                        }
////                    }
////                }
////            }
////        }
////        return null;
////    }

//    public static boolean isAddLib(Project project, VirtualFile folder) {
//        VirtualFile baseFile = project.getBaseDir();
//        VirtualFile[] childFiles = baseFile.getChildren();
//        if (childFiles.length > 0) {
//            for (VirtualFile childFile : childFiles) {
//                String path = childFile.getPath();
//                if (folder != null && folder.getPath().contains(path)) {
//                    Log.i("isAddLib path = " + path);
//                    for (VirtualFile virtualFile : (childFile.getChildren())) {
//                        String name = virtualFile.getName();
//                        Log.i("isAddLib name = " + name);
//                        if (!StringUtils.isBlank(name) && name.equalsIgnoreCase("libs")) {
//                            for (VirtualFile libFile : virtualFile.getChildren()) {
//                                if (!StringUtils.isBlank(libFile.getName())) {
//                                    Log.i("isAddLib libName = " + libFile.getName());
//                                    if (libFile.getName().startsWith("nibiru_studio")
//                                            && libFile.getName().endsWith(".aar")) {
//                                        return true;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }

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

    public static String getPluginVersion() {
        if (PluginManager.getPlugins().length > 0) {
            PluginId pluginId = PluginId.getId(StringConstants.NIBIRU_STUDIO_ID);
            IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            if (pluginDescriptor != null) {
                Log.i("getPlugin name = " + pluginDescriptor.getName() + " version = " + pluginDescriptor.getVersion());
                return pluginDescriptor.getVersion();
            }
        }
        return "";
    }
}
