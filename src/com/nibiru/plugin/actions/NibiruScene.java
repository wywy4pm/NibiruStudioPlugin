package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.nibiru.plugin.ui.*;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class NibiruScene extends AnAction {
    private VirtualFile folder;
    private Project project;
    private String scenename;
    private Boolean isLauncherScene;
    private Boolean isEditWithNss;
    private String layoutname;
    private String packageName;
    private VirtualFile tempFolder;
    private String tempPagePath;
    private AnActionEvent anActionEvent;

    @Override
    public void actionPerformed(AnActionEvent e) {
        anActionEvent = e;
        folder = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        project = e.getProject();
        CreateSceneDialog dialog = new CreateSceneDialog(project, folder);
        dialog.addCallback(callback);
        dialog.show();
    }

    private void createAssets() {
        String curModulePath = ModuleUtils.getCurModulePath(project, folder);
        VirtualFile modulefile = LocalFileSystem.getInstance().findFileByPath(curModulePath);
        if (modulefile != null && modulefile.exists()) {
            VirtualFile[] children = modulefile.getChildren();
            getOutputPath(children);
        }
    }

    private void getOutputPath(VirtualFile[] virtualFiles) {
        for (VirtualFile virtualFile : virtualFiles) {
            String name = virtualFile.getName();
            VirtualFile[] childVirtualFile = virtualFile.getChildren();
            if (virtualFile.isDirectory()) {
                if (name.equals("main")) {
                    VirtualFile[] children = virtualFile.getChildren();
                    VirtualFile assets = null;
                    VirtualFile assetslayout = null;
                    for (VirtualFile child : children) {
                        if (child.isDirectory()) {
                            if (child.getName().equalsIgnoreCase("Assets")) {
                                //如果存在就获取到对象
                                VirtualFile[] assetschilds = child.getChildren();
                                for (VirtualFile assetschild : assetschilds) {
                                    if (assetschild.isDirectory() && assetschild.getName().equals("layout")) {
                                        assetslayout = assetschild;
                                        break;
                                    }
                                }
                                assets = child;
                                break;
                            }
                        }
                    }
                    if (assets != null) {
                        try {
                            if (assetslayout != null) {
                                VirtualFile binfile = assetslayout.findChild(layoutname + NibiruConfig.LAYOUT_SUFFIX);
                                if (binfile != null) {
                                    try {
                                        binfile.delete(null);
                                        VirtualFileManager.getInstance().syncRefresh();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                assetslayout.createChildData(this, layoutname + NibiruConfig.LAYOUT_SUFFIX);
                                VirtualFileManager.getInstance().syncRefresh();
                                VirtualFile nssfile = assetslayout.findChild(layoutname + NibiruConfig.LAYOUT_SUFFIX);
                                if (isEditWithNss && nssfile != null) {
                                    FileUtils.openNssFile(anActionEvent, project, nssfile);
                                }
                            } else {
                                assets.createChildDirectory(this, "layout");
                                VirtualFileManager.getInstance().syncRefresh();
                                createAssets();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            virtualFile.createChildDirectory(this, "Assets");
                            VirtualFileManager.getInstance().syncRefresh();
                            createAssets();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                } else if (childVirtualFile.length > 0) {
                    getOutputPath(childVirtualFile);
                }
            }
        }
    }


    private CreateSceneDialog.Callback callback = new CreateSceneDialog.Callback() {
        @Override
        public void showDialogResult(String sceneName, String layoutName, boolean isLauncherScene, boolean isEditWithNss) {
            NibiruScene.this.scenename = sceneName;
            NibiruScene.this.layoutname = layoutName;
            NibiruScene.this.isLauncherScene = isLauncherScene;
            NibiruScene.this.isEditWithNss = isEditWithNss;
            String curModulePath = ModuleUtils.getCurModulePath(project, folder);
            VirtualFile nssfile = LocalFileSystem.getInstance().findFileByPath(curModulePath+"/src/main/Assets/layout/"+layoutName+NibiruConfig.LAYOUT_SUFFIX);
            SameLayoutNameDialog sameLayoutNameDialog = null;
            if (nssfile != null) {
                sameLayoutNameDialog = new SameLayoutNameDialog();
                sameLayoutNameDialog.show();
            }
            if (sameLayoutNameDialog != null && !sameLayoutNameDialog.isOk()) {
                return;
            }
            ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(runnable));

            if (StringUtils.isBlank(FileUtils.getSdkPath(project, folder)) && !FileUtils.isAddModuleLib(folder)) {
                if (!NibiruConfig.isLogin) {
                    LoginDialog loginDialog = new LoginDialog(anActionEvent, project, folder);
                    loginDialog.show();
                } else if (!NibiruConfig.deviceIsActivate) {
                    ActivateDialog activateDialog = new ActivateDialog(anActionEvent, project, folder);
                    activateDialog.show();
                } else {
                    SdkSettingDialog sdkSettingDialog = new SdkSettingDialog(anActionEvent, project, folder);
                    sdkSettingDialog.show();
//                    SdkModifyDialog sdkModifyDialog = new SdkModifyDialog(anActionEvent, project, folder);
//                    sdkModifyDialog.show();
                }
            }
        }
    };

    protected Runnable getRunnableWrapper(final Runnable runnable) {
        return new Runnable() {
            @Override
            public void run() {
                if (project == null)
                    return;
                CommandProcessor.getInstance().executeCommand(project, runnable, " delete " + scenename + NibiruConfig.SUFFIX, ActionGroup.EMPTY_GROUP);//cut 是 undo 的描述 我应该填写类名
            }
        };
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (folder == null) {
                return;
            }
            try {
                createAssets();
                createpageDir(tempPagePath);
                if (tempFolder != null) {
                    VirtualFile binfile = tempFolder.findChild(scenename + NibiruConfig.SUFFIX);
                    if (binfile != null) {
                        try {
                            binfile.delete(null);
                            VirtualFileManager.getInstance().syncRefresh();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    VirtualFile writeableFile = tempFolder.createChildData(this, scenename + NibiruConfig.SUFFIX);
                    writeableFile.setBinaryContent(getBinaryContent(packageName, scenename, layoutname));
                    FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, writeableFile), true);
                    tempFolder = null;
                } else {
                    VirtualFile binfile = folder.findChild(scenename + NibiruConfig.SUFFIX);
                    if (binfile != null) {
                        try {
                            binfile.delete(null);
                            VirtualFileManager.getInstance().syncRefresh();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    VirtualFile writeableFile = folder.createChildData(this, scenename + NibiruConfig.SUFFIX);
                    writeableFile.setBinaryContent(getBinaryContent(packageName, scenename, layoutname));
                    FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, writeableFile), true);
                }
                if (NibiruScene.this.isLauncherScene) {
                    if (StringUtils.isEmpty(packageName)) {
                        ModifyAndroidManifest manifest = new ModifyAndroidManifest(project, folder, scenename);
                        manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.LauncherScene);
                    } else {
                        ModifyAndroidManifest manifest = new ModifyAndroidManifest(project, folder, packageName + "." + scenename);
                        manifest.modifyManifestXml(ModifyAndroidManifest.ModifyManifestType.LauncherScene);
                    }
                }
                VirtualFileManager.getInstance().syncRefresh();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    public byte[] getBinaryContent(String packageString, String name, String layoutname) {
        String result = null;
        result = createSceneClass(packageString, name, layoutname);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.getBytes();
    }

    private String createSceneClass(String packageString, String className, String layoutname) {
        if (packageString == null || packageString.isEmpty()) {
            return "import x.core.ui.XBaseScene;\n" +
                    "import x.core.ui.XUI;\n" +
                    "\n" +
                    "public class " + className + " extends XBaseScene implements XUI.LoadContentLayoutListener{\n" +
                    "    @Override\n" +
                    "    public void onCreate() {\n" +
                    "        setContentLayout(\"" + "layout/" + layoutname + "" + NibiruConfig.LAYOUT_SUFFIX + "\", XUI.Location.ASSETS,this);\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void onResume() {\n" +
                    "       \n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void onPause() {\n" +
                    "      \n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void onDestroy() {\n" +
                    "\n" +
                    "    }\n" + "    @Override\n" +
                    "    public void onLoadCompleted() {\n" +
                    "\n" +
                    "    }" +
                    "   \n" +
                    "}\n";
        } else {
            return "package " + packageString + ";\n" +
                    "\n" +
                    "import x.core.ui.XBaseScene;\n" +
                    "import x.core.ui.XUI;\n" +
                    "\n" +
                    "public class " + className + " extends XBaseScene implements XUI.LoadContentLayoutListener{\n" +
                    "    @Override\n" +
                    "    public void onCreate() {\n" +
                    "        setContentLayout(\"" + "layout/" + layoutname + "" + NibiruConfig.LAYOUT_SUFFIX + "\", XUI.Location.ASSETS,this);\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void onResume() {\n" +
                    "       \n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void onPause() {\n" +
                    "      \n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void onDestroy() {\n" +
                    "\n" +
                    "    }\n" +
                    "   \n" + "    @Override\n" +
                    "    public void onLoadCompleted() {\n" +
                    "\n" +
                    "    }\n" +
                    "}\n";
        }
    }

    @Override
    public void update(final AnActionEvent e) {
        tempFolder = null;
        VirtualFile operationFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String curModulePath = ModuleUtils.getCurModulePath(e.getProject(), operationFile);
        String sdkPath = FileUtils.getSdkPath(e.getProject(), operationFile);
        if (StringUtils.isBlank(sdkPath)) {
            e.getPresentation().setVisible(false);
        } else {
            if (operationFile != null) {
                String dirpath = operationFile.getPath();
                int index = dirpath.indexOf(NibiruConfig.STR);
                if (index <= 0) {
                    if (dirpath.endsWith("/main/java")) {
                        packageName = "";
                    } else {
                        if (!StringUtils.isBlank(curModulePath)) {
                            VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(curModulePath);
                            packageName = GradleUtils.getBuildpagename(e.getProject(), fileByPath);
                            if (packageName == null) {
                                return;
                            }
                            String replace = packageName.replace(".", "/");
                            tempPagePath = fileByPath.getPath() + "/src/main/java/" + replace;
                            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(tempPagePath);
                            if (file != null && file.exists()) {
                                tempFolder = file;
                            }
                        }
                    }
                } else {
                    String substr = dirpath.substring(index + NibiruConfig.STR.length());
                    packageName = substr.replace("/", ".");
                }
                if (StringUtils.isEmpty(curModulePath)) {
                    e.getPresentation().setVisible(false);//该action 的可见性
                } else {
                    boolean contains = dirpath.contains(curModulePath);
                    e.getPresentation().setVisible(contains);//该action 的可见性
                }
            } else {
                e.getPresentation().setVisible(false);
            }
        }
    }

    /**
     * 创建包名目录
     *
     * @param result
     */
    private void createpageDir(String result) {
        if (StringUtils.isEmpty(result)) {
            return;
        }
        VirtualFile b = dgcreateDir(result);
        if (b != null && b.exists()) {
            try {
                String[] target = result.split("/");
                String[] current = b.getPath().split("/");
                for (int i = 0; i < target.length - current.length; i++) {
                    VirtualFile childDirectory = b.createChildDirectory(this, target[current.length + i]);
                    b = childDirectory;
                    VirtualFileManager.getInstance().syncRefresh();
                }
                tempFolder = b;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private VirtualFile dgcreateDir(String result) {
        int last = result.lastIndexOf("/");
        String substring = result.substring(0, last);
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(substring);
        if (file != null && file.exists()) {
            tempPagePath = "";
            return file;
        } else {
            return dgcreateDir(substring);
        }
    }
}
