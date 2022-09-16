package cn.authing.guard.oneclick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.LoginListener;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.TitleLayout;
import cn.authing.guard.data.Agreement;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ImageLoader;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.dialog.PrivacyConfirmBottomDialog;
import cn.authing.guard.dialog.PrivacyConfirmDialog;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.ContinueWithTextView;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.social.SocialLoginListView;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class OneClick extends SocialAuthenticator implements Serializable {

    private static final String TAG = "OneClick";
    private static final int MSG_LOGIN = 1;

    public static String bizId;

    private final Context context;
    private final Handler handler;
    private UnifyUiConfig uiConfig;
    private AuthCallback<UserInfo> callback;
    private PrivacyConfirmBottomDialog privacyDialog;
    protected AnimatedVectorDrawable loadingDrawable;

    private int screenWidth; // dp

    public OneClick(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_LOGIN)
                    startLogin(uiConfig, callback);
            }
        };
    }

    public void start(@NotNull AuthCallback<UserInfo> callback) {
        start(bizId, null, callback);
    }

    public void start(UnifyUiConfig config, @NotNull AuthCallback<UserInfo> callback) {
        start(bizId, config, callback);
    }

    public void start(String bid, UnifyUiConfig uiConfig, @NotNull AuthCallback<UserInfo> callback) {
        String _bid = TextUtils.isEmpty(bid) ? bizId : bid;
        this.uiConfig = uiConfig;
        this.callback = callback;

        getAndroidScreenProperty();
        if (Authing.isConfigEmpty()) {
            if (_bid == null) {
                callback.call(500, "businessId error", null);
            } else {
                prefetchMobileNumber(_bid, null, null);
            }
            return;
        }

        Authing.getPublicConfig(config -> {
            prefetchMobileNumber(_bid, config, null);
        });
    }

    public void getPhoneNumber(@NotNull AuthCallback<String> callback) {
        getAndroidScreenProperty();
        if (Authing.isConfigEmpty()) {
            callback.call(500, "businessId error", null);
            return;
        }

        Authing.getPublicConfig(config -> {
            prefetchMobileNumber(null, config, callback);
        });
    }

    private void prefetchMobileNumber(String _bid, Config config, AuthCallback<String> callBack) {
        String businessId = (_bid != null) ? _bid : config.getSocialBusinessId(Const.EC_TYPE_YI_DUN);
        QuickLogin.getInstance().init(context, businessId);
        QuickLogin.getInstance().setPrefetchNumberTimeout(3);
        QuickLogin.getInstance().prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                //预取号成功
                ALog.d(TAG, "Got phone:" + mobileNumber);
                if (callBack == null) {
                    handler.sendEmptyMessage(MSG_LOGIN);
                } else {
                    callBack.call(200, "", mobileNumber);
                }
            }

            @Override
            public void onGetMobileNumberError(String YDToken, String msg) {
                ALog.e(TAG, "Got phone error:" + msg);
                callback.call(500, context.getString(R.string.authing_get_phone_failed), null);
            }
        });
    }

    public void startLogin(@NotNull AuthCallback<UserInfo> callback) {
        startLogin(null, callback);
    }

    private void startLogin(UnifyUiConfig uiConfig, @NotNull AuthCallback<UserInfo> callback) {
        this.uiConfig = uiConfig;
        this.callback = callback;
        if (uiConfig != null) {
            QuickLogin.getInstance().setUnifyUiConfig(uiConfig);
            startOnePass();
            return;
        }

        Authing.getPublicConfig((config) -> {
            if (config == null) {
                return;
            }

            String url = config.getLogo();
            ImageLoader.with(context).execute(url, (ok, result) -> {
                config(result);
                startOnePass();
            });
        });
    }

    private void startOnePass() {
        QuickLogin.getInstance().onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String YDToken, String accessCode) {
                //一键登录成功 运营商token：accessCode获取成功
                //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                ALog.d(TAG, "onGetTokenSuccess");
                authingLogin(YDToken, accessCode);
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {
                quit();
                ALog.e(TAG, "onGetTokenError:" + msg);
                callback.call(500, context.getString(R.string.authing_get_auth_code_failed), null);
            }

            @Override
            public void onCancelGetToken() {
                callback.call(201, "cancel", null);
            }
        });
    }

    private void authingLogin(String t, String ac) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByOneAuth(t, ac, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByOneAuth(t, ac, this::fireCallback);
        }
    }

    private void fireCallback(int code, String message, UserInfo userInfo) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (callback != null) {
                callback.call(code, message, userInfo);
            }
            quit();
        });
    }

    private void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        //px
        int width = dm.widthPixels;         // 屏幕宽度（像素）
