package com.nibiru.plugin.utils;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Base64;
import com.nibiru.plugin.ui.LoginDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NibiruUtils {


    public static void logout(AnActionEvent event) {
        NibiruConfig.isLogin=false;
        NibiruConfig.deviceIsActivate = false;
        NibiruConfig.loginBean=null;
        CredentialUtils.putString(CredentialUtils.LOGIN_INFO, "", "");
        Messages.showMessageDialog(StringConstants.LOG_OUT_SUCCED, StringConstants.LOG_OUT,null);
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        LoginDialog loginDialog = new LoginDialog(event,event.getProject(), file);
        loginDialog.show();
    }

    public static String md5(String str) {
        String newStr = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bs = digest.digest(str.getBytes("UTF-8"));
            newStr = Base64.encode(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newStr;
    }

    /**
     * 16位MD5
     *
     * @param sourceStr
     * @return
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }


    public static String getLocalMac() throws SocketException, UnknownHostException {

        InetAddress ia = InetAddress.getLocalHost();
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
//        String replace = sb.toString().toUpperCase().replace("-", "");
        return sb.toString();
    }

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
