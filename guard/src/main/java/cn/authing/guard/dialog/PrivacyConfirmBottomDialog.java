package cn.authing.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class PrivacyConfirmBottomDialog extends Dialog {

    private PrivacyConfirmDialog.OnPrivacyListener onPrivacyListener;
    private TextView contentView;

    public PrivacyConfirmBottomDialog(@NonNull Context context) {
        super(context);
    }

    public PrivacyConfirmBottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public PrivacyConfirmBottomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(R.drawable.authing_social_more_dialog_background);
        getWindow().setGravity(Gravity.CENTER);
        Window window = getWindow();
        int padding = (int) Util.dp2px(getContext(), 24);
        window.getDecorView().setPadding(padding, padding, padding, padding);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialogAnimation);

        setContentView(R.layout.authing_privacy_confirm_botton_dialog);
        contentView = findViewById(R.id.content_text);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.agree_button).setOnClickListener(v -> {
            if (onPrivacyListener != null) {
                dismiss();
                onPrivacyListener.onAgree();
            }
        });
    }

    public void setContent(Spannable spannable) {
        if (contentView != null) {
            contentView.setText(spannable);
        }
    }

    public void setContent(CharSequence content) {
        if (contentView != null) {
            contentView.setText(content);
        }
    }

    public void setOnPrivacyListener(PrivacyConfirmDialog.OnPrivacyListener onPrivacyListener) {
        this.onPrivacyListener = onPrivacyListener;
    }
}
