package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class RegisterContainer extends LinearLayout {

    public enum RegisterType {
        EByPhoneCodePassword,
        EByEmailPassword,
    }

    private RegisterType type;

    public RegisterContainer(Context context) {
        this(context, null);
    }

    public RegisterContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RegisterContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RegisterContainer);
        int t = array.getInt(R.styleable.RegisterContainer_type,2);
        if (t == 2) {
            type = RegisterType.EByPhoneCodePassword;
        } else if (t == 3) {
            type = RegisterType.EByEmailPassword;
        }
        array.recycle();
    }

    public RegisterType getType() {
        return type;
    }
}
