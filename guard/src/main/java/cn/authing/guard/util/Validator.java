package cn.authing.guard.util;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPhoneNumber(CharSequence target) {
        return target != null && PhoneNumberUtils.isGlobalPhoneNumber(target.toString());
    }

    public static boolean hasEnglish(String s) {
        return hasLowerCase(s) || hasUpperCase(s);
    }

    public static boolean hasLowerCase(String s) {
        for (int i=0;i < s.length();i++) {
            char ch = s.charAt(i);
            if (Character.isLowerCase(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasUpperCase(String s) {
        for (int i=0;i < s.length();i++) {
            char ch = s.charAt(i);
            if (Character.isUpperCase(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNumber(String s) {
        for (int i=0;i < s.length();i++) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSpecialCharacter(String s) {
        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(s);
        return hasSpecial.find();
    }
}
