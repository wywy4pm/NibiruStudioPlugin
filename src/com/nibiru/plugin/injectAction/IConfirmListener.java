package com.nibiru.plugin.injectAction;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

public interface IConfirmListener {

    public void onConfirm(Project project, Editor editor, ArrayList<Element> elements);
}
