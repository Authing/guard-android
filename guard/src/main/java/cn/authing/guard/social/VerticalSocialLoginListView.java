package cn.authing.guard.social;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.ContinueWithTextView;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class VerticalSocialLoginListView extends LinearLayout {

    private LinearLayout socialLinearLayout;
    private boolean showSocialTitle;
    private String socialTitle;
    private String src;

    public VerticalSocialLoginListView(Context context) {
        this(context, null);
    }

    public VerticalSocialLoginListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSocialLoginListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VerticalSocialLoginListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalSocialLoginListView);
        showSocialTitle = array.getBoolean(R.styleable.VerticalSocialLoginListView_showSocialTitle, true);
        socialTitle = array.getString(R.styleable.VerticalSocialLoginListView_socialTitle);
        src = array.getString(R.styleable.VerticalSocialLoginListView_loginSource);
        if (TextUtils.isEmpty(src)) {
            src = "auto"; // auto means from console
        }
        array.recycle();

        initView();
    }

    public void initView() {
        removeAllViews();
        if (showSocialTitle) {
            ContinueWithTextView continueWithTextView = new ContinueWithTextView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            continueWithTextView.setLayoutParams(params);
            if (!TextUtils.isEmpty(socialTitle)) {
                continueWithTextView.setSocialTitle(socialTitle);
            }
            addView(continueWithTextView);
        }

        socialLinearLayout = new LinearLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        socialLinearLayout.setLayoutParams(layoutParams);
        socialLinearLayout.setOrientation(VERTICAL);
        socialLinearLayout.setGravity(Gravity.CENTER);
        addView(socialLinearLayout);

        if ("auto".equals(src)) {
            Authing.getPublicConfig((config -> {
                if (config == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                List<SocialConfig> socialConfigs = config.getSocialConfigs();
                for (int i = 0, n = socialConfigs.size(); i < n; i++) {
                    SocialConfig sc = socialConfigs.get(i);
                    parsSource(sb, sc);
                    if (i < n - 1) {
                        sb.append("|");
                    }
                }
                addSocialList(sb.toString());
            }));
        } else {
            addSocialList(src);
        }
    }

    private void parsSource(StringBuilder sb, SocialConfig sc) {
        String type = sc.getType();
        if (Const.EC_TYPE_WECHAT.equals(type)) {
            sb.append(Const.TYPE_WECHAT);
        } else if (Const.EC_TYPE_ALIPAY.equals(type)) {
            sb.append(Const.TYPE_ALIPAY);
        } else if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
            sb.append(Const.TYPE_WECHAT_COM);
        } else if (Const.EC_TYPE_WECHAT_COM_AGENCY.equals(type)) {
            sb.append(Const.TYPE_WECHAT_COM_AGENCY);
        } else if (Const.EC_TYPE_LARK_INTERNAL.equals(type)
                || Const.EC_TYPE_LARK_PUBLIC.equals(type)) {
            sb.append(Const.TYPE_LARK);
        } else if (Const.EC_TYPE_GOOGLE.equals(type)) {
            sb.append(Const.TYPE_GOOGLE);
        }
    }

    private void addSocialList(String src) {
        String[] sources = src.split("\\|");
        for (String source : sources) {
            String s = source.trim();
            if (TextUtils.isEmpty(s)) {
                continue;
            }

            LinearLayout linearLayout = getSocialListLayout();
            linearLayout.addView(getSocialButton(s));
            linearLayout.addView(getSocialTextView(s));
            socialLinearLayout.addView(linearLayout);
        }
    }

    private SocialLoginButton getSocialButton(String src) {
        SocialLoginButton button = null;
        switch (src) {
            case Const.TYPE_WECHAT:
                button = new WechatLoginButton(getContext());
                break;
            case Const.TYPE_ALIPAY:
                button = new AlipayLoginButton(getContext());
                break;
            case Const.TYPE_WECHAT_COM:
            case Const.TYPE_WECHAT_COM_AGENCY:
                button = new WeComLoginButton(getContext());
                button.setType(src);
                break;
            case Const.TYPE_LARK:
                button = new LarkLoginButton(getContext());
                break;
            case Const.TYPE_GOOGLE:
                button = new GoogleLoginButton(getContext());
                break;
        }
        if (button != null) {
            button.setBackgroundResource(0);
            setSocialButtonParams(button);
        }
        return button;
    }

    private void setSocialButtonParams(SocialLoginButton button) {
        int m = (int) Util.dp2px(getContext(), 90);
        int width = (int) Util.dp2px(getContext(), 44);
        LayoutParams lp = new LayoutParams(width, width);
        lp.setMargins(m, 0, 0, 0);
        button.setLayoutParams(lp);
    }

    private TextView getSocialTextView(String s) {
        TextView textView = new TextView(getContext());
        textView.setText(getSocialText(s));
        textView.setTextColor(getContext().getColor(R.color.authing_text_black));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(R.dimen.authing_text_small_size));
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) Util.dp2px(getContext(), 4), 0, 0, 0);
        textView.setLayoutParams(lp);
        return textView;
    }

    private String getSocialText(String src) {
        String str = null;
        switch (src) {
            case Const.TYPE_WECHAT:
                str = getContext().getString(R.string.authing_login_by_wechat);
                break;
            case Const.TYPE_ALIPAY:
                str = getContext().getString(R.string.authing_login_by_alipay);
                break;
            case Const.TYPE_WECHAT_COM:
            case Const.TYPE_WECHAT_COM_AGENCY:
                str = getContext().getString(R.string.authing_login_by_we_com);
                break;
            case Const.TYPE_LARK:
                str = getContext().getString(R.string.authing_login_by_lark);
                break;
            case Const.TYPE_GOOGLE:
                str = getContext().getString(R.string.authing_login_by_google);
                break;
        }
        return str;
    }

    private LinearLayout getSocialListLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.dp2px(getContext(), 44));
        params.topMargin = (int) Util.dp2px(getContext(), 12);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.authing_button_background_gray);
        linearLayout.setOnClickListener(v -> {
            if (v instanceof LinearLayout) {
                View childView = ((LinearLayout) v).getChildAt(0);
                if (childView != null) {
                    childView.performClick();
                }
            }
        });
        return linearLayout;
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            if (child instanceof SocialLoginButton) {
                ((SocialLoginButton) child).setOnLoginListener(callback);
            }
        }
    }

    public void setShowSocialTitle(boolean showSocialTitle) {
        this.showSocialTitle = showSocialTitle;
    }

    public void setSocialTitle(String socialTitle) {
        this.socialTitle = socialTitle;
    }

}