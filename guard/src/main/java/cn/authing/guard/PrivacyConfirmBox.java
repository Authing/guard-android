package cn.authing.guard;

import static cn.authing.guard.util.Util.getThemeAccentColor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import java.util.List;
import java.util.Locale;

import cn.authing.guard.activity.WebActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Agreement;
import cn.authing.guard.data.Safe;
import cn.authing.guard.dialog.PrivacyConfirmBottomDialog;
import cn.authing.guard.dialog.PrivacyConfirmDialog;
import cn.authing.guard.internal.CustomURLSpan;
import cn.authing.guard.util.Util;

public class PrivacyConfirmBox extends LinearLayout {

    private boolean isRequired;
    private final CheckBox checkBox;
    private final TextView textView;
    private Spannable spannable;
    private PrivacyConfirmDialog dialog;
    private PrivacyConfirmBottomDialog bottomDialog;
    private final String dialogMessage;
    private boolean isRememberSate;

    public PrivacyConfirmBox(Context context) {
        this(context, null);
    }

    public PrivacyConfirmBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrivacyConfirmBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PrivacyConfirmBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Analyzer.report("PrivacyConfirmBox");

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        checkBox = new CheckBox(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        checkBox.setLayoutParams(params);
        addView(checkBox);

        textView = new TextView(context);
        textView.setIncludeFontPadding(false);
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        textView.setPadding((int) Util.dp2px(getContext(), 9), 0, 0, 6);
        addView(textView);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PrivacyConfirmBox);
        textView.setTextColor(array.getColor(R.styleable.PrivacyConfirmBox_android_textColor, context.getColor(R.color.authing_text_gray)));
        String text = array.getString(R.styleable.PrivacyConfirmBox_android_text);
        float textSize = array.getDimension(R.styleable.PrivacyConfirmBox_android_textSize, Util.sp2px(context, 12));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int linkColor = array.getColor(R.styleable.PrivacyConfirmBox_linkTextColor, context.getColor(R.color.authing_text_black));
        int uncheckColor = array.getColor(R.styleable.PrivacyConfirmBox_uncheckedColor, getThemeAccentColor(context));
        int checkColor = array.getColor(R.styleable.PrivacyConfirmBox_uncheckedColor, getThemeAccentColor(context));
        boolean round = array.getBoolean(R.styleable.PrivacyConfirmBox_isRound, false);
        boolean isShowInternal = array.getBoolean(R.styleable.PrivacyConfirmBox_isShowInternal, true);
        isRememberSate = array.getBoolean(R.styleable.PrivacyConfirmBox_isRememberSate, false);
        dialogMessage = array.getString(R.styleable.PrivacyConfirmBox_dialogMessage);
        Drawable checkBoxDrawable = array.getDrawable(R.styleable.PrivacyConfirmBox_button);
        array.recycle();

        if (checkBoxDrawable != null) {
            checkBox.setButtonDrawable(checkBoxDrawable);
        } else if (round) {
            checkBox.setButtonDrawable(R.drawable.authing_round_checkbox);
        } else {
            ColorStateList colorStateList = new ColorStateList(new int[][]{
                    new int[]{-android.R.attr.state_checked},
                    new int[]{android.R.attr.state_checked}},
                    new int[]{uncheckColor, checkColor});
            checkBox.setButtonTintList(colorStateList);
        }
        refreshCheckBoxState();


