package jp.co.stream.vrresponse;

import android.content.Context;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AESの暗号化・復号化をする
 * Created by Yamamoto Keita on 2017/01/23.
 */
class AESUtil {

    private static final String ALGORITHM = "AES";
    // PKCS7Paddingは環境によっては使えないので、問題が出た場合はPKCS5Paddingに変える
    // PKCS5はPKCS7の別名で動作は変わらない。Java的にはPKCS5の方が正式名称っぽい
    private static final String ECB_METHOD = "AES/ECB/PKCS7Padding";
    private static final String CHARSET = "UTF-8";

    /**
     * CKの暗号化
     *
     * @param context Applicationコンテキスト
     * @param str     生CK
     * @return 暗号化CK
     */
    public static String encryptCK(Context context, String str) {
        String aesKey = context.getString(R.string.aes_key_phrase_check);
        byte[] encrypted;
        try {
            encrypted = AESUtil.encryptECB(str.getBytes(), aesKey);
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECBモードで暗号化する
     *
     * @param src       バイト列
     * @param keyString 鍵
     * @return 暗号化データ
     */
    private static byte[] encryptECB(byte[] src, String keyString) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_METHOD);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyString.getBytes(CHARSET), ALGORITHM));
        return cipher.doFinal(src);
    }

    /**
     * ECBモードで復号化する
     *
     * @param src       バイト列
     * @param keyString 鍵
     * @return 復号化データ
     */
    private static byte[] decryptECB(byte[] src, String keyString) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_METHOD);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyString.getBytes(CHARSET), ALGORITHM));
        return cipher.doFinal(src);
    }
}
