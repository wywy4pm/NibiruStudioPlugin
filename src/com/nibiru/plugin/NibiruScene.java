package com.nibiru.plugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.nibiru.plugin.ui.CreateSceneDialog;
import com.nibiru.plugin.ui.ImportStudioDialog;
import com.nibiru.plugin.utils.FileUtils;
import com.nibiru.plugin.utils.ModifyAndroidManifest;
import com.nibiru.plugin.utils.NibiruConfig;

import java.io.IOException;

public class NibiruScene extends AnAction {
    private VirtualFile folder;
    private Project project;
    private String scenename;
    private Boolean isLauncherScene;
    private String layoutname;
    private boolean isSourceFolder;
    private String packageName;

    @Override
    public void actionPerformed(AnActionEvent e) {
        folder = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        project = e.getProject();
        CreateSceneDialog dialog = new CreateSceneDialog(project, folder);
        dialog.addCallback(callback);
        dialog.show();
        //dialog.setEditRequestFocus();
    }

    private void createAssets() {
        VirtualFile baseFile = project.getBaseDir();
        VirtualFile[] childFiles = baseFile.getChildren();
        if (childFiles.length > 0) {
            for (VirtualFile childFile : childFiles) {
                String path = childFile.getPath();
                if (folder.getPath().contains(path)) {
                    getOutputPath(childFile.getChildren());
                    break;
                }
            }
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
                                assetslayout.createChildData(this, layoutname + NibiruConfig.LAYOUT_SUFFIX);
                                VirtualFileManager.getInstance().syncRefresh();
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
        public void showDialogResult(String sceneName, String layoutName, boolean isLauncherScene) {
            NibiruScene.this.scenename = sceneName;
            NibiruScene.this.layoutname = layoutName;
            NibiruScene.this.isLauncherScene = isLauncherScene;
            ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(runnable));

            if (!FileUtils.isAddLib(project, folder)) {
                ImportStudioDialog studioDialog = new ImportStudioDialog(project, folder);
                studioDialog.show();
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
            if (folder == null)
                return;
            try {
                createAssets();
                VirtualFile writeableFile = folder.createChildData(this, scenename + NibiruConfig.SUFFIX);
                writeableFile.setBinaryContent(getBinaryContent(packageName, scenename, layoutname));
                if (NibiruScene.this.isLauncherScene) {
                    new ModifyAndroidManifest(project, folder, packageName + "." + scenename);
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
                    "public class " + className + " extends XBaseScene implements XUI.LoadContentLayoutListener {\n" +
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
                    "   \n" +
                    "    @Override\n" +
                    "    public void onLoadCompleted() {\n" +
                    "    }\n" +
                    "}\n";
        } else {
            return "package " + packageString + ";\n" +
                    "\n" +
                    "import x.core.ui.XBaseScene;\n" +
                    "import x.core.ui.XUI;\n" +
                    "\n" +
                    "public class " + className + " extends XBaseScene implements XUI.LoadContentLayoutListener {\n" +
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
                    "   \n" +
                    "    @Override\n" +
                    "    public void onLoadCompleted() {\n" +
                    "    }\n" +
                    "}\n";
        }
    }

    @Override
    public void update(final AnActionEvent e) {
        VirtualFile operationFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (operationFile != null) {
            String dirpath = operationFile.getPath();
            int index = dirpath.indexOf(NibiruConfig.STR);
            if (index <= 0) {
                packageName = "";
            } else {
                String substr = dirpath.substring(index + NibiruConfig.STR.length());
                packageName = substr.replace("/", ".");
            }
            isSourceFolder = operationFile.isDirectory();
            boolean contains = dirpath.contains(NibiruConfig.STR);
            e.getPresentation().setVisible((isSourceFolder && contains));//该action 的可见性
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}
