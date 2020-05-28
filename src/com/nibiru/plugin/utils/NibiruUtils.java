package com.nibiru.plugin.utils;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Base64;
import com.nibiru.plugin.http.NibiruDESUtil;
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
        NibiruConfig.isLogin = false;
        NibiruConfig.deviceIsActivate = false;
        NibiruConfig.loginBean = null;
        PropertiesUtils.setString(PropertiesUtils.LOGIN_NAME, "");
        PropertiesUtils.setString(PropertiesUtils.LOGIN_PAASWORD, "");

        Messages.showMessageDialog(StringConstants.LOG_OUT_SUCCED, StringConstants.LOG_OUT, UiUtils.getCompleteIcon());
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        LoginDialog loginDialog = new LoginDialog(event, event.getProject(), file);
        loginDialog.setisrelogin(true);
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

    /**
     * 生成机器码
     */
    public static final String createDeviceID() {
        String devid = getUUID() + getBoisID() + getCPUID();
        String result = MD532(devid);
        return result;
    }

    /**
     * 32位MD5加密的大写字符串
     *
     * @param s
     * @return
     */
    public final static String MD532(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转换成十六进制字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 生成机器码
     */
    public static final String getUUID() {
        try {
            Process process = Runtime.getRuntime().exec("wmic csproduct get uuid");
            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.append((char) c);
            } catch (IOException e) {
            }
            String output = sw.toString();
            int index = output.indexOf("\r\r\n");
            if (index > -1) {
                String substring = output.substring(index).trim();
                return substring;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 生成机器码
     */
    public static final String getCPUID() {
        try {
            Process process = Runtime.getRuntime().exec("wmic cpu get processorid");
            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.append((char) c);
            } catch (IOException e) {
            }
            String output = sw.toString();
            int index = output.indexOf("\r\r\n");
            if (index > -1) {
                String substring = output.substring(index).trim();
                return substring;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 生成机器码
     */
    public static final String getBoisID() {
        try {
            Process process = Runtime.getRuntime().exec("wmic BaseBoard get SerialNumber");
            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.append((char) c);
            } catch (IOException e) {
            }
            String output = sw.toString();
            int index = output.indexOf("\r\r\n");
            if (index > -1) {
                int lastIndex = output.indexOf("\r\r\n", index + 3);
                if (lastIndex > index) {
                    String substring = output.substring(index + 3, lastIndex - index - 3);
                    if (substring.contains(" ")) {
                        return substring;
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

}
