package com.nibiru.plugin.http;

import com.google.gson.Gson;
import com.intellij.openapi.ui.Messages;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.utils.ComputerInfo;
import com.nibiru.plugin.utils.Log;
import com.nibiru.plugin.utils.NibiruUtils;
import com.nibiru.plugin.utils.StringConstants;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpManager {

    /**
     * 登录开发者账号
     *
     * @param name
     * @param password
     * @return
     */
    public static String Login(String name, String password, LoginCallback loginCallback) {
        String url = "https://dev.inibiru.com/DeveloperLoginAction";
//        String url = "https://112.124.67.116:8483/DeveloperServerV2/DeveloperLoginAction";//测试

        String localMac = "";
        try {
            localMac = NibiruUtils.getLocalMac();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            Object [] paramss = new Object[]{"name","password","macAddr"};
            Object [] valuess = new Object[]{NibiruDESUtil.encryptStr(name, NibiruDESUtil.DEFAULT_KEY_STR),NibiruDESUtil.encryptStr(NibiruUtils.md5(password), NibiruDESUtil.DEFAULT_KEY_STR),NibiruDESUtil.encryptStr(localMac, NibiruDESUtil.DEFAULT_KEY_STR)};
            List<NameValuePair> paramsList = HttpClientService.getParams(paramss, valuess);
            String request=  HttpClientService.sendPost(url,paramsList);
            String decryptStr = NibiruDESUtil.decryptStr(request, NibiruDESUtil.DEFAULT_KEY_STR);
            Gson gson = new Gson();
            LoginBean loginBean = gson.fromJson(decryptStr, LoginBean.class);
            if (loginBean==null){
                if (loginCallback != null) {
                    loginCallback.onFailed(-1);
                }
            }else {
                if (loginBean.getResCode() == 0) {
                    if (loginCallback != null) {
                        loginCallback.onSucceed(loginBean);
                    }
                } else {
                    if (loginCallback != null) {
                        loginCallback.onFailed(loginBean.getResCode());
                    }
                }
            }
            return request;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String DeviceAuth(String uid, DeviceAuthCallback callback) {
        String url = "https://dev.inibiru.com/DeviceAuthAction";
//        String url = "https://112.124.67.116:8483/DeveloperServerV2/DeviceAuthAction";//测试

        String localMac = "";
        try {
            localMac = NibiruUtils.getLocalMac();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            Object [] paramss = new Object[]{"uid","macAddr"};
            Object [] valuess = new Object[]{NibiruDESUtil.encryptStr(uid, NibiruDESUtil.DEFAULT_KEY_STR),NibiruDESUtil.encryptStr(localMac, NibiruDESUtil.DEFAULT_KEY_STR)};
            List<NameValuePair> paramsList = HttpClientService.getParams(paramss, valuess);
            String request=  HttpClientService.sendPost(url,paramsList);
            String decryptStr = NibiruDESUtil.decryptStr(request, NibiruDESUtil.DEFAULT_KEY_STR);
            if (callback != null) {
                callback.onResult(decryptStr);
            }
            return decryptStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface DeviceAuthCallback {
        void onResult(String result);
    }

    public interface LoginCallback {
        void onSucceed(LoginBean loginBean);

        void onFailed(int errorCode);
    }
}
