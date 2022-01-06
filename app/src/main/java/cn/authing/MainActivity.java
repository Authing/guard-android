package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.authing.guard.Authing;
import cn.authing.guard.activity.UpdateCustomDataActivity;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class MainActivity extends AppCompatActivity {

    private TextView tvNickName;
    private TextView tvName;
    private TextView tvUserName;
    private TextView tvPhone;
    private TextView tvEmail;

    private Map<String, TextView> customDataViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNickName = findViewById(R.id.tv_nick_name);
        tvName = findViewById(R.id.tv_name);
        tvUserName = findViewById(R.id.tv_username);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);

        TextView tvChangePassword = findViewById(R.id.tv_change_password);
        tvChangePassword.setOnClickListener((v)->{
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        });

        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            return;
        }
        LinearLayout customData = findViewById(R.id.ll_custom_data);
        setupCustomDataUI(customData, userInfo);

        Button btn = findViewById(R.id.btn_logout);
        btn.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            return;
        }

        setText(tvNickName, userInfo.getNickname());
        setText(tvName, userInfo.getName());
        setText(tvUserName, userInfo.getUsername());
        setText(tvPhone, userInfo.getPhone_number());
        setText(tvEmail, userInfo.getEmail());

        for (UserInfo.CustomData data : userInfo.getCustomData()) {
            TextView tv = customDataViews.get(data.getKey());
            tv.setText(data.getValue());
        }
    }

    private void setupCustomDataUI(LinearLayout container, UserInfo user) {
        int padding = (int)getResources().getDimension(R.dimen.authing_form_start_end_margin);
        for (UserInfo.CustomData data : user.getCustomData()) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)Util.dp2px(this, 48));
            layout.setLayoutParams(lp);
            layout.setPadding(padding, 0, padding, 0);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            container.addView(layout);

            TextView tvLabel = new TextView(this);
            tvLabel.setText(data.getLabel());
            tvLabel.setTextSize(16);
            layout.addView(tvLabel);

            Space space = new Space(this);
            LinearLayout.LayoutParams lpSpace = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            space.setLayoutParams(lpSpace);
            layout.addView(space);

            TextView tvValue = new TextView(this);
            tvValue.setTextSize(16);
            layout.addView(tvValue);
            customDataViews.put(data.getKey(), tvValue);

            View sep = new View(this);
            LinearLayout.LayoutParams lpSep = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            int m = (int)getResources().getDimension(R.dimen.authing_form_start_end_margin);
            lpSep.setMargins(m, 0, 0, 0);
            sep.setBackgroundColor(0xffdddddd);
            sep.setLayoutParams(lpSep);
            container.addView(sep);

            layout.setOnClickListener((v -> {
                goUpdateUserData(data);
            }));
        }
    }

    private void logout() {
        Authing.logout((code, message, data)->{
            Intent intent = new Intent(this, SampleListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setText(TextView tv, String s) {
        if (Util.isNull(s)) {
            tv.setText("Unspecified");
        } else {
            tv.setText(s);
        }
    }

    private void goUpdateUserData(UserInfo.CustomData data) {
        Intent intent = new Intent(this, UpdateCustomDataActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }
}