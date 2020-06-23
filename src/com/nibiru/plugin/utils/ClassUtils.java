package com.nibiru.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;

public class ClassUtils {

    /**
     * 修改继承关系
     *
     * @param targetClass
     * @return
     */
    public static boolean changeSuperClass(PsiClass targetClass) {
        final PsiReferenceList psiReferenceList = targetClass.getExtendsList();
        if (psiReferenceList == null) {
            return false;
        }
        PsiReferenceList.Role role = psiReferenceList.getRole();
        if (role == PsiReferenceList.Role.EXTENDS_LIST) {
            if (psiReferenceList.getReferenceElements().length != 0) {
                PsiJavaCodeReferenceElement[] referenceElements = psiReferenceList.getReferenceElements();
                PsiJavaCodeReferenceElement referenceElement = referenceElements[0];
                String referenceName = referenceElement.getReferenceName();
                if (referenceName.equalsIgnoreCase("XBaseXRActivity")) {
                    return false;
                } else {
                    psiReferenceList.getReferenceElements()[0].handleElementRename("XBaseXRActivity");
                    addImport("x.core", targetClass);
                    createMethod("@Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }", targetClass);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改继承关系
     * 之前已经有了继承关系
     *
     * @param targetClass 目标类
     * @param parentname  父类的类名
     * @param packagename 包名
     * @return
     */
    public static boolean changeSuperClass(PsiClass targetClass, String parentname, String packagename) {
        final PsiReferenceList psiReferenceList = targetClass.getExtendsList();
        if (psiReferenceList == null) {
            return false;
        }
        PsiReferenceList.Role role = psiReferenceList.getRole();
        if (role == PsiReferenceList.Role.EXTENDS_LIST) {
            if (psiReferenceList.getReferenceElements().length != 0) {
                PsiJavaCodeReferenceElement[] referenceElements = psiReferenceList.getReferenceElements();
                PsiJavaCodeReferenceElement referenceElement = referenceElements[0];
                String referenceName = referenceElement.getReferenceName();
                if (referenceName.equalsIgnoreCase(parentname)) {
                    return false;
                } else {
                    //修改继承类的名称
                    psiReferenceList.getReferenceElements()[0].handleElementRename(parentname);
                    addImport(packagename, targetClass);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 创建方法
     * 删除onCreate方法
     *
     * @param method
     * @param cls
     */
    public static void createMethod(String method, PsiClass cls) {
        PsiMethod[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase("onCreate"))
                methods[i].delete();
        }
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(cls.getProject());
        PsiMethod methodElement = psiElementFactory.createMethodFromText(method, cls);
        cls.add(methodElement);
        CodeStyleManager.getInstance(cls.getProject()).reformat(cls);
    }

    /**
     * 导包
     */
    public static void addImport(String fullyQualifiedName, PsiClass psiClass) {
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        final PsiFile file = psiClass.getContainingFile();
        if (!(file instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;
        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            String impQualifiedName = is.getImportReference().getQualifiedName();
            if (fullyQualifiedName.equals(impQualifiedName)) {
                return;
            }
        }
        importList.add(psiElementFactory.createImportStatementOnDemand(fullyQualifiedName));
        CodeStyleManager.getInstance(psiClass.getProject()).reformat(javaFile);
    }

    /**
     * 创建成员变量
     *
     * @param field
     * @param mProject
     * @param mClass
     */
    public static void createField(String field, Project mProject, PsiClass mClass) {
        PsiElementFactory mFactory = JavaPsiFacade.getElementFactory(mProject);
        mClass.add(mFactory.createFieldFromText(field, mClass));
    }
}
