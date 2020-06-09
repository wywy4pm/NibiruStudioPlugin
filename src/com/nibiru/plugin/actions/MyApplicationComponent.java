package com.nibiru.plugin.actions;


import com.intellij.openapi.components.BaseComponent;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpManager;
import com.nibiru.plugin.http.NibiruDESUtil;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

public class MyApplicationComponent implements BaseComponent {

    public void initComponent() {
        if (NibiruConfig.isLogin) {
            return;
        }
        String name = PropertiesUtils.getString(PropertiesUtils.LOGIN_NAME);
        String password = PropertiesUtils.getString(PropertiesUtils.LOGIN_PAASWORD);
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
            return;
        }
        String userName = NibiruDESUtil.decryptStr(name,NibiruDESUtil.DEFAULT_KEY_STR);
        String passwordAsString =NibiruDESUtil.decryptStr(password,NibiruDESUtil.DEFAULT_KEY_STR);
            if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(passwordAsString)) {
                HttpManager.Login(userName, passwordAsString, new HttpManager.LoginCallback() {
                    @Override
                    public void onSucceed(LoginBean loginBean) {
                        NibiruConfig.isLogin = true;
                        NibiruConfig.loginBean = loginBean;
                        if (loginBean.getAccount() != null) {
                            LoginBean.AccountBean account = loginBean.getAccount();
                            if (account.isActiveStatus()) {
                                NibiruConfig.deviceIsActivate = true;
                            }
                        }
                    }

                    @Override
                    public void onFailed(int errorCode) {
                    }
                });
        }
    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "myApplicationComponent";
    }
}