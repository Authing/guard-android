package cn.authing.oneclick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.AuthingDemoLoginActivity;
import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.social.SocialLoginListView;
import cn.authing.guard.util.Util;

public class OneClickActivity extends AppCompatActivity {

    private final static String TAG = "OneClickActivity";

    TextView tvTip;
    QuickLogin quickLogin;
    LoadingButton btn;
    int width; //px
    int screenWidth; // dp

    EditText etBizId;
    EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click);

        getAndroidScreenProperty();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);

        tvTip = findViewById(R.id.tv_tip);

        etBizId = findViewById(R.id.et_biz_id);
        etUrl = findViewById(R.id.et_url);
        getInfo();

        btn = findViewById(R.id.btn_one_click);
        btn.setOnClickListener((v)->{
            btn.startLoadingVisualEffect();
            String bizId = etBizId.getText().toString();
            saveInfo();
            quickLogin = QuickLogin.getInstance(this, bizId);
            quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
                @Override
                public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                    //预取号成功
                    Log.d(TAG, "Got phone:" + mobileNumber);
                    btn.postDelayed(()-> {
                        startLogin();
                    }, 1000);
                }

                @Override
                public void onGetMobileNumberError(String YDToken, String msg) {
                    Log.e(TAG, "Got phone error:" + msg);
                    btn.stopLoadingVisualEffect();
                    runOnUiThread(()-> {
                        tvTip.setText(msg);
                    });
                }
            });
        });
    }

    private void startLogin() {
        config(quickLogin);
        quickLogin.onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String YDToken, String accessCode) {
                quickLogin.quitActivity();
                //一键登录成功 运营商token：accessCode获取成功
                //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                Log.e(TAG, "onGetTokenSuccess:" + accessCode);
                btn.stopLoadingVisualEffect();
                getAuthingToken(YDToken, accessCode);
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {
                quickLogin.quitActivity();
                Log.e(TAG, "onGetTokenError:" + msg);
                runOnUiThread(()->{
                    btn.stopLoadingVisualEffect();
                    tvTip.setText(msg);
                    btn.setText("登录");
                });
            }
        });
    }

    private void getAuthingToken(String t, String ac) {
        try {
            String url = etUrl.getText().toString();
            JSONObject body = new JSONObject();
            body.put("token", t);
            body.put("accessToken", ac);
            Guardian.post(url, body, (response -> {
                if (response != null && response.getCode() == 200) {
                    JSONObject data = response.getData();

                    String phone = null;
                    try {
                        phone = data.getString("phone");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UserInfo userInfo = new UserInfo();
                    userInfo.setPhone_number(phone);
                    gotoMain(userInfo);
                } else {
                    runOnUiThread(() -> {
                        tvTip.setText("用易盾 token 换取 authing token 失败");
                    });
                }
                runOnUiThread(() -> {
                    btn.stopLoadingVisualEffect();
                });
            }));
        } catch (Exception e) {

        }
    }

    private void getInfo() {
        SharedPreferences sp = getSharedPreferences("SP_AUTHING_GUARD", 0);
        String bizId = sp.getString("BIZID", "74ae90bd84f74b69a88b578bbbbcdcfd");
        String url = sp.getString("URL", "https://developer-beta.authing.cn/stats/ydtoken");
        etBizId.setText(bizId);
        etUrl.setText(url);
    }

    private void saveInfo() {
        String bizId = etBizId.getText().toString();
        String url = etUrl.getText().toString();
        SharedPreferences sp = getSharedPreferences("SP_AUTHING_GUARD", 0);
        sp.edit().putString("BIZID", bizId);
        sp.edit().putString("URL", url);
        sp.edit().commit();
    }

    private void gotoMain(UserInfo data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", data);
        startActivity(intent);
        finish();
    }

    public void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }

    private void config(QuickLogin quickLogin) {
        Drawable authingLogo = getDrawable(R.drawable.ic_authing_default_logo);
        Drawable authingMainColor = getDrawable(R.color.authing_main);

        int bottomMargin = 300;
        int topMargin = 16;
        RelativeLayout otherLoginRel = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, topMargin, 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.BELOW, R.id.oauth_login);
        otherLoginRel.setLayoutParams(layoutParamsOther);

        Button other = new Button(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = (int)Util.dp2px(this, 24);
        lp.setMargins(m, 0, m, 0);
        other.setLayoutParams(lp);
        otherLoginRel.addView(other);
        int tpInDp = 320 + 48 + 8;
        int tp = (int)Util.dp2px(this, tpInDp);
//        ll.setPadding(m, tp, m, 0);
        other.setText("其他方式登录");
        other.setTextColor(0xff545968);
        other.setBackgroundColor(0xffF5F6F7);
        other.setMinimumWidth((int)Util.dp2px(this, screenWidth - 24*2));
        other.setMinimumHeight((int)Util.dp2px(this, 48));
        other.setOnClickListener((v)->{
            Intent intent = new Intent(OneClickActivity.this, AuthingDemoLoginActivity.class);
            startActivity(intent);
        });

        RelativeLayout socialRel = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParamsSocial = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsSocial.setMargins(0, 0, 0, (int)Util.dp2px(this, 100));
        layoutParamsSocial.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsSocial.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        socialRel.setLayoutParams(layoutParamsSocial);
        SocialLoginListView slv = new SocialLoginListView(this);
        socialRel.addView(slv);

        UnifyUiConfig c = new UnifyUiConfig.Builder()
                .setHideNavigation(true)
                .setLogoIconDrawable(authingLogo)
                .setLogoTopYOffset(80)
                .setMaskNumberTopYOffset(250)
                .setSloganColor(0)
                .setSloganBottomYOffset(1000)
                .setLoginBtnText("本机号码一键登录")
                .setLoginBtnTopYOffset(320)
                .setLoginBtnWidth(screenWidth - 24*2)
                .setLoginBtnHeight(48)
                .setLoginBtnBackgroundDrawable(authingMainColor)
                .addCustomView(otherLoginRel, "otherBtn", UnifyUiConfig.POSITION_IN_BODY, null)
                .addCustomView(socialRel, "socialList", UnifyUiConfig.POSITION_IN_BODY, null)
                .setPrivacyBottomYOffset(bottomMargin - 28 - 8)
                .build(this);
        quickLogin.setUnifyUiConfig(c);
    }
}