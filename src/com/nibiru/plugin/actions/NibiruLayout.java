package com.nibiru.plugin.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.nibiru.plugin.ui.*;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class NibiruLayout extends AnAction {
    private VirtualFile folder;
    private Project project;
    private Boolean isEditWithNss;
    private String layoutname;
    private AnActionEvent anActionEvent;

    @Override
    public void actionPerformed(AnActionEvent e) {
        anActionEvent = e;
        folder = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        project = e.getProject();
        CreateLayoutDialog dialog = new CreateLayoutDialog(project, folder);
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


    private CreateLayoutDialog.Callback callback = new CreateLayoutDialog.Callback() {
        @Override
        public void showDialogResult(String layoutName, boolean isEditWithNss) {
            NibiruLayout.this.layoutname = layoutName;
            NibiruLayout.this.isEditWithNss = isEditWithNss;
            String curModulePath = ModuleUtils.getCurModulePath(project, folder);
            VirtualFile nssfile = LocalFileSystem.getInstance().findFileByPath(curModulePath + "/src/main/Assets/layout/" + layoutName + NibiruConfig.LAYOUT_SUFFIX);
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
                }
            }
        }
    };

    protected Runnable getRunnableWrapper(final Runnable runnable) {
        return new Runnable() {
            @Override
            public void run() {
                if (project == null) {
                    return;
                }
                CommandProcessor.getInstance().executeCommand(project, runnable, " delete " + layoutname + NibiruConfig.LAYOUT_SUFFIX, ActionGroup.EMPTY_GROUP);//cut 是 undo 的描述 我应该填写类名
            }
        };
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            createAssets();
        }
    };


    @Override
    public void update(final AnActionEvent e) {
        VirtualFile operationFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String sdkPath = FileUtils.getSdkPath(e.getProject(), operationFile);
        if (StringUtils.isBlank(sdkPath)) {
            e.getPresentation().setVisible(false);
        } else {
            e.getPresentation().setVisible(true);
        }
    }
}

