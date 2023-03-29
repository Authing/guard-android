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

    public static TOTPBindResult bind(Context context, String data) {
        TOTPBindResult result = new TOTPBindResult();
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
                String issuer = map.get("issuer");
                int digits = TOTPGenerator.CODE_DIGITS;
                if (null != digitsStr){
                    try {
                        digits = Integer.parseInt(digitsStr);
                    } catch (Exception ignored) {}
                }
                int period = TOTPGenerator.TIME_STEP;
                if (null != periodStr){
                    try {
                        period = Integer.parseInt(periodStr);
                    } catch (Exception ignored) {}
                }

                if(secret == null){
                    result.setMessage(context.getResources().getString(R.string.qr_exception));
                    return result;
                }

                TOTPEntity newTotp = new TOTPEntity();
                newTotp.setPath(path);
                if (path.contains(":")){
                    String[] pathArray = path.split(":");
                    newTotp.setApplication(pathArray.length > 0 ? pathArray[0] : "");
                    newTotp.setAccount(pathArray.length > 1 ? pathArray[1] : "");
                }
                newTotp.setSecret(secret);
                newTotp.setAlgorithm(algorithm);
                newTotp.setDigits(digits);
                newTotp.setPeriod(period);
                newTotp.setIssuer(issuer);

                DatabaseHelper db = new DatabaseHelper(context);
                TOTPEntity historyTotp = db.getOTP(path);
                if (null != historyTotp && path.equals(historyTotp.getPath())){
                    if (secret.equals(historyTotp.getSecret())){
                        result.setCode(TOTPBindResult.BIND_FAILURE);
                        result.setMessage(context.getResources().
                                getString(R.string.the_account_is_bound, historyTotp.getAccountDetail()));
                    }else {
                        result.setCode(TOTPBindResult.UPDATED_ACCOUNT);
                        result.setMessage(context.getResources().
                                getString(R.string.the_account_is_updated, historyTotp.getAccountDetail()));
                        newTotp.setUuid(historyTotp.getUuid());
                        newTotp.setApplication(historyTotp.getApplication());
                        newTotp.setAccount(historyTotp.getAccount());
                        updateTotp(context, newTotp);
                    }
                    return result;
                }

                db.addOTP(newTotp);
                result.setCode(TOTPBindResult.BIND_SUCCESS);
            }else {
                result.setMessage(context.getResources().getString(R.string.qr_exception));
            }
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
            result.setMessage(context.getResources().getString(R.string.qr_exception));
        }
        return result;
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

    public static void deleteTotp(Context context, TOTPEntity totp){
        DatabaseHelper db = new DatabaseHelper(context);
        db.deleteOTP(totp);
    }

    public static void addTotp(Context context, TOTPEntity totp){
        DatabaseHelper db = new DatabaseHelper(context);
        db.addOTP(totp);
    }

    public static void updateTotp(Context context, TOTPEntity totp){
        DatabaseHelper db = new DatabaseHelper(context);
        db.updateOTP(totp);
    }

}
