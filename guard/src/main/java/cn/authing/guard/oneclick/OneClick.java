package cn.authing.guard.oneclick;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.data.ImageLoader;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
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
    private QuickLogin quickLogin;

    private int screenWidth; // dp

    public OneClick(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_LOGIN)
                    startLogin();
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

        Authing.getPublicConfig(config -> {
            String businessId = (_bid != null ) ? _bid : config.getSocialBusinessId(Const.EC_TYPE_YI_DUN);
            quickLogin = QuickLogin.getInstance(context, businessId);
            quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
                @Override
                public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                    //预取号成功
                    ALog.d(TAG, "Got phone:" + mobileNumber);
                    handler.sendEmptyMessageDelayed(MSG_LOGIN, 1000);
                }

                @Override
                public void onGetMobileNumberError(String YDToken, String msg) {
                    ALog.e(TAG, "Got phone error:" + msg);
                    callback.call(500, msg, null);
                }
            });
        });
    }

    private void startLogin() {
        if (uiConfig != null) {
            quickLogin.setUnifyUiConfig(uiConfig);
            startOnePass();
            return;
        }

        Authing.getPublicConfig((config)->{
            if (config == null) {
                return;
            }

            String url = config.getUserpoolLogo();
            ImageLoader.with(context).execute(url, (ok, result)->{
                config(result);
                startOnePass();
            });
        });
    }

    private void startOnePass() {
        quickLogin.onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String YDToken, String accessCode) {
                quickLogin.quitActivity();
                //一键登录成功 运营商token：accessCode获取成功
                //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                ALog.e(TAG, "onGetTokenSuccess:" + accessCode);
                authingLogin(YDToken, accessCode);
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {
                quickLogin.quitActivity();
                ALog.e(TAG, "onGetTokenError:" + msg);
                callback.call(500, msg, null);
            }

            @Override
            public void onCancelGetToken() {
                callback.call(201, null, null);
            }
        });
    }

    private void authingLogin(String t, String ac) {
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.loginByOneAuth(t, ac, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            OIDCClient.loginByOneAuth(t, ac, this::fireCallback);
        }
    }

    private void fireCallback(int code, String message, UserInfo userInfo){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()-> {
            if (code != 200) {
                Toast.makeText(Authing.getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
            callback.call(code, message, userInfo);
        });
    }

    private void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        //px
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        ALog.d(TAG, "屏幕宽度（像素）：" + width);
        ALog.d(TAG, "屏幕高度（像素）：" + height);
        ALog.d(TAG, "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        ALog.d(TAG, "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        ALog.d(TAG, "屏幕宽度（dp）：" + screenWidth);
        ALog.d(TAG, "屏幕高度（dp）：" + screenHeight);
    }

    private void config(Drawable logo) {
        Drawable mainColorDrawable = new ColorDrawable(Util.getThemeAccentColor(context));

        int topMargin = (int)Util.dp2px(context, 16);
        RelativeLayout otherLoginRel = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, topMargin, 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.BELOW, com.netease.nis.quicklogin.R.id.oauth_login);
        otherLoginRel.setLayoutParams(layoutParamsOther);

        Button other = new Button(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = (int) Util.dp2px(context, 24);
        lp.setMargins(m, 0, m, 0);
        other.setLayoutParams(lp);
        otherLoginRel.addView(other);
        other.setText(context.getString(R.string.authing_other_login));
        other.setStateListAnimator(null);
        other.setTextColor(0xff545968);
        other.setBackgroundColor(0xffF5F6F7);
        other.setMinimumWidth((int)Util.dp2px(context, screenWidth - 24*2));
        other.setMinimumHeight((int)Util.dp2px(context, 48));
        other.setOnClickListener((v)-> {
            quickLogin.quitActivity();
            AuthFlow.start((Activity) context);
            callback.call(500, "cancel", null);
        });

        RelativeLayout socialRel = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParamsSocial = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsSocial.setMargins(0, 0, 0, (int)Util.dp2px(context, 100));
        layoutParamsSocial.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsSocial.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        socialRel.setLayoutParams(layoutParamsSocial);
        SocialLoginListView slv = new SocialLoginListView(context);
        slv.setOnLoginListener(((code, message, userInfo) -> {
            quickLogin.quitActivity();
            callback.call(code, message, userInfo);
        }));
        socialRel.addView(slv);

        UnifyUiConfig c = new UnifyUiConfig.Builder()
                .setHideNavigation(true)
                .setLogoIconDrawable(logo)
                .setLogoTopYOffset(80)
                .setMaskNumberTopYOffset(250)
                .setSloganColor(0)
                .setSloganBottomYOffset(1000)
                .setLoginBtnText(context.getString(R.string.authing_current_phone_login))
                .setLoginBtnTopYOffset(320)
                .setLoginBtnWidth(screenWidth - 24*2)
                .setLoginBtnHeight(48)
                .setLoginBtnBackgroundDrawable(mainColorDrawable)
                .addCustomView(otherLoginRel, "otherBtn", UnifyUiConfig.POSITION_IN_BODY, null)
//                .addCustomView(socialRel, "socialList", UnifyUiConfig.POSITION_IN_BODY, null)
                .setPrivacyBottomYOffset(80)
                .setPrivacyMarginLeft(24)
                .setPrivacyMarginRight(24)
                .setPrivacyTextGravityCenter(false)
                .setCheckBoxGravity(Gravity.TOP)
                .setPrivacyCheckBoxWidth(21)
                .setPrivacyProtocolColor(context.getColor(R.color.authing_main))
                .setPrivacyCheckBoxHeight(18)
                .setUnCheckedImageName("authing_checkbox")
                .setCheckedImageName("authing_checked")
                .setPrivacySize(14)
                .build(context);
        quickLogin.setUnifyUiConfig(c);
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
