package cn.authing.guard.social;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.social.view.QQLoginButton;
import cn.authing.guard.social.view.WeiboLoginButton;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class SocialLoginListView extends LinearLayout {

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

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SocialLoginListView);
        String src = array.getString(R.styleable.SocialLoginListView_loginSource);
        if (TextUtils.isEmpty(src)) {
            src = "auto"; // auto means from console
        }
        array.recycle();

        if ("auto".equals(src)) {
            Authing.getPublicConfig((config -> {
                if (config == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                List<SocialConfig> socialConfigs = config.getSocialConfigs();
                for (int i = 0, n = socialConfigs.size(); i < n; ++i) {
                    SocialConfig sc = socialConfigs.get(i);
                    parsSource(sb, sc);
                    if (i < n - 1) {
                        sb.append("|");
                    }
                }
                setup(context, sb.toString());
            }));
        } else {
            setup(context, src);
        }
    }

    private void parsSource(StringBuilder sb, SocialConfig sc){
        String type = sc.getType();
        if (Const.EC_TYPE_WECHAT.equals(type)) {
            sb.append("wechat");
        } else if (Const.EC_TYPE_ALIPAY.equals(type)) {
            sb.append("alipay");
        } else if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
            sb.append("wecom");
        } else if (Const.EC_TYPE_WECHAT_COM_AGENCY.equals(type)) {
            sb.append("wecom-agency");
        } else if (Const.EC_TYPE_LARK_INTERNAL.equals(type)
                || Const.EC_TYPE_LARK_PUBLIC.equals(type)) {
            sb.append("lark");
        } else if (Const.EC_TYPE_GOOGLE.equals(type)) {
            sb.append("google");
        } else if (Const.EC_TYPE_WECHAT_MINI_PROGRAM.equals(type)) {
            sb.append("wechat-mini-program");
        } else if (Const.EC_TYPE_FACE_BOOK.equals(type)) {
            sb.append("facebook");
        } else if (Const.EC_TYPE_QQ.equals(type)) {
            sb.append("qq");
        } else if (Const.EC_TYPE_WEIBO.equals(type)) {
            sb.append("weibo");
        }
    }

    private void setup(Context context, String s) {
        removeAllViews();
        String[] sources = s.split("\\|");
        for (String source : sources) {
            String type = source.trim();

            SocialLoginButton button = null;
            switch (type) {
                case "wechat":
                    button = new WechatLoginButton(context);
                    break;
                case "alipay":
                    button = new AlipayLoginButton(context);
                    break;
                case "wecom":
                case "wecom-agency":
                    button = new WeComLoginButton(context);
                    button.setType(type);
                    break;
                case "lark":
                    button = new LarkLoginButton(context);
                    break;
                case "google":
                    button = new GoogleLoginButton(context);
                    break;
                case "wechat-mini-program":
                    button = new WechatMiniProgramLoginButton(context);
                    break;
                case "facebook":
                    button = new FaceBookLoginButton(context);
                    break;
                case "qq":
                    button = new QQLoginButton(context);
                    break;
                case "weibo":
                    button = new WeiboLoginButton(context);
                    break;
            }

            if (button == null) {
                continue;
            }

            int length = (int) Util.dp2px(context, 44);
            int m = (int) Util.dp2px(context, 8);
            LayoutParams lp = new LayoutParams(length, length);
            lp.setMargins(m, 0, m, 0);
            button.setLayoutParams(lp);
            addView(button);
        }
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        for (int i = 0;i < getChildCount();++i) {
            View child = getChildAt(i);
            if (child instanceof SocialLoginButton) {
                ((SocialLoginButton)child).setOnLoginListener(callback);
            }
        }
    }

}
