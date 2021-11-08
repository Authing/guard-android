package cn.authing.guard;

import android.graphics.drawable.Drawable;

public class GlobalStyle {
    private static int sEditTextLayoutBackground;
    private static boolean isEditTextLayoutBackgroundSet;
    private static int sEditTextBackground;
    private static boolean isEditTextBackgroundSet;

    public static void clear() {
        isEditTextLayoutBackgroundSet = false;
        isEditTextBackgroundSet = false;
    }

    public static int getEditTextLayoutBackground() {
        return sEditTextLayoutBackground;
    }

    public static void setEditTextLayoutBackground(int sEditTextLayoutBackground) {
        GlobalStyle.sEditTextLayoutBackground = sEditTextLayoutBackground;
        isEditTextLayoutBackgroundSet = true;
    }

    public static boolean isIsEditTextLayoutBackgroundSet() {
        return isEditTextLayoutBackgroundSet;
    }

    public static int getEditTextBackground() {
        return sEditTextBackground;
    }

    public static void setEditTextBackground(int sEditTextBackground) {
        GlobalStyle.sEditTextBackground = sEditTextBackground;
        isEditTextBackgroundSet = true;
    }

    public static boolean isIsEditTextBackgroundSet() {
        return isEditTextBackgroundSet;
    }
}