//        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width / density);  // 屏幕宽度(dp)
//        int screenHeight = (int) (height / density);// 屏幕高度(dp)
//
//        ALog.d(TAG, "屏幕宽度（像素）：" + width);
//        ALog.d(TAG, "屏幕高度（像素）：" + height);
//        ALog.d(TAG, "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
//        ALog.d(TAG, "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
//        ALog.d(TAG, "屏幕宽度（dp）：" + screenWidth);
//        ALog.d(TAG, "屏幕高度（dp）：" + screenHeight);
    }

    private void config(Drawable logo) {
        TitleLayout titleLayout = inflateTitleLayout();
        RelativeLayout otherLoginRel = inflateOtherLayout();
        //LinearLayout socialRel = inflateSocialLayout();

        UnifyUiConfig.Builder builder = new UnifyUiConfig.Builder()
                .setStatusBarDarkColor(true)
                .setHideNavigation(true)
                .setLogoIconDrawable(logo)
                .setLogoTopYOffset(160)
                .setLogoHeight(52)
                .setLogoWidth(46)
                .setSloganColor(0)
                .setMaskNumberTopYOffset(233)//160+52+21=233
                .setSloganTopYOffset(265)//160+52+21+24+8=265
                .setPrivacyTopYOffset(263)
                .setPrivacyMarginLeft(24)
                .setPrivacyMarginRight(24)
                .setPrivacyLineSpacing(1, 1)
                .setPrivacyTextGravityCenter(false)
                .setCheckBoxGravity(Gravity.TOP)
                .setPrivacyState(false)
                .setPrivacyCheckBoxWidth(14)
                .setPrivacyCheckBoxHeight(14)
                .setCheckedImageName("ic_authing_checkbox_selected") // 设置隐私栏复选框选中时的图片资源
                .setUnCheckedImageName("ic_authing_checkbox_normal") // 设置隐私栏复选框未选中时的图片资源
                .setPrivacyTextMarginLeft(9)
                .setPrivacyTextEnd("") // 设置隐私栏声明部分尾部文案
                .setPrivacyTextColor(context.getColor(R.color.authing_text_gray)) // 设置隐私栏文本颜色，不包括协议
                .setPrivacyProtocolColor(context.getColor(R.color.authing_text_black)) // 设置隐私栏协议颜色
                .setPrivacySize(12) // 设置隐私栏区域字体大小
                .setHidePrivacySmh(true)
                .setLoginBtnText(context.getString(R.string.authing_current_phone_login))
                .setLoginBtnTopYOffset(365)//160+52+21+24+102=359
                .setLoginBtnWidth(screenWidth - 24 * 2)
                .setLoginBtnHeight(44)
                .setLoginBtnBackgroundRes("authing_button_background")
                .setLoginBtnTextSize(16)
                .addCustomView(titleLayout, "titleLayout", UnifyUiConfig.POSITION_IN_BODY, null)
                .addCustomView(otherLoginRel, "otherBtn", UnifyUiConfig.POSITION_IN_BODY, null)
                //.addCustomView(socialRel, "socialList", UnifyUiConfig.POSITION_IN_BODY, null)
                .setLoadingVisible(true)
                .setLoadingView(inflateLoadingLayout())
                .setLoginListener(new LoginListener() {
                    @Override
                    public boolean onDisagreePrivacy(TextView privacyTv, Button btnLogin) {
                        showPrivacyBottomDialog(privacyTv, btnLogin);
                        return true;
                    }
                });
        initPrivacy(builder);
        UnifyUiConfig uiConfig = builder.build(context);
        QuickLogin.getInstance().setUnifyUiConfig(uiConfig);
    }

    private TitleLayout inflateTitleLayout() {
        TitleLayout titleLayout = new TitleLayout(context);
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) Util.dp2px(context, 44));
        titleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        titleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        titleLayoutParams.addRule(RelativeLayout.ALIGN_TOP, com.netease.nis.quicklogin.R.id.yd_iv_logo);
        titleLayout.setLayoutParams(titleLayoutParams);
        titleLayout.setShowBackIcon(true);
        titleLayout.setCheckNetWork(true);
        titleLayout.setPadding((int) Util.dp2px(context, 24), 0, (int) Util.dp2px(context, 12), 0);
        titleLayout.setBackIconClickListener(v -> {
            quit();
            callback.call(500, "cancel", null);
        });
        titleLayout.initView();
        return titleLayout;
    }

    private RelativeLayout inflateOtherLayout() {
        RelativeLayout otherLoginRel = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, (int) Util.dp2px(context, 12), 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.BELOW, com.netease.nis.quicklogin.R.id.yd_btn_oauth);
        otherLoginRel.setLayoutParams(layoutParamsOther);

        Button other = new Button(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = (int) Util.dp2px(context, 24);
        lp.setMargins(m, 0, m, 0);
        other.setLayoutParams(lp);
        otherLoginRel.addView(other);
        other.setText(context.getString(R.string.authing_other_login));
        other.setTextAppearance(android.R.style.Widget_TextView);
        other.setAllCaps(false);
        other.setStateListAnimator(null);
        other.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.authing_text_large_size));
        other.setTextColor(context.getColor(R.color.authing_text_black));
        other.setBackgroundResource(R.drawable.authing_button_background_gray);
        other.setMinimumWidth((int) Util.dp2px(context, screenWidth - 24 * 2));
        other.setMinimumHeight((int) Util.dp2px(context, 42));
        other.setOnClickListener((v) -> {
            AuthFlow.start((Activity) context);
            quit();
            callback.call(500, "cancel", null);
        });
        return otherLoginRel;
    }

    private LinearLayout inflateSocialLayout() {
        LinearLayout socialRel = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParamsSocial = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsSocial.setMargins(0, (int) Util.dp2px(context, 12 + 44 + 40), 0, 0);
        layoutParamsSocial.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsSocial.addRule(RelativeLayout.BELOW, com.netease.nis.quicklogin.R.id.yd_btn_oauth);
        socialRel.setLayoutParams(layoutParamsSocial);
        socialRel.setOrientation(LinearLayout.VERTICAL);

        ContinueWithTextView continueWithTextView = new ContinueWithTextView(context);
        socialRel.addView(continueWithTextView);

        SocialLoginListView slv = new SocialLoginListView(context);
        LinearLayout.LayoutParams slvLayoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        slvLayoutParams.setMargins(0, (int) Util.dp2px(context, 16), 0, 0);
        slv.setLayoutParams(slvLayoutParams);
        slv.setOnLoginListener(((code, message, userInfo) -> {
            quit();
            callback.call(code, message, userInfo);
        }));
        socialRel.addView(slv);
        return socialRel;
    }

    private LinearLayout inflateLoadingLayout() {
        LinearLayout loadingLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams loadingParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        loadingLayout.setLayoutParams(loadingParams);
        loadingLayout.setOrientation(LinearLayout.VERTICAL);
        loadingLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        loadingLayout.setBackgroundColor(Color.parseColor("#00000000"));

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageParams.topMargin = (int) Util.dp2px(context, 520);
        loadingDrawable = (AnimatedVectorDrawable) context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        imageView.setImageDrawable(loadingDrawable);
        loadingDrawable.start();
        imageView.setLayoutParams(imageParams);
        loadingLayout.addView(imageView);

        return loadingLayout;
    }

    private void initPrivacy(UnifyUiConfig.Builder builder) {
        Authing.getPublicConfig(config -> {
            if (config == null) {
                return;
            }

            List<Agreement> agreements = config.getAgreements();
            if (agreements == null || agreements.size() == 0) {
                return;
            }
            Spannable spannable;
            String lang = Locale.getDefault().getLanguage();
            for (Agreement agreement : config.getAgreements()) {
                if (agreement.getLang().startsWith(lang) && agreement.isShowAtLogin()) {
                    Spanned htmlAsSpanned = Html.fromHtml(agreement.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY);
                    spannable = new SpannableString(removeTrailingLineBreak(htmlAsSpanned));
                    URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
                    for (int i = 0; i < spans.length; i++) {
                        URLSpan span = spans[i];
                        int start = spannable.getSpanStart(span);
                        int end = spannable.getSpanEnd(span);
                        String title = spannable.toString().subSequence(start, end).toString();
                        String url = span.getURL();
                        if (i == 0) {
                            builder.setPrivacyTextStart(spannable.toString().subSequence(0, start).toString());
                            builder.setProtocolText(title);
                            builder.setProtocolLink(url);
                        } else if (i == 1) {
                            builder.setProtocol2Text(title);
                            builder.setProtocol2Link(url);
                        } else if (i == 2) {
                            builder.setProtocol3Text(title);
                            builder.setProtocol3Link(url);
                        }
                    }
                    break;
                }
            }
        });
    }

    private CharSequence removeTrailingLineBreak(CharSequence text) {
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    private void showPrivacyBottomDialog(TextView privacyTv, Button btnLogin) {
        if (privacyDialog == null) {
            privacyDialog = new PrivacyConfirmBottomDialog(privacyTv.getContext());
        }
        privacyDialog.setOnPrivacyListener(new PrivacyConfirmDialog.OnPrivacyListener() {

            @Override
            public void onShow() {

            }

            @Override
            public void onCancel() {
                privacyDialog.dismiss();
            }

            @Override
            public void onAgree() {
                privacyDialog.dismiss();
                QuickLogin.getInstance().setPrivacyState(true);
                btnLogin.performClick();
            }
        });
        privacyDialog.show();
        privacyDialog.setContent(privacyTv.getText());
    }

    private void quit() {
        clear();
        QuickLogin.getInstance().quitActivity();
    }

    private void clear(){
        if (privacyDialog != null) {
            if (privacyDialog.isShowing()) {
                privacyDialog.dismiss();
            }
            privacyDialog = null;
        }
        if (loadingDrawable != null) {
            if (loadingDrawable.isRunning()) {
                loadingDrawable.stop();
            }
            loadingDrawable = null;
        }
    }

    @Override
    public void login(Context context, @NonNull AuthCallback<UserInfo> callback) {
        start(callback);
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
    }
}
