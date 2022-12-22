package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class ContinueWithTextView extends LinearLayout {

    private TextView textView;

    public ContinueWithTextView(Context context) {
        this(context, null);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Authing.getPublicConfig(config -> {
            if (config == null || ((config.getSocialConfigs() == null || config.getSocialConfigs().size() == 0)
                    && (!config.isEnableFingerprintLogin()))) {
                setVisibility(View.GONE);
                return;
            }
            if (config.getSocialConfigs().size() == 1){
                SocialConfig socialConfig = config.getSocialConfigs().get(0);
                if (socialConfig != null && !TextUtils.isEmpty(socialConfig.getType())
                        && Const.EC_TYPE_YI_DUN.endsWith(socialConfig.getType())){
                    if (!config.isEnableFingerprintLogin()){
                        setVisibility(View.GONE);
                        return;
                    }
                }
            }

            setOrientation(LinearLayout.HORIZONTAL);
            setGravity(Gravity.CENTER);

            ImageView leftView = new ImageView(getContext());
            leftView.setBackgroundResource(R.drawable.authing_social_line_left);
            addView(leftView);

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContinueWithTextView);
            String text = array.getString(R.styleable.ContinueWithTextView_middleText);
            float textSize = array.getDimension(R.styleable.ContinueWithTextView_middleTextSize, Util.sp2px(context, 12));
            int textColor = array.getColor(R.styleable.ContinueWithTextView_middleTextColor, context.getColor(R.color.authing_text_gray));
            array.recycle();

            String defaultText = config.isEnableFingerprintLogin()
                    ? getContext().getString(R.string.authing_other_login)
                    : getContext().getString(R.string.authing_3rd_login);
            textView = new TextView(getContext());
            textView.setText(TextUtils.isEmpty(text) ? defaultText : text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setTextColor(textColor);
            textView.setPadding((int) Util.dp2px(context, 8), 0, (int) Util.dp2px(context, 8), 0);
            addView(textView);

            ImageView rightView = new ImageView(getContext());
            rightView.setBackgroundResource(R.drawable.authing_social_line_right);
            addView(rightView);
        });
    }

    public void setSocialTitle(String title){
        if (textView != null){
            textView.setText(title);
        }
    }
}