        setVisibility(View.GONE);
        post(() -> Authing.getPublicConfig((config -> {
            if (config == null) {
                return;
            }

            List<Agreement> agreements = config.getAgreements();
            if (agreements == null || agreements.size() == 0) {
                return;
            }

            int pageType = -1;
            View v = Util.findViewByClass(this, LoginButton.class);
            if (v != null) {
                pageType = 1;
            }
            v = Util.findViewByClass(this, RegisterButton.class);
            if (v != null) {
                pageType = 0;
            }
            boolean show = false;
            String lang = Locale.getDefault().getLanguage();
            for (Agreement agreement : config.getAgreements()) {
                if (agreement.getLang().startsWith(lang)
                        && (pageType == -1
                        || (pageType == 0 && agreement.isShowAtRegister())
                        || (pageType == 1 && agreement.isShowAtLogin()))) {
                    Spanned htmlAsSpanned = Html.fromHtml(agreement.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY);

                    spannable = new SpannableString(removeTrailingLineBreak(htmlAsSpanned));

                    URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
                    for (URLSpan span : spans) {
                        int start = spannable.getSpanStart(span);
                        int end = spannable.getSpanEnd(span);
                        CustomURLSpan customURLSpan = new CustomURLSpan(span.getURL(), linkColor);
                        ClickableSpan clickable = null;
                        if (isShowInternal) {
                            clickable = new ClickableSpan() {
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext().getApplicationContext(), WebActivity.class);
                                    intent.putExtra("title", spannable.toString().subSequence(start, end));
                                    intent.putExtra("url", customURLSpan.getURL());
                                    getContext().startActivity(intent);
                                }
                            };
                        }
                        spannable.removeSpan(span);
                        if (clickable != null) {
                            spannable.setSpan(clickable, start, end, 0);
                        }
                        spannable.setSpan(customURLSpan, start, end, 0);
                    }
                    textView.setText(spannable);

                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    isRequired = agreement.isRequired();
                    show = true;
                    break;
                }
            }

            if (show) {
                setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(text)) {
                    textView.setText(text);
                }
            }
        })));
    }

    public void setRememberSate(boolean rememberSate) {
        isRememberSate = rememberSate;
        refreshCheckBoxState();
    }

    private void refreshCheckBoxState() {
        if (isRememberSate) {
            checkBox.setChecked(Safe.loadPrivacyConfirmState());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> Safe.savePrivacyConfirmState(isChecked));
        }
    }

    public void setButtonDrawable(int resId) {
        checkBox.setButtonDrawable(resId);
    }

    private CharSequence removeTrailingLineBreak(CharSequence text) {
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public boolean require(PrivacyConfirmDialog.OnPrivacyListener listener) {
        if (isRequired && !checkBox.isChecked()) {
            showBottomDialog(listener);
            return true;
        }
        return false;
    }

    public void showDialog(PrivacyConfirmDialog.OnPrivacyListener listener) {
        if (dialog == null) {
            dialog = new PrivacyConfirmDialog(getContext());
        }
        dialog.setOnPrivacyListener(new PrivacyConfirmDialog.OnPrivacyListener() {

            @Override
            public void onShow() {

            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }

            @Override
            public void onAgree() {
                checkBox.setChecked(true);
                if (isRememberSate) {
                    Safe.savePrivacyConfirmState(true);
                }
                if (listener != null) {
                    listener.onAgree();
                }
            }
        });
        dialog.show();
        dialog.setContent(TextUtils.isEmpty(dialogMessage) ? spannable : new SpannableString(dialogMessage));
    }

    public void showBottomDialog(PrivacyConfirmDialog.OnPrivacyListener listener) {
        if (bottomDialog == null) {
            bottomDialog = new PrivacyConfirmBottomDialog(getContext());
        }
        bottomDialog.setOnPrivacyListener(new PrivacyConfirmDialog.OnPrivacyListener() {

            @Override
            public void onShow() {

            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }

            @Override
            public void onAgree() {
                checkBox.setChecked(true);
                if (isRememberSate) {
                    Safe.savePrivacyConfirmState(true);
                }
                if (listener != null) {
                    listener.onAgree();
                }
            }
        });
        bottomDialog.show();
        bottomDialog.setContent(TextUtils.isEmpty(dialogMessage) ? spannable : new SpannableString(dialogMessage));
        bottomDialog.setOnDismissListener(dialog -> {
            if (listener != null) {
                listener.onCancel();
            }
        });
        if (listener != null) {
            listener.onShow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }
}
