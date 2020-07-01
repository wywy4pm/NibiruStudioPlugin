package com.nibiru.plugin.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;

public class ModuleUtils {

    public static boolean isModuleFolder(Project project, String selectFolderPath) {
        if (project != null && ModuleManager.getInstance(project) != null) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            if (modules.length > 0) {
                for (Module module : modules) {
                    if (module != null && !StringUtils.isBlank(module.getName())) {
                        if (!StringUtils.isBlank(module.getProject().getName())
                                && module.getProject().getName().equals(module.getName())) {
                            continue;
                        }
                        String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
                        if (!StringUtils.isBlank(selectFolderPath) && selectFolderPath.equals(modulePath)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getModulePath(Project project, VirtualFile selectFile) {
        if (project != null && ModuleManager.getInstance(project) != null && selectFile != null) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            if (modules.length > 0) {
                for (Module module : modules) {
                    if (module != null && !StringUtils.isBlank(module.getName())) {
                        if (!StringUtils.isBlank(module.getProject().getName())
                                && module.getProject().getName().equals(module.getName())) {
                            continue;
                        }
                        String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
                        if (!StringUtils.isBlank(selectFile.getPath()) && selectFile.getPath().equals(modulePath)) {
                            return modulePath;
                        }
                    }
                }
            }
        }
        return "";
    }

//    public static String getCurModulePath(Project project, VirtualFile file) {
//        Module module = ModuleUtil.findModuleForFile(file, project);
//        if (module != null && !StringUtils.isBlank(module.getName()) && !module.getName().equals(project.getName())) {
//            String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
//            return modulePath;
//        }
//        return "";
//    }

    public static String getCurModulePath(Project project, VirtualFile file) {
        Module curmodule = ModuleUtil.findModuleForFile(file, project);
        if (curmodule != null) {
            return project.getBasePath() + "/" + curmodule.getName();
        }
        return "";
    }
}
