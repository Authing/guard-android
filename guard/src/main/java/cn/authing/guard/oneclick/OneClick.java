package cn.authing.guard.oneclick;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.ImageLoader;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.social.SocialLoginListView;
import cn.authing.guard.util.Util;

public class OneClick {

    private static final String TAG = "OneClickAuthButton";

    public static String bizId;

    private OneClickCallback callback;
    private QuickLogin quickLogin;

    private int screenWidth; // dp

    public interface OneClickCallback {
        void call(int code, String message, UserInfo userInfo);
    }

    private Context getContext() {
        return Authing.getAppContext();
    }

    public void start(String bid, @NotNull OneClickCallback callback) {
        String _bid = TextUtils.isEmpty(bid) ? bizId : bid;
        this.callback = callback;

        getAndroidScreenProperty();

        quickLogin = QuickLogin.getInstance(getContext(), _bid);
        quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                //预取号成功
                Log.d(TAG, "Got phone:" + mobileNumber);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startLogin();
            }

            @Override
            public void onGetMobileNumberError(String YDToken, String msg) {
                Log.e(TAG, "Got phone error:" + msg);
                callback.call(500, msg, null);
            }
        });
    }

    private void startLogin() {
        Authing.getPublicConfig((config)->{
            if (config == null) {
                return;
            }

            String url = config.getUserpoolLogo();
            new ImageLoader(getContext()) {
                @Override
                public void onPostExecute(Drawable result) {
                    config(result);
                    quickLogin.onePass(new QuickLoginTokenListener() {
                        @Override
                        public void onGetTokenSuccess(String YDToken, String accessCode) {
                            quickLogin.quitActivity();
                            //一键登录成功 运营商token：accessCode获取成功
                            //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                            Log.e(TAG, "onGetTokenSuccess:" + accessCode);
                            authingLogin(YDToken, accessCode);
                        }

                        @Override
                        public void onGetTokenError(String YDToken, String msg) {
                            quickLogin.quitActivity();
                            Log.e(TAG, "onGetTokenError:" + msg);
                            callback.call(500, msg, null);
                        }
                    });
                }
            }.execute(url);
        });
    }

    private void authingLogin(String t, String ac) {
        AuthClient.loginByOneClick(t, ac, (code, message, userInfo)-> callback.call(code, message, userInfo));
    }

    private void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
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

        Log.d(TAG, "屏幕宽度（像素）：" + width);
        Log.d(TAG, "屏幕高度（像素）：" + height);
        Log.d(TAG, "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d(TAG, "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d(TAG, "屏幕宽度（dp）：" + screenWidth);
        Log.d(TAG, "屏幕高度（dp）：" + screenHeight);
    }

    private void config(Drawable logo) {
        Drawable mainColorDrawable = new ColorDrawable(Util.getThemeAccentColor(getContext()));

        int bottomMargin = 300;
        int topMargin = 16;
        RelativeLayout otherLoginRel = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, topMargin, 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.BELOW, R.id.oauth_login);
        otherLoginRel.setLayoutParams(layoutParamsOther);

        Button other = new Button(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = (int) Util.dp2px(getContext(), 24);
        lp.setMargins(m, 0, m, 0);
        other.setLayoutParams(lp);
        otherLoginRel.addView(other);
//        int tpInDp = 320 + 48 + 8;
//        int tp = (int)Util.dp2px(getContext(), tpInDp);
//        ll.setPadding(m, tp, m, 0);
        other.setText("其他方式登录");
        other.setTextColor(0xff545968);
        other.setBackgroundColor(0xffF5F6F7);
        other.setMinimumWidth((int)Util.dp2px(getContext(), screenWidth - 24*2));
        other.setMinimumHeight((int)Util.dp2px(getContext(), 48));
        other.setOnClickListener((v)-> AuthFlow.start((Activity) getContext()));

        RelativeLayout socialRel = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams layoutParamsSocial = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsSocial.setMargins(0, 0, 0, (int)Util.dp2px(getContext(), 100));
        layoutParamsSocial.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsSocial.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        socialRel.setLayoutParams(layoutParamsSocial);
        SocialLoginListView slv = new SocialLoginListView(getContext());
        socialRel.addView(slv);

        UnifyUiConfig c = new UnifyUiConfig.Builder()
                .setHideNavigation(true)
                .setLogoIconDrawable(logo)
                .setLogoTopYOffset(80)
                .setMaskNumberTopYOffset(250)
                .setSloganColor(0)
                .setSloganBottomYOffset(1000)
                .setLoginBtnText("本机号码一键登录")
                .setLoginBtnTopYOffset(320)
                .setLoginBtnWidth(screenWidth - 24*2)
                .setLoginBtnHeight(48)
                .setLoginBtnBackgroundDrawable(mainColorDrawable)
                .addCustomView(otherLoginRel, "otherBtn", UnifyUiConfig.POSITION_IN_BODY, null)
                .addCustomView(socialRel, "socialList", UnifyUiConfig.POSITION_IN_BODY, null)
                .setPrivacyBottomYOffset(bottomMargin - 28 - 8)
                .build(getContext());
        quickLogin.setUnifyUiConfig(c);
    }
}
