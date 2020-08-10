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
import org.apache.http.util.TextUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            }
            return;
        }
        Runtime rt = Runtime.getRuntime();
        try {
            String file_path = current_file.getPath();
            if (current_file.getPath().matches(".*?\\.java$")) {
                //这里需要获取到java文件中nss的路径
                Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
                if (editor != null) {
                    PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
                    String layoutName = getLayoutName(editor, file);
                    if (layoutName != null) {
                        String modulePath = ModuleUtils.getCurModulePath(project, current_file);
                        file_path = modulePath + "/src/main/Assets/layout/" + layoutName;
                    }
                } else {
//                    Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", ".nss file Error!", NotificationType.INFORMATION));
                    return;
                }
            } else {
                if (current_file == null || !current_file.getPath().toString().matches(".*?\\.nss$")) {
                    Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", "This is not .nss file.", NotificationType.INFORMATION));
                    return;
                }
            }

            int index = file_path.indexOf("/Assets/layout/");

            if (index < 0) {
                index = file_path.indexOf("/assets/layout/");
            }
            if (index > 0) {
                if (NibiruConfig.isLogin) {
                    String userName = PropertiesUtils.getString(PropertiesUtils.LOGIN_NAME);
                    String password = PropertiesUtils.getString(PropertiesUtils.LOGIN_PAASWORD);
                    //exe路径,main路径,.nss文件路径,插件版本号,用户名,密码,机器码
                    String[] cmd = {exepath, file_path.substring(0, index), file_path, FileUtils.getPluginVersion(), "-u", userName, "-p", password, "-m", NibiruUtils.createDeviceID()};
                    rt.exec(cmd);
                } else {
                    String[] cmd = {exepath, file_path.substring(0, index), file_path, FileUtils.getPluginVersion()};
                    rt.exec(cmd);
                }
            } else {
                Notifications.Bus.notify(new Notification("Nibiru Studio", "Information", "The NSS file is not in the assets/layout directory!", NotificationType.INFORMATION));
                return;
            }
        } catch (
                IOException e1) {
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
    public static void createBinFile(LoginBean loginBean, Project project, VirtualFile virtualFile, boolean isShowDialog) {
        int uid = loginBean.getAccount().getId();
        String pagename = GradleUtils.getBuildpagename(project, virtualFile);

        if (TextUtils.isEmpty(pagename)) {
            Log.i("获取包名失败!");
            return;
        }

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
        if (isShowDialog) {
            Messages.showMessageDialog("Module " + virtualFile.getName() + " has updated Nibiru Studio App License successfully.", StringConstants.TITLE_NO_NA_TIP, UiUtils.getCompleteIcon());
        }
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
                            if (childFile.isDirectory()) {
                                if ("Lib".equals(childFile.getName())) {
                                    VirtualFile[] libs = childFile.getChildren();
                                    if (libs.length > 0) {
                                        for (VirtualFile libFile : libs) {
                                            if (!StringUtils.isBlank(libFile.getName())) {
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
            return fileName;
        }
        return "";
    }

    public static String getAarFileName(VirtualFile aarFile) {
        if (aarFile != null) {
            String fileName = aarFile.getName();
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
        List<VirtualFile> aarpathlist = new ArrayList<VirtualFile>();
        if (sdkFile != null) {
            VirtualFile[] sdkChildFiles = sdkFile.getChildren();
            if (sdkChildFiles.length > 0) {
                for (VirtualFile childFile : sdkChildFiles) {
                    if (!StringUtils.isBlank(childFile.getName())) {
                        if (childFile.isDirectory()) {
                            if ("Lib".equals(childFile.getName())) {
                                VirtualFile[] libs = childFile.getChildren();
                                if (libs.length > 0) {
                                    for (VirtualFile libFile : libs) {
                                        if (!StringUtils.isBlank(libFile.getName())) {
                                            if (libFile.getName().startsWith("nibiru_studio") && libFile.getName().endsWith(".aar")) {
                                                aarpathlist.add(libFile);
//                                                return libFile;
                                            }
                                        }
                                    }
                                    if (aarpathlist.size() > 0) {
                                        if (aarpathlist.size() == 1) {
                                            return aarpathlist.get(0);
                                        } else {
                                            Collections.sort(aarpathlist, new Comparator<VirtualFile>() {
                                                @Override
                                                public int compare(VirtualFile v1, VirtualFile v2) {
                                                    String o1 = v1.getName();
                                                    String o2 = v2.getName();
                                                    int o1index = o1.lastIndexOf(".aar");
                                                    int o1index_ = o1.lastIndexOf("pro_");
                                                    String o1version = "";
                                                    if (o1index != -1 && o1index_ != -1) {
                                                        o1version = o1.substring(o1index_ + 4, o1index);
                                                    }
                                                    int o2index = o2.lastIndexOf(".aar");
                                                    int o2index_ = o2.lastIndexOf("pro_");
                                                    String o2version = "";
                                                    if (o2index != -1 && o2index_ != -1) {
                                                        o2version = o2.substring(o2index_ + 4, o2index);
                                                    }
                                                    String[] o1Split = o1version.split("_");
                                                    String[] o2Split = o2version.split("_");
                                                    int cha = 0;
                                                    if (o1Split.length == o2Split.length) {
                                                        for (int i = 0; i < o1Split.length; i++) {
                                                            cha = Integer.parseInt(o2Split[i]) - Integer.parseInt(o1Split[i]);
                                                            if (cha != 0) {
                                                                break;
                                                            }
                                                        }
                                                        return cha;
                                                    }
                                                    return 0;
                                                }
                                            });
                                            return aarpathlist.get(0);
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
        List<String> exepathlist = new ArrayList<String>();
        if (sdkFile != null) {
            VirtualFile[] sdkChildFiles = sdkFile.getChildren();
            if (sdkChildFiles.length > 0) {
                for (VirtualFile childFile : sdkChildFiles) {
                    if (!StringUtils.isBlank(childFile.getName())) {
                        if (childFile.isDirectory()) {
                            if ("Editor".equals(childFile.getName())) {
                                VirtualFile[] edits = childFile.getChildren();
                                if (edits.length > 0) {
                                    for (VirtualFile editFile : edits) {
                                        if (!StringUtils.isBlank(editFile.getName())) {
                                            if (editFile.getName().startsWith("Nibiru Studio") && editFile.getName().endsWith(".exe")) {
                                                exepathlist.add(editFile.getPath());
                                            }
                                        }
                                    }
                                    if (exepathlist.size() > 0) {
                                        if (exepathlist.size() == 1) {
                                            return exepathlist.get(0);
                                        } else {
                                            Collections.sort(exepathlist, new Comparator<String>() {
                                                @Override
                                                public int compare(String o1, String o2) {
                                                    int o1index = o1.lastIndexOf(".exe");
                                                    int o1index_ = o1.lastIndexOf("_");
                                                    String o1version = "";
                                                    if (o1index != -1 && o1index_ != -1) {
                                                        o1version = o1.substring(o1index_ + 1, o1index);
                                                    }
                                                    int o2index = o2.lastIndexOf(".exe");
                                                    int o2index_ = o2.lastIndexOf("_");
                                                    String o2version = "";
                                                    if (o2index != -1 && o2index_ != -1) {
                                                        o2version = o2.substring(o2index_ + 1, o2index);
                                                    }

                                                    String[] o1Split = o1version.split("\\.");
                                                    String[] o2Split = o2version.split("\\.");
                                                    int cha = 0;
                                                    if (o1Split.length == o2Split.length) {
                                                        for (int i = 0; i < o1Split.length; i++) {
                                                            cha = Integer.parseInt(o2Split[i]) - Integer.parseInt(o1Split[i]);
                                                            if (cha != 0) {
                                                                break;
                                                            }
                                                        }
                                                        return cha;
                                                    }
                                                    return 0;
                                                }
                                            });
                                            return exepathlist.get(0);
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
            return moduleFolder.getPath() + File.separator + "libs";
        }
        return null;
    }

    public static boolean isAddModuleLib(VirtualFile packageFolder) {
        if (packageFolder != null) {
            if (packageFolder.getPath().contains("/src/main/java")) {
                int index = packageFolder.getPath().indexOf("/src/main/java");
                if (index > -1) {
                    String modulePath = packageFolder.getPath().substring(0, index);
                    String libsPath = modulePath + File.separator + "libs";
                    VirtualFile libsFile = VirtualFileManager.getInstance().findFileByUrl("file://" + libsPath);
                    if (libsFile != null) {
                        for (VirtualFile libFile : libsFile.getChildren()) {
                            if (!StringUtils.isBlank(libFile.getName())) {
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

    public static String getModulePath(String moduleImlPath) {
        if (!StringUtils.isBlank(moduleImlPath)) {
            if (moduleImlPath.contains("/.idea/modules")) {
                moduleImlPath = moduleImlPath.replace("/.idea/modules", "");
            }
            if (moduleImlPath.endsWith(".iml")) {
                int lastIndex = moduleImlPath.lastIndexOf("/");
                return moduleImlPath.substring(0, lastIndex);
            }
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

    public static String getEditorExeVersion() {
        String location = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{01CEE08C-C171-4D18-B3B9-B0CB280836EB}_is1";
        String key = "DisplayIcon";
        String exePath = NibiruUtils.readRegistry(location, key);
        String versionKey = "DisplayVersion";
        String versionK = NibiruUtils.readRegistry(location, versionKey);
        VirtualFile app = VirtualFileManager.getInstance().findFileByUrl("file://" + exePath);
        if (app != null) {
            return versionK;
        }
        return null;
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
        }
        return sdkPath;
    }

    public static String getPluginVersion() {
        if (PluginManager.getPlugins().length > 0) {
            PluginId pluginId = PluginId.getId(StringConstants.NIBIRU_STUDIO_ID);
            IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            if (pluginDescriptor != null) {
                return pluginDescriptor.getVersion();
            }
        }
        return "";
    }

    /**
     * 更新sdk
     *
     * @param sdkPath
     */
    public static boolean updateExe(String sdkPath) {
        String editorExeVersion = getEditorExeVersion();
        if (!TextUtils.isEmpty(editorExeVersion)) {
            String exePath = getExePath(LocalFileSystem.getInstance().findFileByPath(sdkPath));
            int index = exePath.lastIndexOf(".exe");
            int index_ = exePath.lastIndexOf("_");
            if (index != -1 && index_ != -1) {
                String version = exePath.substring(index_ + 1, index);
                String[] installedSplit = editorExeVersion.split("\\.");
                String[] versionSplit = version.split("\\.");
                if (installedSplit.length == versionSplit.length) {
                    for (int i = 0; i < installedSplit.length; i++) {
                        if (Integer.parseInt(versionSplit[i]) - Integer.parseInt(installedSplit[i]) > 0) {
                            int okCancel = Messages.showOkCancelDialog(StringConstants.TIP_TO_UPDATE_EXE, StringConstants.TITLE_SDK_SETTING, StringConstants.UPDATEEDITOR, StringConstants.CANCEL, UiUtils.getCompleteIcon());
                            if (okCancel == 0) {
                                installExe(exePath);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
