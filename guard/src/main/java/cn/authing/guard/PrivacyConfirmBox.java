package cn.authing.guard;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.text.HtmlCompat;

import java.util.List;
import java.util.Locale;

import cn.authing.guard.data.Agreement;
import cn.authing.guard.data.Config;

public class PrivacyConfirmBox extends LinearLayout {

    private boolean isRequired = true;
    private final AppCompatCheckBox checkBox;
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

        checkBox = new AppCompatCheckBox(context);
        addView(checkBox);

        Config config = Authing.getPublicConfig();
        if (config != null) {
            List<Agreement> agreements = config.getAgreements();
            if (agreements != null) {
                TextView textView = new TextView(context);
                addView(textView);
                String lang = Locale.getDefault().getLanguage();
                for (Agreement agreement : config.getAgreements()) {
                    if (agreement.getLang().startsWith(lang)) {
                        Spanned htmlAsSpanned = Html.fromHtml(agreement.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY);
                        textView.setText(htmlAsSpanned);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                        isRequired = agreement.isRequired();
                        break;
                    }
                }
            }
        }

        animShake = AnimationUtils.loadAnimation(context, R.anim.authing_shake);
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
