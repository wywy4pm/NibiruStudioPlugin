package com.nibiru.plugin.injectAction;

import java.util.ArrayList;
import java.util.HashMap;

public class Definitions {

    public static final HashMap<String, String> paths = new HashMap<String, String>();
    public static final ArrayList<String> adapters = new ArrayList<String>();

    static {
        paths.put("GifView", "x.core.ui.animation.XAnimGIFImage");
        paths.put("Image", "x.core.ui.XImage");
        paths.put("Label", "x.core.ui.XLabel");
        paths.put("ImageText", "x.core.ui.XImageText");
        paths.put("TextEdit", "x.core.ui.XEditText");
        paths.put("Panel", "x.core.ui.XPanel");
        paths.put("Button", "x.core.ui.XButton");
        paths.put("StaticModel", "x.core.ui.XStaticModelActor");

        adapters.add("x.core.ui.XBaseScene");
    }
}
