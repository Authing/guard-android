package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Safe;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class RecoveryCodeEditText extends AccountEditText implements TextWatcher {

    public RecoveryCodeEditText(@NonNull Context context) {
        this(context, null);
    }

    public RecoveryCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecoveryCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "hint") == null) {
            getEditText().setHint(context.getString(R.string.authing_account_edit_text_hint) + context.getString(R.string.authing_recovery_code));
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RecoveryCodeEditText);
        boolean copyEnabled = array.getBoolean(R.styleable.RecoveryCodeEditText_copyEnabled, false);
        array.recycle();

        String recoveryCode = Safe.loadRecoveryCode();
        if (!TextUtils.isEmpty(recoveryCode)){
            getEditText().setText(recoveryCode);
            copyEnabled = true;
        }

        if (copyEnabled) {
            LinearLayout copyTouchArea = new LinearLayout(context);
            copyTouchArea.setOrientation(HORIZONTAL);
            copyTouchArea.setGravity(Gravity.CENTER_VERTICAL);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            copyTouchArea.setLayoutParams(lp);

            Drawable drawable = context.getDrawable(R.drawable.ic_authing_copy);
            int length = (int) Util.dp2px(context, 24);
            ImageView imageView = new ImageView(context);
            LayoutParams iconParam = new LayoutParams(length, length);
            int p = (int) Util.dp2px(context, 6);
            iconParam.setMargins(p, 0, p, 0);
            imageView.setLayoutParams(iconParam);
            imageView.setBackground(drawable);
            copyTouchArea.setOnClickListener(this::copy);
            copyTouchArea.addView(imageView);
            root.addView(copyTouchArea);
        }
    }

    @Override
    protected void syncData() {
        getEditText().setText("");
    }

    public void copy(View v) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("RecoveryCode", getText());
        clipboard.setPrimaryClip(clip);
        ToastUtil.showTop(getContext(), getContext().getString(R.string.authing_copied));
    }

    @Override
    protected void report() {
        Analyzer.report("RecoveryCodeEditText");
    }
}
