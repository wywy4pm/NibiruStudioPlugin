package com.nibiru.plugin.utils;
import com.intellij.openapi.editor.Document;
public class WriteAction
        implements Runnable
{
    private String text;
    private Document document;

    public WriteAction(String text, Document document)
    {
        this.text = text;
        this.document = document;
    }

    public void run()
    {
        this.document.setText(this.text);
    }
}
