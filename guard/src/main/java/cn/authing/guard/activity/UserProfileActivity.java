package cn.authing.guard.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.profile.UserProfileContainer;
import cn.authing.guard.util.Util;

public class UserProfileActivity extends BaseAuthActivity {

    private UserProfileContainer userProfileContainer;
    private LinearLayout customDataContainer;
    private final Map<String, TextView> customDataViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_user_profile);

        userProfileContainer = findViewById(R.id.user_profile_container);

        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            return;
        }
        customDataContainer = findViewById(R.id.ll_custom_data);
        setupCustomDataUI(customDataContainer, userInfo);

        Button btn = findViewById(R.id.btn_logout);
        btn.setOnClickListener(v -> logout());

        btn = findViewById(R.id.btn_delete);
        btn.setOnClickListener(v -> deleteAccount());
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            return;
        }

        userProfileContainer.refreshData();

        if (userInfo.getCustomData().size() > 0) {
            customDataContainer.setVisibility(View.VISIBLE);
        } else {
            customDataContainer.setVisibility(View.GONE);
        }
        for (UserInfo.CustomData data : userInfo.getCustomData()) {
            TextView tv = customDataViews.get(data.getKey());
            Objects.requireNonNull(tv).setText(data.getValue());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000) {
            Uri selectedImageUri = data.getData();
            InputStream in;
            try {
                in = getContentResolver().openInputStream(selectedImageUri);
                AuthClient.uploadAvatar(in, (code, message, userInfo) -> runOnUiThread(()-> userProfileContainer.refreshData()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupCustomDataUI(LinearLayout container, UserInfo user) {
        int padding = (int)getResources().getDimension(R.dimen.authing_user_profile_padding);
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
            tvValue.setMaxLines(2);
            tvValue.setPadding(padding, 0, padding, 0);
            tvValue.setEllipsize(TextUtils.TruncateAt.END);
            tvValue.setTextSize(16);
            layout.addView(tvValue);

            ImageView rightArrow = new ImageView(this);
            rightArrow.setImageResource(R.drawable.authing_arrow_right);
            layout.addView(rightArrow);
            customDataViews.put(data.getKey(), tvValue);

            View sep = new View(this);
            LinearLayout.LayoutParams lpSep = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            int m = (int)getResources().getDimension(R.dimen.authing_form_start_end_margin);
            lpSep.setMargins(m, 0, 0, 0);
            sep.setBackgroundColor(0xffdddddd);
            sep.setLayoutParams(lpSep);
            container.addView(sep);

            layout.setOnClickListener((v -> goUpdateUserData(data)));
        }
    }

    private void logout() {
        AuthClient.logout((code, message, data)-> AuthFlow.start(this));
    }

    private void deleteAccount() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.authing_delete_account).setMessage(R.string.authing_delete_account_tip)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> AuthClient.deleteAccount((code, message, data) -> {
                    if (code == 200) {
                        AuthFlow.start(UserProfileActivity.this);
                    } else {
                        runOnUiThread(()->Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_LONG).show());
                    }
                }))
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void goUpdateUserData(UserInfo.CustomData data) {
        Intent intent = new Intent(this, UpdateUserProfileActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }
}
