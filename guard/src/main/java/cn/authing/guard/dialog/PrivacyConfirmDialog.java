package cn.authing.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;

public class PrivacyConfirmDialog extends Dialog {

    private OnItemClickListener onItemClickListener;
    private TextView contentView;

    public PrivacyConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public PrivacyConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public PrivacyConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_privacy_confirm_dialog);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.authing_privacy_confirm_dialog_background);

        contentView = findViewById(R.id.content_text);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            if (onItemClickListener != null) {
                dismiss();
                onItemClickListener.onCancelClick();
            }
        });

        findViewById(R.id.agree_button).setOnClickListener(v -> {
            if (onItemClickListener != null) {
                dismiss();
                onItemClickListener.onAgreeClick();
            }
        });
    }

    public void setContent(Spannable spannable) {
        if (contentView != null) {
            contentView.setText(spannable);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onCancelClick();

        void onAgreeClick();
    }

}
