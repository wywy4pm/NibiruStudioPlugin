package com.nibiru.plugin.actions;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import com.nibiru.plugin.injectAction.*;
import com.nibiru.plugin.utils.Log;

import javax.swing.*;
import java.util.ArrayList;

public class InjectAction extends BaseGenerateAction implements IConfirmListener, ICancelListener {
    protected JFrame mDialog;

    @SuppressWarnings("unused")
    public InjectAction() {
        super(null);
    }

    @SuppressWarnings("unused")
    public InjectAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        actionPerformedImpl(project, editor);
    }

    @Override
    public void actionPerformedImpl(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiFile layout = InjectUtils.getLayoutFileFromCaret(editor, file);
        if (layout == null) {
            InjectUtils.showErrorNotification(project, "No nss found");
            return;
        }
        ArrayList<Element> elements = InjectUtils.getIDsFromLayout(layout);
        if (elements != null && !elements.isEmpty()) {
            showDialog(project, editor, elements);
        } else {
            InjectUtils.showErrorNotification(project, "No IDs found in nss");
        }
    }

    public void onConfirm(Project project, Editor editor, ArrayList<Element> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        PsiFile layout = InjectUtils.getLayoutFileFromCaret(editor, file);
        closeDialog();

        if (InjectUtils.getInjectCount(elements) > 0) {
            new InjectWriter(file, getTargetClass(editor, file), "Generate Injections", elements, layout.getName()).execute();
        } else {
            InjectUtils.showInfoNotification(project, "No injection was selected");
        }
    }

    public void onCancel() {
        closeDialog();
    }

    protected void showDialog(Project project, Editor editor, ArrayList<Element> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }

        PsiClass clazz = getTargetClass(editor, file);
        PsiField[] fields = clazz.getAllFields();
        for (PsiField field : fields) {
            String[] s = field.getFirstChild().getText().split(" ");
            if (s != null && s.length > 0) {
                for (int i = 0; i < s.length; i++) {
                    String annom = s[i];
                    Element tempElement = null;
                    if (annom.contains("@NibiruActor")) {
                        for (Element element : elements) {
                            if (annom.contains(element.id)) {
                                tempElement = element;
                                break;
                            }
                        }
                        if (tempElement != null) {
                            elements.remove(tempElement);
                        }
                    }
                }
            }
        }

        if (elements.isEmpty()) {
            InjectUtils.showErrorNotification(project, "No new IDs found in nss");
        }else {
            EntryList panel = new EntryList(project, editor, elements, this, this);
            mDialog = new JFrame();
            mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
            mDialog.getContentPane().add(panel);
            mDialog.pack();
            mDialog.setLocationRelativeTo(null);
            mDialog.setVisible(true);
        }
    }

    protected void closeDialog() {
        if (mDialog == null) {
            return;
        }
        mDialog.setVisible(false);
        mDialog.dispose();
    }
}
