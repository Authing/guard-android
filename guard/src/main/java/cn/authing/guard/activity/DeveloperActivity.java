package cn.authing.guard.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.jwt.Jwt;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.Util;

public class DeveloperActivity extends BaseAuthActivity {

    private UserInfo userInfo;

    private Switch switchAT;
    private TextView tvAccessToken;
    private ImageView ivCopyAccessToken;

    private Switch switchIDT;
    private TextView tvIDToken;
    private ImageView ivCopyIDToken;

    private String refreshToken;
    private TextView tvRefreshToken;
    private ImageView ivCopyRefreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_developer);

        userInfo = (UserInfo) getIntent().getSerializableExtra("user");
        if (userInfo == null) {
            return;
        }

        tvAccessToken = findViewById(R.id.tv_at);
        switchAT = findViewById(R.id.switch_decode_at);
        switchAT.setOnCheckedChangeListener((v, checked)-> updateAccessTokenData());
        ivCopyAccessToken = findViewById(R.id.iv_copy_at);
        ivCopyAccessToken.setOnClickListener((v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(userInfo.getAccessToken(), tvAccessToken.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.authing_copied, Toast.LENGTH_SHORT).show();
        }));

        tvIDToken = findViewById(R.id.tv_id_token);
        switchIDT = findViewById(R.id.switch_decode_id_token);
        switchIDT.setOnCheckedChangeListener((v, checked)-> updateIdTokenData());
        ivCopyIDToken = findViewById(R.id.iv_copy_idt);
        ivCopyIDToken.setOnClickListener((v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(userInfo.getIdToken(), tvIDToken.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.authing_copied, Toast.LENGTH_SHORT).show();
        }));

        tvRefreshToken = findViewById(R.id.tv_rt);
        ivCopyRefreshToken = findViewById(R.id.iv_copy_rt);
        ivCopyRefreshToken.setOnClickListener((v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(userInfo.getRefreshToken(), tvRefreshToken.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.authing_copied, Toast.LENGTH_SHORT).show();
        }));

        updateData(userInfo);

        PrimaryButton refreshButton = findViewById(R.id.btn_refresh_token);
        refreshButton.setOnClickListener((v)->{
            if (!Util.isNull(refreshToken)) {
                refreshButton.startLoadingVisualEffect();
                OIDCClient.getNewAccessTokenByRefreshToken(refreshToken, (code, message, user)->{
                    refreshButton.stopLoadingVisualEffect();
                    if (code == 200) {
                        runOnUiThread(()-> updateData(user));
                    }
                });
            }
        });
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

            TextView tv = findViewById(R.id.tv_at_iat);
            tv.setText(getDate(accessTokenPayload.getString("iat")));
            tv = findViewById(R.id.tv_at_exp);
            tv.setText(getDate(accessTokenPayload.getString("exp")));
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

            TextView tv = findViewById(R.id.tv_idt_iat);
            tv.setText(getDate(idTokenPayload.getString("iat")));
            tv = findViewById(R.id.tv_idt_exp);
            tv.setText(getDate(idTokenPayload.getString("exp")));
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