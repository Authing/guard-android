package cn.authing.guard.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.authing.guard.R;

public class AuthContainer extends LinearLayout {

    public enum AuthProtocol {
        EInHouse,
        EOIDC
    }

    private AuthProtocol authProtocol = AuthProtocol.EInHouse;

    public AuthContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AuthContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AuthContainer);
        int p = array.getInt(R.styleable.AuthContainer_authProtocol,0);
        if (p == 0) {
            authProtocol = AuthProtocol.EInHouse;
        } else if (p == 1) {
            authProtocol = AuthProtocol.EOIDC;
        }
        array.recycle();
    }

    public AuthProtocol getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(AuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }
}
