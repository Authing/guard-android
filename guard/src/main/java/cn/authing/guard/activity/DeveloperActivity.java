package cn.authing.guard.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.jwt.Jwt;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class DeveloperActivity extends BaseAuthActivity {

    private UserInfo userInfo;

    private LinearLayout layoutTitleAt;
    private ImageView accessTokenArrow;
    private Switch switchAT;
    private FrameLayout layoutValueAt;
    private TextView tvAccessToken;
    private ImageView ivCopyAccessToken;
    private TextView tvAccessTokenIt;
    private TextView tvAccessTokenEt;

    private LinearLayout layoutTitleIt;
    private ImageView iDTokenArrow;
    private Switch switchIDT;
    private FrameLayout layoutValueIt;
    private TextView tvIDToken;
    private ImageView ivCopyIDToken;
    private TextView tvIDTokenIt;
    private TextView tvIDTokenEt;

    private LinearLayout layoutTitleRt;
    private ImageView refreshTokenArrow;
    private FrameLayout layoutValueRt;
    private String refreshToken;
    private TextView tvRefreshToken;
    private ImageView ivCopyRefreshToken;
    //private TextView tvRefreshTokenIt;
    //private TextView tvRefreshTokenEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_developer);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        userInfo = (UserInfo) getIntent().getSerializableExtra("user");
        if (userInfo == null) {
            return;
        }

        initActionBar();
        initAccessTokenModule();
        initIdTokenModule();
        initRefreshTokenModule();
        updateData(userInfo);
    }

    private void initActionBar(){
        findViewById(R.id.text_refresh).setOnClickListener((v)->{
            if (Util.isNull(refreshToken)) {
                return;
            }
            new OIDCClient().getNewAccessTokenByRefreshToken(refreshToken, (code, message, user)->{
                if (code == 200) {
                    runOnUiThread(() -> {
                        updateData(user);
                        ToastUtil.showCenter(DeveloperActivity.this,
                                getString(R.string.authing_refresh_token_success));
                    });
                }
            });
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initAccessTokenModule(){
        layoutTitleAt = findViewById(R.id.layout_title_at);
        layoutTitleAt.setSelected(true);
        layoutTitleAt.setOnClickListener(v -> {
            boolean isSelected = layoutTitleAt.isSelected();
            int visibility = isSelected ? View.GONE : View.VISIBLE;
            //switchAT.setVisibility(visibility);
            layoutValueAt.setVisibility(visibility);
            tvAccessTokenIt.setVisibility(visibility);
            tvAccessTokenEt.setVisibility(visibility);
            accessTokenArrow.setImageDrawable(isSelected ? getDrawable(R.drawable.ic_authing_arrow_down)
                    : getDrawable(R.drawable.ic_authing_arrow_up));
            layoutTitleAt.setSelected(!isSelected);
        });
        accessTokenArrow = findViewById(R.id.arrow_at);
        layoutValueAt = findViewById(R.id.layout_value_at);
        tvAccessTokenIt = findViewById(R.id.tv_at_iat);
        tvAccessTokenEt = findViewById(R.id.tv_at_exp);
        tvAccessToken = findViewById(R.id.tv_at);
        switchAT = findViewById(R.id.switch_decode_at);
        switchAT.setOnCheckedChangeListener((v, checked)-> updateAccessTokenData());
        switchAT.setChecked(true);
        ivCopyAccessToken = findViewById(R.id.iv_copy_at);
        ivCopyAccessToken.setOnClickListener((v -> {
            clipText(userInfo.getAccessToken(), tvAccessToken.getText());
        }));
    }

    private void initIdTokenModule(){
        layoutTitleIt = findViewById(R.id.layout_title_it);
        layoutTitleIt.setOnClickListener(v -> {
            boolean isSelected = layoutTitleIt.isSelected();
            int visibility = isSelected ? View.GONE : View.VISIBLE;
            //switchIDT.setVisibility(visibility);
            layoutValueIt.setVisibility(visibility);
            tvIDTokenIt.setVisibility(visibility);
            tvIDTokenEt.setVisibility(visibility);
            iDTokenArrow.setImageDrawable(isSelected ? getDrawable(R.drawable.ic_authing_arrow_down)
                    : getDrawable(R.drawable.ic_authing_arrow_up));
            layoutTitleIt.setSelected(!isSelected);
        });
        iDTokenArrow = findViewById(R.id.arrow_it);
        layoutValueIt = findViewById(R.id.layout_value_it);
        tvIDTokenIt = findViewById(R.id.tv_it_iat);
        tvIDTokenEt = findViewById(R.id.tv_it_exp);
        tvIDToken = findViewById(R.id.tv_id_token);
        switchIDT = findViewById(R.id.switch_decode_id_token);
        switchIDT.setOnCheckedChangeListener((v, checked)-> updateIdTokenData());
        switchIDT.setChecked(true);
        ivCopyIDToken = findViewById(R.id.iv_copy_idt);
        ivCopyIDToken.setOnClickListener((v -> {
            clipText(userInfo.getIdToken(), tvIDToken.getText());
        }));
    }

    private void initRefreshTokenModule(){
        layoutTitleRt = findViewById(R.id.layout_title_rt);
        layoutTitleRt.setOnClickListener(v -> {
            boolean isSelected = layoutTitleRt.isSelected();
            int visibility = isSelected ? View.GONE : View.VISIBLE;
            layoutValueRt.setVisibility(visibility);
            //tvRefreshTokenIt.setVisibility(visibility);
            //tvRefreshTokenEt.setVisibility(visibility);
            refreshTokenArrow.setImageDrawable(isSelected ? getDrawable(R.drawable.ic_authing_arrow_down)
                    : getDrawable(R.drawable.ic_authing_arrow_up));
            layoutTitleRt.setSelected(!isSelected);
        });
        refreshTokenArrow = findViewById(R.id.arrow_rt);
        layoutValueRt = findViewById(R.id.layout_value_rt);
        //tvRefreshTokenIt = findViewById(R.id.tv_rt_iat);
        //tvRefreshTokenEt = findViewById(R.id.tv_rt_exp);
        tvRefreshToken = findViewById(R.id.tv_rt);
        ivCopyRefreshToken = findViewById(R.id.iv_copy_rt);
        ivCopyRefreshToken.setOnClickListener((v -> {
            clipText(userInfo.getRefreshToken(), tvRefreshToken.getText());
        }));
    }

    private void clipText(CharSequence label, CharSequence text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        ToastUtil.showCenter(DeveloperActivity.this, getString(R.string.authing_copied));
    }

    private void updateData(UserInfo userInfo) {
        updateAccessTokenData();
        updateIdTokenData();

        refreshToken = userInfo.getRefreshToken();
        if (TextUtils.isEmpty(refreshToken)) {
            tvRefreshToken.setText("null");
            ivCopyRefreshToken.setVisibility(View.GONE);
        } else {
            tvRefreshToken.setText(refreshToken);
        }
    }

    private void updateAccessTokenData() {
        try {
            UserInfo userInfo = Authing.getCurrentUser();
            String at = userInfo.getAccessToken();
            if (TextUtils.isEmpty(at)) {
                tvAccessToken.setText("null");
                ivCopyAccessToken.setVisibility(View.GONE);
                return;
            }

            Jwt accessToken = new Jwt(at);
            JSONObject accessTokenPayload = new JSONObject(accessToken.getPayload());
            boolean checked = switchAT.isChecked();
            if (checked) {
                Spannable decoded = getColoredJwt(accessToken);
                tvAccessToken.setText(decoded);
            } else {
                tvAccessToken.setText(getJwtSpannable(at));
            }

            tvAccessTokenIt.setText(getString(R.string.authing_token_issued_time,
                    getDate(accessTokenPayload.getString("iat"))));
            tvAccessTokenEt.setText(getString(R.string.authing_token_expired_time,
                    getDate(accessTokenPayload.getString("exp"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateIdTokenData() {
        try {
            UserInfo userInfo = Authing.getCurrentUser();
            String idt = userInfo.getIdToken();
            if (TextUtils.isEmpty(idt)) {
                tvIDToken.setText("null");
                ivCopyIDToken.setVisibility(View.GONE);
                return;
            }

            Jwt idToken = new Jwt(idt);
            JSONObject idTokenPayload = new JSONObject(idToken.getPayload());
            boolean checked = switchIDT.isChecked();
            if (checked) {
                Spannable decoded = getColoredJwt(idToken);
                tvIDToken.setText(decoded);
            } else {
                tvIDToken.setText(getJwtSpannable(idt));
            }

            tvIDTokenIt.setText(getString(R.string.authing_token_issued_time,
                    getDate(idTokenPayload.getString("iat"))));
            tvIDTokenEt.setText(getString(R.string.authing_token_expired_time,
                    getDate(idTokenPayload.getString("exp"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Spannable getJwtSpannable(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return new SpannableString(token);
        }
        Spannable spannable = new SpannableString(token);
        spannable.setSpan(new ForegroundColorSpan(0xfff60049), 0, parts[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int payloadEnd = parts[0].length() + 1 + parts[1].length();
        spannable.setSpan(new ForegroundColorSpan(0xffc900ff), parts[0].length() + 1, payloadEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(0xff15a9de), token.length() - parts[2].length(), token.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private Spannable getColoredJwt(Jwt jwt) {
        String header = prettifyJSON(jwt.getHeader());
        String payload = prettifyJSON(jwt.getPayload());
        Spannable text = new SpannableString(header + "\n\n" + payload);
        text.setSpan(new ForegroundColorSpan(0xfff60049), 0, header.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(0xffc900ff), header.length(), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private String prettifyJSON(String json) {
        StringBuilder sb = new StringBuilder();
        int level = 0;
        for (int i = 0, n = json.length();i < n;++i) {
            char c = json.charAt(i);
            if (c == '{') {
                sb.append(c);
                sb.append('\n');
                sb.append(getIndent(++level));
            } else if (c == '}') {
                sb.append('\n');
                sb.append(getIndent(--level));
                sb.append(c);
            } else if (c == ',') {
                sb.append(c);
                sb.append('\n');
                sb.append(getIndent(level));
            } else if (c == ':') {
                sb.append(c);
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String getIndent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0;i < level;++i) {
            indent.append("    ");
        }
        return indent.toString();
    }

    private String getDate(String time) {
        Date timeD = new Date(Long.parseLong(time) * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timeD);
    }
}