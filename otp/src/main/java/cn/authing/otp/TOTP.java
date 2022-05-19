package cn.authing.otp;

import android.content.Context;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class TOTP {

    public static void bind(Context context, String data) {
        try {
            URI uri = new URI(data);
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            Map<String, String> map = splitQuery(uri);
            if (map != null) {
                String secret = map.get("secret");
                String algorithm = map.get("algorithm");
                String digitsStr = map.get("digits");
                String periodStr = map.get("period");
                int digits = TOTPUtils.CODE_DIGITS;
                if (null != digitsStr){
                    try {
                        digits = Integer.parseInt(digitsStr);
                    } catch (Exception ignored) {}
                }
                int period = TOTPUtils.TIME_STEP;
                if (null != periodStr){
                    try {
                        period = Integer.parseInt(periodStr);
                    } catch (Exception ignored) {}
                }
                String issuer = map.get("issuer");
                if (secret != null && issuer != null) {
                    TOTPEntity totp = new TOTPEntity();
                    totp.setAccount(path);
                    totp.setSecret(secret);
                    totp.setAlgorithm(algorithm);
                    totp.setDigits(digits);
                    totp.setPeriod(period);
                    totp.setIssuer(issuer);
                    DatabaseHelper db = new DatabaseHelper(context);
                    db.addOTP(totp);
                }
            }
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> splitQuery(URI url) throws UnsupportedEncodingException {
        final Map<String, String> queryPairs = new LinkedHashMap<>();
        final String query = url.getQuery();
        if (TextUtils.isEmpty(query)) {
            return null;
        }

        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    public static void delete(Context context, TOTPEntity totp){
        DatabaseHelper db = new DatabaseHelper(context);
        db.deleteOTP(totp);
    }
}
