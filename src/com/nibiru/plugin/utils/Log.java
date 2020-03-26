package com.nibiru.plugin.utils;

public class Log {
    public static void i (String... params) {
        if (params == null)
            return;
        String out = "";
        for (int i = 0; i < params.length; i++) {
            out += params[i] + "\n";
        }
        System.out.println(out);
    }

    public static void i(Object... params) {
        if (params == null)
            return;
        String out = "";
        for (int i = 0; i < params.length; i++) {
            out += params[i].toString() + "\n";
        }
        System.out.println(out);
    }
}
