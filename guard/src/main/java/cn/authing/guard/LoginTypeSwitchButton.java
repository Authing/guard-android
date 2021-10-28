package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

public class LoginTypeSwitchButton extends AppCompatButton {

    private LoginContainer.LoginType targetLoginType = LoginContainer.LoginType.EByAccountPassword;

    public LoginTypeSwitchButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LoginTypeSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoginTypeSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setText(R.string.authing_login_by_account_and_password);
        setTextColor(context.getColor(R.color.authing_main));
        setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        setBackground(null);

        setOnClickListener((v -> {
            switchLoginType();
        }));
    }

    private void switchLoginType() {
        ViewGroup vg = (ViewGroup) getParent();
        for (int i = 0;i < vg.getChildCount();++i) {
            View v = vg.getChildAt(i);
            if (v instanceof LoginContainer) {
                LoginContainer container = (LoginContainer)v;

                if (targetLoginType == LoginContainer.LoginType.EByAccountPassword) {
                    if (container.getType() == LoginContainer.LoginType.EByAccountPassword) {
                        container.setVisibility(View.VISIBLE);
                    } else {
                        container.setVisibility(View.INVISIBLE);
                    }
                } else if (targetLoginType == LoginContainer.LoginType.EByPhoneCode) {
                    if (container.getType() == LoginContainer.LoginType.EByPhoneCode) {
                        container.setVisibility(View.VISIBLE);
                    } else {
                        container.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        if (targetLoginType == LoginContainer.LoginType.EByAccountPassword) {
            targetLoginType = LoginContainer.LoginType.EByPhoneCode;
            setText(R.string.authing_login_by_phone_and_verify_code);
        } else {
            targetLoginType = LoginContainer.LoginType.EByAccountPassword;
            setText(R.string.authing_login_by_account_and_password);
        }
    }
}
