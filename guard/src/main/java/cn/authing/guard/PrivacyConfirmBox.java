package cn.authing.guard;

import static cn.authing.guard.util.Util.getThemeAccentColor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import java.util.List;
import java.util.Locale;

import cn.authing.guard.data.Agreement;
import cn.authing.guard.internal.CustomURLSpan;

public class PrivacyConfirmBox extends LinearLayout {

    private boolean isRequired;
    private final CheckBox checkBox;
    private final TextView textView;
    private final Animation animShake;

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

        checkBox = new CheckBox(context);
        addView(checkBox);

        textView = new TextView(context);
        addView(textView);

        animShake = AnimationUtils.loadAnimation(context, R.anim.authing_shake);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PrivacyConfirmBox);
        textView.setTextColor(array.getColor(R.styleable.PrivacyConfirmBox_android_textColor, 0xff000000));
        String text = array.getString(R.styleable.PrivacyConfirmBox_android_text);
        float textSize = array.getDimension(R.styleable.PrivacyConfirmBox_android_textSize, 14);
        textView.setTextSize(textSize);
        int linkColor = array.getColor(R.styleable.PrivacyConfirmBox_linkTextColor, 0xff396aff);
        int uncheckColor = array.getColor(R.styleable.PrivacyConfirmBox_uncheckedColor, getThemeAccentColor(context));
        int checkColor = array.getColor(R.styleable.PrivacyConfirmBox_uncheckedColor, getThemeAccentColor(context));
        boolean round = array.getBoolean(R.styleable.PrivacyConfirmBox_isRound, false);
        Drawable checkBoxDrawable = array.getDrawable(R.styleable.PrivacyConfirmBox_button);
        array.recycle();

        if (checkBoxDrawable != null) {
            checkBox.setButtonDrawable(checkBoxDrawable);
        } else if (round) {
            checkBox.setButtonDrawable(R.drawable.authing_round_checkbox);
        }
        ColorStateList colorStateList = new ColorStateList(new int[][] {
                new int[] { -android.R.attr.state_checked },
                new int[] { android.R.attr.state_checked } },
                new int[] { uncheckColor, checkColor });
        checkBox.setButtonTintList(colorStateList);

        Authing.getPublicConfig((config -> {
            if (config == null) {
                return;
            }

            List<Agreement> agreements = config.getAgreements();
            if (agreements != null && agreements.size() > 0) {
                String lang = Locale.getDefault().getLanguage();
                for (Agreement agreement : config.getAgreements()) {
                    if (agreement.getLang().startsWith(lang)) {
                        Spanned htmlAsSpanned = Html.fromHtml(agreement.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY);

                        Spannable s = new SpannableString(htmlAsSpanned);

                        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
                        for (URLSpan span: spans) {
                            int start = s.getSpanStart(span);
                            int end = s.getSpanEnd(span);
                            s.removeSpan(span);
                            span = new CustomURLSpan(span.getURL(), linkColor);
                            s.setSpan(span, start, end, 0);
                        }
                        textView.setText(s);

                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                        isRequired = agreement.isRequired();
                        break;
                    }
                }
            } else {
                setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(text)) {
                textView.setText(text);
            }
        }));
    }

    public boolean require(boolean shake) {
        if (isRequired && !checkBox.isChecked()) {
            if (shake) {
                startAnimation(animShake);
            }
            return true;
        }
        return false;
    }
}
