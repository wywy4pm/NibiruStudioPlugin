package com.nibiru.plugin.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
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
                        Log.i("ModulePath  iml = " + module.getModuleFilePath());
                        String modulePath = FileUtils.getModulePath(module.getModuleFilePath());
                        Log.i("ModulePath  = " + modulePath);
                        if (!StringUtils.isBlank(selectFolderPath) && selectFolderPath.equals(modulePath)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
