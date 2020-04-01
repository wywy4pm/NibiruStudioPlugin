package com.nibiru.plugin.http;

import com.google.gson.Gson;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.utils.Log;
import com.nibiru.plugin.utils.NibiruUtils;
import com.nibiru.plugin.utils.PropertiesUtils;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
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
        String localMac = "";
        try {
            localMac = NibiruUtils.getLocalMac();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<>();
        try {
            params.put("name", NibiruDESUtil.encryptStr(name));
            params.put("password", NibiruDESUtil.encryptStr(NibiruUtils.encryptStr(password)));
            Log.i(NibiruDESUtil.encryptStr(NibiruUtils.MD5(password)));
            params.put("macAddr", NibiruDESUtil.encryptStr(localMac));
            String request = HttpClientUtil.sendPostSSLRequest(url, params);
            String decryptStr = NibiruDESUtil.decryptStr(request);
            Gson gson = new Gson();
            LoginBean loginBean = gson.fromJson(decryptStr, LoginBean.class);
            if (loginBean.getResCode() == 0) {
                if (loginCallback != null) {
                    loginCallback.onSucceed(loginBean);
                }
            } else {
                if (loginCallback != null) {
                    loginCallback.onFailed(loginBean.getResCode());
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
        String localMac = "";
        try {
            localMac = NibiruUtils.getLocalMac();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<>();
        try {
            params.put("uid", NibiruDESUtil.encryptStr(uid));
            params.put("macAddr", NibiruDESUtil.encryptStr(localMac));
            String request = HttpClientUtil.sendPostSSLRequest(url, params);
            String decryptStr = NibiruDESUtil.decryptStr(request);
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
