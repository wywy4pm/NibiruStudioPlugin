package com.nibiru.plugin.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.arguments.GrArgumentListImpl;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrCommandArgumentListImpl;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.path.GrCallExpressionImpl;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.path.GrMethodCallExpressionImpl;

import java.util.*;

public class GradleUtils {

//    public static void readGradle(PsiElement gradleElement) {
//        if (gradleElement != null) {
//            if (gradleElement instanceof GrMethodCallExpressionImpl) {
//
//            } else if (gradleElement instanceof GrCallExpressionImpl) {
//                if (gradleElement.getChildren().length >= 2) {
//
//                }
//            } else if (gradleElement instanceof GrArgumentListImpl) {
//                GroovyPsiElement[] grPsiElements = ((GrArgumentListImpl) gradleElement).getAllArguments();
//
//            }
//            PsiElement[] psiElements = gradleElement.getChildren();
//            if (psiElements.length > 0) {
//                for (PsiElement psiElement : psiElements) {
//
//                }
//            }
//        }
//    }

    /**
     * 获取当前module下的build.gradle里的元素
     *
     * @param project
     * @return
     */
    public static PsiElement addAppBuildFile(Project project, String aarName, String selectModuleName) {
        Log.i("addAppBuildFile aarName = " + aarName);
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, "build.gradle", GlobalSearchScope.projectScope(project));//Arrays.asList(folder.getChildren()
        String compileText = "implementation";
        Log.i("parentName = " + project.getName());
        List<PsiFile> psiFileList = Arrays.asList(psiFiles);
        Collections.reverse(psiFileList);
        for (PsiFile psiFile : psiFileList) {
            if (psiFile.getParent() != null && !StringUtils.isBlank(psiFile.getParent().getName())) {
                if (psiFile.getParent().getName().equals(project.getName())) {
                    compileText = getGradleClasspath(psiFile);
                    continue;
                } else if (!psiFile.getParent().getName().equals(selectModuleName)) {
                    continue;
                }
            }
            PsiElement[] psiElements = psiFile.getChildren();
            if (psiElements.length > 0) {
                boolean isAddRepos = isAddRepos(project, psiElements);
                for (PsiElement psiElement : psiElements) {
                    if (psiElement.getFirstChild() != null && !StringUtils.isBlank(psiElement.getFirstChild().getText())) {
                        PsiElement firstChild = psiElement.getFirstChild();
                        String firstText = firstChild.getText();
                        Log.i("addAppBuildFile firstText = " + firstText);
                        if (firstText.equals("dependencies")) {
                            String depend = compileText + "(name: '" + aarName + "', ext: 'aar')";
                            if (!isAddDependencies(project, psiElement, aarName, depend, compileText)) {
                                addDependencies(project, psiElement, depend);
                            }
                        } else if (firstText.equals("android") && !isAddRepos) {
                            addRepositories(project, psiElement, psiFile);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getGradleClasspath(PsiFile psiFile) {
        String compileText = "implementation";
        PsiElement[] psiElements = psiFile.getChildren();
        if (psiElements.length > 0) {
            for (PsiElement psiElement : psiElements) {
                if (psiElement.getFirstChild() != null && !StringUtils.isBlank(psiElement.getFirstChild().getText())) {
                    if (psiElement instanceof GrMethodCallExpressionImpl && "buildscript".equals(psiElement.getFirstChild().getText())) {
                        GrClosableBlock[] closureArguments = ((GrMethodCallExpressionImpl) psiElement).getClosureArguments();
                        if (closureArguments.length > 0 && closureArguments[0] != null) {
                            GrStatement[] statements = closureArguments[0].getStatements();
                            if (statements.length > 0) {
                                for (int i = 0; i < statements.length; i++) {
                                    GrStatement grStatement = statements[i];
                                    Log.i("getGradleClasspath grStatement = " + grStatement.getText());
                                    if ("dependencies".equals(grStatement.getFirstChild().getText())) {
                                        if (grStatement instanceof GrMethodCallExpressionImpl) {
                                            GrClosableBlock[] inClosureArguments = ((GrMethodCallExpressionImpl) grStatement).getClosureArguments();
                                            if (inClosureArguments.length > 0 && inClosureArguments[0] != null) {
                                                GrStatement[] inStatements = inClosureArguments[0].getStatements();
                                                if (inStatements.length > 0) {
                                                    for (int j = 0; j < inStatements.length; j++) {
                                                        GrStatement inGrStatement = inStatements[j];
                                                        Log.i("getGradleClasspath inGrStatement = " + inGrStatement.getText());
                                                        if (inGrStatement.getChildren().length == 2 && inGrStatement.getChildren()[1] != null) {
                                                            if (inGrStatement.getChildren()[1] instanceof GrArgumentListImpl) {
                                                                GroovyPsiElement[] grPsiElements = ((GrArgumentListImpl) inGrStatement.getChildren()[1]).getAllArguments();
                                                                if (grPsiElements.length > 0 && grPsiElements[0].getLastChild() != null) {
                                                                    Log.i("getGradleClasspath getLastChild Text = " + grPsiElements[0].getLastChild().getText());
                                                                    if (!StringUtils.isBlank(grPsiElements[0].getLastChild().getText())) {
                                                                        String value = grPsiElements[0].getLastChild().getText().replaceAll("'", "");
                                                                        if (value.contains("com.android.tools.build:gradle:")) {
                                                                            value = value.replace("com.android.tools.build:gradle:", "");
                                                                            if (!StringUtils.isBlank(value)) {
                                                                                String firstVersion = value.substring(0, 1);
                                                                                int firstVersionInt = Integer.parseInt(firstVersion);
                                                                                if (firstVersionInt >= 3) {
                                                                                    compileText = "implementation";
                                                                                } else {
                                                                                    compileText = "compile";
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return compileText;
    }

    public static boolean isAddDependencies(Project project, PsiElement psiElement, String aarName, String newDepend, String compileText) {
        if (psiElement instanceof GrMethodCallExpressionImpl) {
            GrClosableBlock[] closureArguments = ((GrMethodCallExpressionImpl) psiElement).getClosureArguments();
            if (closureArguments.length > 0 && closureArguments[0] != null) {
                GrStatement[] dependStatements = closureArguments[0].getStatements();
                if (dependStatements.length > 0) {
                    for (int i = 0; i < dependStatements.length; i++) {
                        GrStatement grStatement = dependStatements[i];
                        if (grStatement.getChildren().length == 2
                                && grStatement.getChildren()[0] != null
                                && grStatement.getChildren()[1] != null) {
                            Log.i("addAppBuildFile isAddDependencies0 = " + grStatement.getChildren()[0].getText());
                            Log.i("addAppBuildFile isAddDependencies1 = " + grStatement.getChildren()[1].getText());
                            if (grStatement.getChildren()[1] instanceof GrArgumentListImpl) {
                                GroovyPsiElement[] grPsiElements = ((GrArgumentListImpl) grStatement.getChildren()[1]).getAllArguments();
                                if (grPsiElements.length > 0 && grPsiElements[0].getLastChild() != null) {
                                    Log.i("grPsiElements getLastChild Text = " + grPsiElements[0].getLastChild().getText());
                                    if (!StringUtils.isBlank(grPsiElements[0].getLastChild().getText())) {
                                        String name = grPsiElements[0].getLastChild().getText().replaceAll("'", "");
                                        if (!StringUtils.isBlank(name)) {
                                            if (name.equals(aarName)) {
                                                if (!StringUtils.isBlank(grStatement.getChildren()[0].getText())) {
                                                    if (!grStatement.getChildren()[0].getText().equals(compileText)) {
                                                        replaceDependencies(project, grStatement, newDepend);
                                                    }
                                                }
                                                return true;
                                            } else if (name.startsWith("nibiru_studio")) {
                                                replaceDependencies(project, grStatement, newDepend);
                                                return true;
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
        return false;
    }

    public static boolean isAddRepos(Project project, PsiElement[] psiElements) {
        for (PsiElement psiElement : psiElements) {
            if (psiElement.getFirstChild() != null && !StringUtils.isBlank(psiElement.getFirstChild().getText())) {
                String firstText = psiElement.getFirstChild().getText();
                if (firstText.equals("repositories") && psiElement instanceof GrMethodCallExpressionImpl) {
                    GrClosableBlock[] closureArguments = ((GrMethodCallExpressionImpl) psiElement).getClosureArguments();
                    if (closureArguments.length > 0 && closureArguments[0] != null) {
                        //Log.i("closureArgument = " + closureArguments[0].getText());
                        GrStatement[] flatStatements = closureArguments[0].getStatements();
                        if (flatStatements.length > 0 && flatStatements[0] != null
                                && flatStatements[0] instanceof GrMethodCallExpressionImpl) {
                            //Log.i("flatStatement = " + flatStatements[0].getText());
                            if (flatStatements[0].getFirstChild() != null
                                    && flatStatements[0].getFirstChild().getText().equals("flatDir")) {
                                GrClosableBlock[] flatClosureArguments = ((GrMethodCallExpressionImpl) flatStatements[0]).getClosureArguments();
                                if (flatClosureArguments.length > 0 && flatClosureArguments[0] != null) {
                                    //Log.i("flatClosureArgument = " + flatClosureArguments[0].getText());
                                    GrStatement[] dirStatements = flatClosureArguments[0].getStatements();
                                    if (dirStatements.length > 0) {
                                        boolean isContainsLib = false;
                                        for (GrStatement dirStatement : dirStatements) {
                                            Log.i("dirStatement = " + dirStatement.getText());
                                            if (dirStatement.getChildren().length == 2 && dirStatement.getChildren()[1] != null
                                                    && dirStatement.getChildren()[1] instanceof GrCommandArgumentListImpl) {
                                                String dirText = dirStatement.getChildren()[1].getText();
                                                if (dirText.contains("'libs'")) {
                                                    isContainsLib = true;
                                                }
                                            }
                                        }
                                        if (!isContainsLib) {
                                            addDir(project, flatClosureArguments[0]);
                                        }
                                    } else {
                                        addDir(project, flatClosureArguments[0]);
                                    }
                                }
                            }
                        } else {
                            addFlatDir(project, closureArguments[0]);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void addRepositories(Project project, PsiElement androidElement, PsiFile gradleFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement flatElement = GroovyPsiElementFactory.getInstance(project).createStatementFromText("repositories { \n" +
                    "    flatDir { \n" +
                    "        dirs 'libs' \n" +
                    "    } \n" +
                    "}");
            gradleFile.addAfter(flatElement, androidElement);
            VirtualFileManager.getInstance().syncRefresh();
        });
    }

    public static void addFlatDir(Project project, PsiElement reposBlockElement) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement dirElement = GroovyPsiElementFactory.getInstance(project).createStatementFromText("flatDir {\n" +
                    "            dirs 'libs'\n" +
                    "        }");
            reposBlockElement.addBefore(dirElement, reposBlockElement.getLastChild());
            VirtualFileManager.getInstance().syncRefresh();
        });
    }

    public static void addDir(Project project, PsiElement flatBlockElement) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement dirElement = GroovyPsiElementFactory.getInstance(project).createStatementFromText("dirs 'libs'");
            flatBlockElement.addBefore(dirElement, flatBlockElement.getLastChild());
            VirtualFileManager.getInstance().syncRefresh();
        });
    }

    public static void addDependencies(Project project, PsiElement dependenciesElement, String depend) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            GrStatement statement = GroovyPsiElementFactory.getInstance(project).createStatementFromText(depend);
            PsiElement dependenciesClosableBlock = dependenciesElement.getLastChild();
            //添加依赖项在 } 前，即在dependencies 末尾添加新的依赖项
            dependenciesClosableBlock.addBefore(statement, dependenciesClosableBlock.getLastChild());
            VirtualFileManager.getInstance().syncRefresh();
        });
    }

    public static void replaceDependencies(Project project, GrStatement sourceStatement, String newDepend) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            GrStatement newStatement = GroovyPsiElementFactory.getInstance(project).createStatementFromText(newDepend);
            sourceStatement.replace(newStatement);
            VirtualFileManager.getInstance().syncRefresh();
            //sourceStatement.removeStatement();
        });
    }

    public static void getBuildpagename(Project project, VirtualFile virtualFile) {
        VirtualFile[] children = virtualFile.getChildren();
        for (VirtualFile child : children) {
            if (!child.isDirectory() && child.getName().equalsIgnoreCase("build.gradle")) {
                PsiFile buildfile = PsiManager.getInstance(project).findFile(child);
                PsiElement[] psiElements = buildfile.getChildren();
                for (int i = 0; i < psiElements.length; i++) {
                    PsiElement firstChild = psiElements[i].getFirstChild();
                    if (firstChild != null && !StringUtils.isBlank(firstChild.getText()) && firstChild.getText().equalsIgnoreCase("android")) {
                        String android = psiElements[i].getText();
                        int index = android.indexOf("applicationId");
                        if (index > 0) {
                            int indexstart = android.indexOf("\"", index);
                            int indexend = android.indexOf("\"", indexstart + 1);
                            NibiruConfig.packagename = android.substring(indexstart + 1, indexend);
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
}
