package com.nibiru.plugin.injectAction;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.awt.RelativePoint;
import com.nibiru.plugin.utils.Log;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class InjectUtils {
    public static PsiFile getLayoutFileFromCaret(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);

        PsiFile layout = findLayoutResource(candidateA);
        if (layout != null) {
            return layout;
        }
        return findLayoutResource(candidateB);
    }

    public static PsiFile findLayoutResource(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement layout = element.getParent().getFirstChild();
        if (layout == null) {
            return null;
        }
        if (!layout.getText().contains(".nss")) {
            return null;
        }
        Project project = element.getProject();
        String text = element.getText();
        String replace = text.replace("\"", "");
        String[] split = replace.split("/");
        Log.i(split[split.length - 1]);
        return resolveLayoutResourceFile(element, project, split[split.length - 1]);
    }

    private static PsiFile resolveLayoutResourceFile(PsiElement element, Project project, String name) {
        Module module = ModuleUtil.findModuleForPsiElement(element);
        PsiFile[] files = null;
        if (module != null) {
            GlobalSearchScope moduleScope = module.getModuleWithDependenciesScope();
            files = FilenameIndex.getFilesByName(project, name, moduleScope);
            if (files == null || files.length <= 0) {
                moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
                files = FilenameIndex.getFilesByName(project, name, moduleScope);
            }
        }
        if (files == null || files.length <= 0) {
            files = FilenameIndex.getFilesByName(project, name, new EverythingGlobalScope(project));
            if (files.length <= 0) {
                return null;
            }
        }
        return files[0];
    }

    public static ArrayList<Element> getIDsFromLayout(final PsiFile file) {
        final ArrayList<Element> elements = new ArrayList<Element>();

        return getIDsFromLayout(file, elements);
    }

    public static ArrayList<Element> getIDsFromLayout(final PsiFile file, final ArrayList<Element> elements) {
        String text = file.getText();
        try {
            JSONObject jsonObject = parseJSONObject(text);
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equalsIgnoreCase("SceneObjects")) {
                    JSONArray sceneArray = new JSONArray(jsonObject.get(key).toString());
                    if (sceneArray.length() > 0 && sceneArray.get(0) instanceof JSONObject) {
                        for (int i = 0; i < sceneArray.length(); i++) {
                            JSONObject sceneObject = sceneArray.getJSONObject(i);
                            Iterator iterator = sceneObject.keys();
                            while (iterator.hasNext()) {
                                String keynew = (String) iterator.next();
                                if (keynew.equalsIgnoreCase("SceneObject")) {
                                    Element element = new Element();
                                    JSONObject childjson = parseJSONObject(sceneObject.get(keynew).toString());
                                    Iterator childiter = childjson.keys();
                                    while (childiter.hasNext()) {
                                        String next = (String) childiter.next();
                                        if (next.equals("ID")) {
                                            String id = childjson.getString(next);
                                            element.setId(id);
                                        }
                                        if (next.equals("Type")) {
                                            String type = childjson.getString(next);
                                            element.setType(type);
                                            elements.add(element);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return elements;
    }

    public static JSONObject parseJSONObject(String jsonStr) throws JSONException {
        if (jsonStr.startsWith("{")) {
            return new JSONObject(jsonStr);
        } else if (jsonStr.startsWith("[")) {
            JSONArray jsonArray = new JSONArray(jsonStr);

            if (jsonArray.length() > 0 && jsonArray.get(0) instanceof JSONObject) {
                return getJsonObject(jsonArray);
            }
        }
        return null;
    }

    private static JSONObject getJsonObject(JSONArray jsonArray) throws JSONException {
        JSONObject resultJSON = jsonArray.getJSONObject(0);

        for (int i = 1; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (!(value instanceof JSONObject)) {
                break;
            }
            JSONObject json = (JSONObject) value;
            Iterator keys = json.keys();
            Set<String> set = IteratorToSet.toSet(keys);
            Set<String> resultJSONset = IteratorToSet.toSet(resultJSON.keys());
            for (String key : set) {
                if (!resultJSONset.contains(key)) {
                    resultJSON.put(key, json.get(key));
                }
            }
        }
        return resultJSON;
    }

    public static void showInfoNotification(Project project, String text) {
        showNotification(project, MessageType.INFO, text);
    }

    public static void showErrorNotification(Project project, String text) {
        showNotification(project, MessageType.ERROR, text);
    }

    public static void showNotification(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


    public static int getInjectCount(ArrayList<Element> elements) {
        int cnt = 0;
        for (Element element : elements) {
            if (element.used) {
                cnt++;
            }
        }
        return cnt;
    }

}
