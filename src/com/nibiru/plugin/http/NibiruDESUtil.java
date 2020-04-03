package com.nibiru.plugin.http;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class NibiruDESUtil {

    public final static String DEFAULT_KEY_STR = "zi92_oim9_";
    private final static String DES = "DES";

    /**
     * 根据参数生成 KEY
     */
    private static Key getKey(String strKey) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            DESKeySpec keySpec = new DESKeySpec(strKey.getBytes());
            keyFactory.generateSecret(keySpec);
            return keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        }
    }

    /**
     * 加密 String 明文输入 ,String 密文输出
     */
    public static String encryptStr(String strMing, String key) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        try {
            byteMing = strMing.getBytes("UTF8");
            byteMi = encryptByte(byteMing, key);
            strMi = Base64.encodeToString(byteMi, Base64.DEFAULT).trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteMing = null;
            byteMi = null;
        }
        if (!key.equals(DEFAULT_KEY_STR)){
            return afterEncode(strMi);
        }else {
            return strMi;
        }
    }

    /**
     * 解密 以 String 密文输入 ,String 明文输出
     *
     * @param strMi
     * @return
     */
    public static String decryptStr(String strMi, String key) {
        if (!key.equals(DEFAULT_KEY_STR)){
            strMi = beforeDecode(strMi);
        }
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = Base64.decode(strMi, Base64.DEFAULT);
            byteMing = decryptByte(byteMi, key);
            strMing = new String(byteMing, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以 byte[] 明文输入 ,byte[] 密文输出
     *
     * @param byteS
     * @return
     */
    public static byte[] encryptByte(byte[] byteS, String key) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以 byte[] 密文输入 , 以 byte[] 明文输出
     *
     * @param byteD
     * @return
     */
    public static byte[] decryptByte(byte[] byteD, String key) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.DECRYPT_MODE, getKey(key));
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 文件 file 进行加密并保存目标文件 destFile 中
     *
     * @param file     要加密的文件 如 c:/test/srcFile.txt
     * @param destFile 加密后存放的文件名 如 c:/ 加密后文件 .txt
     */
    public static void encryptFile(String file, String destFile) throws Exception {
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(DEFAULT_KEY_STR));
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(destFile);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();
    }

    /**
     * 文件采用 DES 算法解密文件
     *
     * @param file 已加密的文件 如 c:/ 加密后文件 .txt *
     * @param dest 解密后存放的文件名 如 c:/ test/ 解密后文件 .txt
     */
    public static void decryptFile(String file, String dest) throws Exception {
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, getKey(DEFAULT_KEY_STR));
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = is.read(buffer)) >= 0) {
            cos.write(buffer, 0, r);
        }
        cos.close();
        out.close();
        is.close();
    }

	private static String afterEncode(String input) {
		return input.replace("+", "_").replace("/", "*").replace("=", ".");
	}

	private static String beforeDecode(String input) {
		return input.replace("_", "+").replace("*", "/").replace(".", "=");
	}
}