package com.nibiru.plugin.utils;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class CredentialUtils {
    public static final String LOGIN_INFO = "loginInfo";
    public static Credentials getString(String key) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(key);
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        if (credentials != null) {
            return credentials;
        }
        return null;
    }

    public static void putString(String key, String userName, String pwd) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(key); // see previous sample
        Credentials credentials = new Credentials(userName, pwd);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    public static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("MySystem", key));
    }
}
