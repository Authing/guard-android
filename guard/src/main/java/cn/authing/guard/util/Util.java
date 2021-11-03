package cn.authing.guard.util;

import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Integer> intDigits(int i) {
        int temp = i;
        ArrayList<Integer> array = new ArrayList<Integer>();
        do{
            array.add(0, temp % 10);
            temp /= 10;
        } while  (temp > 0);
        return array;
    }

    public static List<View> findAllViewByClass(View current, Class T) {
        View view = current.getRootView();
        List<View> result = new ArrayList<>();
        _findAllViewByClass((ViewGroup)view, T, result);
        return result;
    }

    private static void _findAllViewByClass(ViewGroup parent, Class T, List<View> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                _findAllViewByClass((ViewGroup)child, T, result);
            }

            if (child.getClass().equals(T)) {
                result.add(child);
            }
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
