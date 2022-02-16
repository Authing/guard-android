package cn.authing.guard.jwt;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Jwt {
    private String header;
    private String payload;
    private String signature;

    public Jwt(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length == 3) {
            header = new String(Base64.decode(parts[0], Base64.URL_SAFE), StandardCharsets.UTF_8);
            payload = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
            signature = parts[2];
        }
    }

    public String getHeader() {
        return header;
    }

    public String getPayload() {
        return payload;
    }

    public String getSignature() {
        return signature;
    }
}
