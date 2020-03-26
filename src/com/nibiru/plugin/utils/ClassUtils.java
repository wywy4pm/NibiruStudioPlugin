package com.nibiru.plugin.utils;

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
}
