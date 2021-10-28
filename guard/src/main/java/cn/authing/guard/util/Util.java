package cn.authing.guard.util;

import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class Util {

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xKeUgQ+Aoz7TLfAfs9+paePb5KIofVthEopwrXFkp8OCeocaTHt9ICjTT2QeJh6cZaDaArfZ873GPUn00eOIZ7Ae+TiA2BKHbCvloW3w5Lnqm70iSsUi5Fmu9/2+68GZRH9L7Mlh8cFksCicW2Y2W2uMGKl64GDcIq3au+aqJQIDAQAB";

    public static float dp2px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float px2dp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static String encryptPassword(String password) {
        try {
            byte[] keyBytes = Base64.decode(publicKey, Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherMsg = cipher.doFinal(password.getBytes());
            return new String(Base64.encode(cipherMsg, Base64.NO_WRAP));
        } catch (Exception e) {
            return "{\"2020\":\"" + e + "\"}";
        }
    }

    public static View findViewByClass(View current, Class T) {
        View view = current.getRootView();
        return _findViewByClass((ViewGroup)view, T);
    }

    private static View _findViewByClass(ViewGroup parent, Class T) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                View result = _findViewByClass((ViewGroup)child, T);
                if (result != null) {
                    return result;
                }
            }

            if (child.getClass().equals(T)) {
                return child;
            }
        }
        return null;
    }
}
