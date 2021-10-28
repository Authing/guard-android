package cn.authing.guard;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.List;

import cn.authing.guard.data.Config;

public class AccountEditText extends ClearableEditText {

    private final static String LOGIN_METHOD_UN = "username-password";
    private final static String LOGIN_METHOD_EMAIL = "email-password";
    private final static String LOGIN_METHOD_PHONE = "phone-password";

    public AccountEditText(Context context) {
        super(context);
        init(context);
    }

    public AccountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
//        String s1 = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_marginStart");
//        if (s1 == null) {
//            ViewGroup.LayoutParams lp = getLayoutParams();
//            if (lp instanceof ViewGroup.MarginLayoutParams) {
//                ((ViewGroup.MarginLayoutParams) lp).setMarginStart(64);
//            }
//        }
//
//        String s2 = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_marginEnd");
//        TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{R.styleable.Layout_android_layout_marginStart});
//        float ml = ta.getInt(0, 0);
        init(context);
    }

    public AccountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        CharSequence s = getHint();
        if (s == null) {
            setHint(getHintByConfig(context));
        }
    }

    private String getHintByConfig(Context context) {
        String s = context.getString(R.string.account_edit_text_hint);
        String username = context.getString(R.string.authing_username);
        String email = context.getString(R.string.authing_email);
        String phone = context.getString(R.string.authing_phone);
        String defaultHint = s + username + "/" + email + "/" + phone;
        Config config = Authing.getPublicConfig();
        if (config == null) {
            return defaultHint;
        }
        List<String> enabledLoginMethods = config.getEnabledLoginMethods();
        if (enabledLoginMethods == null || enabledLoginMethods.size() == 0) {
            return defaultHint;
        }
        for (int i = 0, n = enabledLoginMethods.size();i < n;++i) {
            String opt = enabledLoginMethods.get(i);
            if (opt.equals(LOGIN_METHOD_UN)) {
                s += username;
            } else if (opt.equals(LOGIN_METHOD_EMAIL)) {
                s += email;
            } else if (opt.equals(LOGIN_METHOD_PHONE)) {
                s += phone;
            }
            if (i < n - 1) {
                s +="/";
            }
        }
        return  s;
    }
}
