package cn.authing.guard.social.bind;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.authing.guard.R;

public class SocialBindContainer extends LinearLayout {

    private SocialBindType type;

    public SocialBindContainer(Context context) {
        this(context, null);
    }

    public SocialBindContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialBindContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SocialBindContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SocialBindContainer);
        int t = array.getInt(R.styleable.SocialBindContainer_type, 0);
        if (t == 0) {
            type = SocialBindType.EByPhoneCode;
        } else if (t == 1) {
            type = SocialBindType.EByAccountPassword;
        } else if (t == 4) {
            type = SocialBindType.EByEmailCode;
        }
        array.recycle();
    }

    public SocialBindType getType() {
        return type;
    }

    public enum SocialBindType {
        EByPhoneCode,
        EByAccountPassword,
        EByPhonePassword,
        EByEmailCode
    }
}
