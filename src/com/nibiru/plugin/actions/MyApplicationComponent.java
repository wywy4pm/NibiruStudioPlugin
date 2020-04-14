package com.nibiru.plugin.actions;


import com.intellij.credentialStore.Credentials;
import com.intellij.openapi.components.BaseComponent;
import com.nibiru.plugin.beans.LoginBean;
import com.nibiru.plugin.http.HttpManager;
import com.nibiru.plugin.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class MyApplicationComponent implements BaseComponent {

    public void initComponent() {
        if (NibiruConfig.isLogin) {
            return;
        }
        Credentials loginInfo = CredentialUtils.getString(CredentialUtils.LOGIN_INFO);
        if (loginInfo != null) {
            String userName = loginInfo.getUserName();
            String passwordAsString = loginInfo.getPasswordAsString();
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
    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        //Log.i("MyApplicationComponent getComponentName");
        return "myApplicationComponent";
    }
}