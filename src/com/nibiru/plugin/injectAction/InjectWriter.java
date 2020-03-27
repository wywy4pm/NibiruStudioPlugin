package com.nibiru.plugin.injectAction;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import java.util.ArrayList;

public class InjectWriter extends WriteCommandAction.Simple {
    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected ArrayList<Element> mElements;
    protected PsiElementFactory mFactory;
    protected String mLayoutFileName;

    public InjectWriter(PsiFile file, PsiClass clazz, String command, ArrayList<Element> elements, String layoutFileName) {
        super(clazz.getProject(), command);
        mFile = file;
        mProject = clazz.getProject();
        mClass = clazz;
        mElements = elements;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        mLayoutFileName = layoutFileName;
    }

    @Override
    public void run() throws Throwable {
        if (InjectUtils.getInjectCount(mElements) > 0) {
            generateFields();
        }
        InjectUtils.showInfoNotification(mProject, String.valueOf(InjectUtils.getInjectCount(mElements)));

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }

    protected void generateFields() {
        for (Element element : mElements) {
            if (element.isUsed()) {
                StringBuilder injection = new StringBuilder();
                injection.append('@');
                injection.append("NibiruActor");
                injection.append('(');
                injection.append("\"" + element.id + "\"");
                injection.append(") ");
                if (Definitions.paths.containsKey(element.type)) { // listed class
                    injection.append(Definitions.paths.get(element.type));
                }
                injection.append(" ");
                injection.append(element.vartriablename);
                injection.append(";");

                mClass.add(mFactory.createFieldFromText(injection.toString(), mClass));
            }
        }
    }
}