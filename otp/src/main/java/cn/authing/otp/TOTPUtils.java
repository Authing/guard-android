package cn.authing.otp;

import java.nio.ByteBuffer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TOTPUtils {

    public static final int TIME_STEP = 30;
    public static final int CODE_DIGITS = 6;

    public static String getQRCodeStr(String user, String secret) {
        String format = "otpauth://totp/%s?secret=%s";
        return String.format(format, user, secret);
    }

    public static String generateTOTP(String secret) {
        return generateTOTP(secret, getCurrentInterval(), CODE_DIGITS);
    }

    public static String generateTOTP(String secret, int codeDigits) {
        return generateTOTP(secret, getCurrentInterval(), codeDigits);
    }

    public static String generateTOTP(String secret, int period, int codeDigits) {
        return generateTOTP(secret, getCurrentInterval(period), codeDigits);
    }

    public static boolean verify(String secret, String code) {
        return verify(secret, code, CODE_DIGITS);
    }

    public static boolean verify(String secret, String code, int codeDigits) {
        long currentInterval = getCurrentInterval();
        for (int i = 0; i <= 1; i++) {
            String tmpCode = generateTOTP(secret, currentInterval - i, codeDigits);
            if (tmpCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static int getRemainingSeconds() {
        return TIME_STEP - (int) (System.currentTimeMillis() / 1000 % TIME_STEP);
    }

    public static int getRemainingMilliSeconds() {
        return TIME_STEP * 1000 - (int) (System.currentTimeMillis() % (TIME_STEP * 1000));
    }

    private static String generateTOTP(String secret, long currentInterval, int codeDigits) {
        if (codeDigits < 1 || codeDigits > 18) {
            throw new UnsupportedOperationException("不支持" + codeDigits + "位数的动态口令");
        }
        byte[] content = ByteBuffer.allocate(8).putLong(currentInterval).array();
        byte[] hash = hmacsha("HmacSHA1", content, secret);
        if(hash == null){
            return "";
        }
        int offset = hash[hash.length - 1] & 0xf;
        int binary =
                ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);
        long digitsPower = Long.parseLong(rightPadding("1", codeDigits + 1));
        long code = binary % digitsPower;
        return leftPadding(Long.toString(code), codeDigits);
    }

    private static long getCurrentInterval() {
        return System.currentTimeMillis() / 1000 / TIME_STEP;
    }

    private static long getCurrentInterval(int period) {
        return System.currentTimeMillis() / 1000 / period;
    }

    private static String leftPadding(String value, int length) {
        while (value.length() < length) {
            value = "0" + value;
        }
        return value;
    }

    private static String rightPadding(String value, int length) {
        while (value.length() < length) {
            value = value + "0";
        }
        return value;
    }

    private static byte[] hmacsha(String crypto, byte[] content, String key) {
        try {
            byte[] byteKey = Base32.decode(key);
            Mac hmac = Mac.getInstance(crypto);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, crypto);
            hmac.init(keySpec);
            return hmac.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
