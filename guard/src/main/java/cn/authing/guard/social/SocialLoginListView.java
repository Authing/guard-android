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

public class SocialLoginListView extends LinearLayout {

    private LinearLayout socialLinearLayout;
    private boolean showSingleLine;
    private boolean showSocialTitle;
    private String socialTitle;
    private String src;

    public SocialLoginListView(Context context) {
        this(context, null);
    }

    public SocialLoginListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialLoginListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SocialLoginListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SocialLoginListView);
        showSingleLine = array.getBoolean(R.styleable.SocialLoginListView_showSingleLine, true);
        showSocialTitle = array.getBoolean(R.styleable.SocialLoginListView_showSocialTitle, true);
        socialTitle = array.getString(R.styleable.SocialLoginListView_socialTitle);
        src = array.getString(R.styleable.SocialLoginListView_loginSource);
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
        if (showSingleLine) {
            layoutParams.topMargin = (int) Util.dp2px(getContext(), 16);
        }
        socialLinearLayout.setLayoutParams(layoutParams);
        socialLinearLayout.setOrientation(showSingleLine ? HORIZONTAL : VERTICAL);
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
        if (showSingleLine) {
            setSingleList(src);
        } else {
            addMoreSocialList(src);
        }
    }

    private void setSingleList(String s) {
        String[] sources = s.split("\\|");
        int added = 0;
        for (String source : sources) {
            String src = source.trim();

            SocialLoginButton button;
            if (added == 3) {
                button = new MoreLoginButton(getContext());
            } else {
                button = getSocialButton(src);
            }

            if (button == null) {
                continue;
            }
            setSocialButtonParams(button);
            socialLinearLayout.addView(button);
            added++;
            if (added == 4) {
                break;
            }
        }
    }

    private void addMoreSocialList(String s) {
        String[] sources = s.split("\\|");
        LinearLayout linearLayout = getSocialListLayout();
        int added = 0;
        for (int i = 0; i < sources.length; i++) {
            String src = sources[i].trim();
            if (added == 4) {
                socialLinearLayout.addView(linearLayout);
                added = 0;
                linearLayout = getSocialListLayout();
            }

            LinearLayout itemLayout = getSocialItemLayout();
            SocialLoginButton button = getSocialButton(src);
            if (button == null) {
                continue;
            }
            setSocialButtonParams(button);
            itemLayout.addView(button);
            itemLayout.addView(getSocialItemTextView(src));
            linearLayout.addView(itemLayout);

            added++;
            if (i == sources.length - 1) {
                socialLinearLayout.addView(linearLayout);
            }
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
        return button;
    }

    private void setSocialButtonParams(SocialLoginButton button) {
        int length = (int) Util.dp2px(getContext(), 48);
        int m = (int) Util.dp2px(getContext(), 12.5f);
        LayoutParams lp = new LayoutParams(length, length);
        lp.setMargins(m, 0, m, 0);
        button.setLayoutParams(lp);
    }

    private LinearLayout getSocialListLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) Util.dp2px(getContext(), 16);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }

    private LinearLayout getSocialItemLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }

    private TextView getSocialItemTextView(String src) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = (int)Util.dp2px(getContext(), 4);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getColor(R.color.authing_text_gray));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(R.dimen.authing_text_small_size));
        String title = "";
        switch (src) {
            case Const.TYPE_WECHAT:
                title = getContext().getString(R.string.authing_social_wechat);
                break;
            case Const.TYPE_ALIPAY:
                title = getContext().getString(R.string.authing_social_alipay);
                break;
            case Const.TYPE_WECHAT_COM:
            case Const.TYPE_WECHAT_COM_AGENCY:
                title = getContext().getString(R.string.authing_social_we_com);
                break;
            case Const.TYPE_LARK:
                title = getContext().getString(R.string.authing_social_lark);
                break;
            case Const.TYPE_GOOGLE:
                title = getContext().getString(R.string.authing_social_google);
                break;
        }
        textView.setText(title);
        return textView;
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        if (socialLinearLayout == null) {
            return;
        }
        if (showSingleLine) {
            for (int i = 0; i < socialLinearLayout.getChildCount(); ++i) {
                View child = socialLinearLayout.getChildAt(i);
                if (child instanceof SocialLoginButton) {
                    ((SocialLoginButton) child).setOnLoginListener(callback);
                }
            }
        } else {
            for (int i = 0; i < socialLinearLayout.getChildCount(); ++i) {
                View child = socialLinearLayout.getChildAt(i);
                if (child instanceof LinearLayout) {
                    for (int j = 0; j < ((LinearLayout) child).getChildCount(); ++j) {
                        View socialItem = ((LinearLayout) child).getChildAt(j);
                        if (socialItem instanceof LinearLayout) {
                            View socialButton = ((LinearLayout) socialItem).getChildAt(0);
                            if (socialButton instanceof SocialLoginButton) {
                                ((SocialLoginButton) socialButton).setOnLoginListener(callback);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setShowSocialTitle(boolean showSocialTitle) {
        this.showSocialTitle = showSocialTitle;
    }

    public void setSocialTitle(String socialTitle) {
        this.socialTitle = socialTitle;
    }

    public void setShowSingleLine(boolean showSingleLine) {
        this.showSingleLine = showSingleLine;
    }
}
