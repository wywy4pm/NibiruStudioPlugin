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
                        //Log.i("isModuleFolder ModulePath  iml = " + module.getModuleFilePath());
                        String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
                        //Log.i("isModuleFolder ModulePath  = " + modulePath);
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
                        Log.i("getModulePath ModulePath  iml = " + module.getModuleFilePath());
                        String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
                        Log.i("getModulePath ModulePath  = " + modulePath);
                        if (!StringUtils.isBlank(selectFile.getPath()) && selectFile.getPath().equals(modulePath)) {
                            return modulePath;
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String getCurModulePath(Project project, VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        if (module != null && !StringUtils.isBlank(module.getName()) && !module.getName().equals(project.getName())) {
            String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
            Log.i("getCurModulePath ModulePath  = " + modulePath);
            return modulePath;
        }
        return "";
    }

//    public static String getCurModuleName(Project project, VirtualFile selectFile) {
//        if (project != null && ModuleManager.getInstance(project) != null) {
//            Module[] modules = ModuleManager.getInstance(project).getModules();
//            if (modules.length > 0) {
//                for (Module module : modules) {
//                    if (module != null && !StringUtils.isBlank(module.getName())) {
//                        if (!StringUtils.isBlank(module.getProject().getName())
//                                && module.getProject().getName().equals(module.getName())) {
//                            continue;
//                        }
//                        String secondName = getSelectFileSecondName(selectFile, project.getName());
//                        if (module.getName().equals(secondName)) {
//                            return module.getName();
//                        }
//                    }
//                }
//            }
//        }
//        return "";
//    }
//
//    public static String getSelectFileSecondName(VirtualFile selectFile, String projectName) {
//        if (selectFile != null) {
//            if (!StringUtils.isBlank(selectFile.getPath()) && selectFile.getPath().contains("/")) {
//                Log.i("getCurModuleName selectPath = " + selectFile.getPath());
//                int index = selectFile.getPath().indexOf(projectName);
//                if (index > -1) {
//                    String projectPath = selectFile.getPath().substring(index);
//                    Log.i("getCurModuleName projectPath = " + projectPath);
//                    if (!StringUtils.isBlank(projectPath)) {
//                        int secondIndex = projectPath.indexOf("/");
//                        if (secondIndex > -1) {
//                            String moduleRealPath = projectPath.substring(secondIndex + 1);
//                            Log.i("getCurModuleName moduleRealPath = " + moduleRealPath);
//                            if (!StringUtils.isBlank(moduleRealPath)) {
//                                int thirdIndex = moduleRealPath.indexOf("/");
//                                if (thirdIndex > -1) {
//                                    String moduleName = moduleRealPath.substring(0, thirdIndex);
//                                    Log.i("getCurModuleName moduleName = " + moduleName);
//                                    return moduleName;
//                                } else {
//                                    return moduleRealPath;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return "";
//    }
}
