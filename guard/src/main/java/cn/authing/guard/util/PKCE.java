package cn.authing.guard.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PKCE {

    public static final String CODE_CHALLENGE_METHOD_S256 = "S256";
    public static final String CODE_CHALLENGE_METHOD_PLAIN = "plain";

    public static String getCodeChallengeMethod() {
        try {
            MessageDigest.getInstance("SHA-256");
            return CODE_CHALLENGE_METHOD_S256;
        } catch (NoSuchAlgorithmException e) {
            return CODE_CHALLENGE_METHOD_PLAIN;
        }
    }

    public static String generateCodeVerifier() {
        return Util.randomString(43);
    }

    // save verifier at app side for token exchange
    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest sha256Digester = MessageDigest.getInstance("SHA-256");
            sha256Digester.update(codeVerifier.getBytes("ISO_8859_1"));
            byte[] digestBytes = sha256Digester.digest();
            return Base64.encodeToString(digestBytes, Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE);
        } catch (NoSuchAlgorithmException e) {
            ALog.w("PKCE", "SHA-256 is not supported on this device! Using plain challenge", e);
            return codeVerifier;
        } catch (UnsupportedEncodingException e) {
            ALog.e("PKCE", "ISO-8859-1 encoding not supported on this device!", e);
            throw new IllegalStateException("ISO-8859-1 encoding not supported", e);
        }
    }
}
