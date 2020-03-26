package com.nibiru.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.io.IOException;
import java.io.InputStream;

public class NibiruUtils {

    /**
     * 通过地址,获取到注册表信息
     *
     * @param location path in the registry
     * @param key      registry key
     * @return registry value or null if not found
     */

    public static final String readRegistry(String location, String key) {
        try {
            Process process = Runtime.getRuntime().exec("reg query " +
                    '"' + location + "\" /v " + key);

            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();

            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.append((char) c);
            } catch (IOException e) {
            }

            String output = sw.toString();
            int i = output.indexOf("REG_SZ");
            if (i == -1) {
                return null;
            }

            sw = new StringBuilder();
            i += 6; // skip REG_SZ

            // skip spaces or tabs
            for (; ; ) {
                if (i > output.length())
                    break;
                char c = output.charAt(i);
                if (c != ' ' && c != '\t')
                    break;
                ++i;
            }

            // take everything until end of line
            for (; ; ) {
                if (i > output.length())
                    break;
                char c = output.charAt(i);
                if (c == '\r' || c == '\n')
                    break;
                sw.append(c);
                ++i;
            }

            return sw.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
