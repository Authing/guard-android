package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;

public class LoginContainer extends LinearLayout {

    public enum LoginType {
        EByPhoneCode,
        EByAccountPassword,
        EByPhonePassword,
        EByEmailCode
    }

    private LoginType type;

    public LoginContainer(Context context) {
        this(context, null);
    }

    public LoginContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LoginContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoginContainer);
        int t = array.getInt(R.styleable.LoginContainer_type,0);
        if (t == 0) {
            type = LoginType.EByPhoneCode;
        } else if (t == 1) {
            type = LoginType.EByAccountPassword;
        }
        array.recycle();
    }

    public LoginType getType() {
        return type;
    }
}
