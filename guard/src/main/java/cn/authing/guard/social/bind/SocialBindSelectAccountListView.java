package cn.authing.guard.social.bind;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.RoundImageView;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.ImageLoader;
import cn.authing.guard.data.SocialBindData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class SocialBindSelectAccountListView extends LinearLayout {

    public SocialBindSelectAccountListView(Context context) {
        this(context, null);
    }

    public SocialBindSelectAccountListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialBindSelectAccountListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        if (!(context instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = activity.getFlow();

        UserInfo data = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (data != null) {
            post(() -> initView(data.getSocialBindData()));
        }
    }

    private void initView(SocialBindData socialBindData) {
        if (socialBindData == null || socialBindData.getAccounts() == null) {
            return;
        }
        for (UserInfo userInfo : socialBindData.getAccounts()) {
            if (userInfo == null) {
                continue;
            }
            addAccountListView(userInfo);
        }
    }

    private void addAccountListView(UserInfo userInfo) {
        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) Util.dp2px(getContext(), 65));
        contentView.setLayoutParams(contentParams);
        contentView.setGravity(Gravity.CENTER_VERTICAL);

        RoundImageView roundImageView = new RoundImageView(getContext());
        int size = (int) Util.dp2px(getContext(), 40);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        roundImageView.setLayoutParams(params);
        roundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.with(getContext()).load(userInfo.getPhoto()).into(roundImageView);
        contentView.addView(roundImageView);

        LinearLayout userView = new LinearLayout(getContext());
        userView.setOrientation(VERTICAL);
        userView.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams userViewParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT);
        userViewParams.weight = 1;
        int m = (int) Util.dp2px(getContext(), 8);
        userViewParams.setMargins(m, 0, m, 0);
        userView.setLayoutParams(userViewParams);
        String userName = userInfo.getName();
        if (Util.isNull(userName)) {
            userName = userInfo.getNickname();
        }
        if (Util.isNull(userName)) {
            userName = userInfo.getUsername();
        }
        if (!Util.isNull(userName)) {
            userView.addView(createUserNameTextView(userName));
            String phone = userInfo.getPhone_number();
            if (Util.isNull(phone)) {
                phone = userInfo.getEmail();
            }
            if (!Util.isNull(phone)) {
                userView.addView(createUserPhoneTextView(phone));
            }
        } else {
            String phone = userInfo.getPhone_number();
            if (Util.isNull(phone)) {
                phone = userInfo.getEmail();
            }
            if (!Util.isNull(phone)) {
                userView.addView(createUserNameTextView(phone));
            }
        }
        contentView.addView(userView);
        addView(contentView);

        contentView.setOnClickListener(v -> doBindAccount(userInfo.getId()));
    }

    private void doBindAccount(String account) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        UserInfo userInfo = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (userInfo != null && userInfo.getSocialBindData() != null) {
            SocialBindData socialBindData = userInfo.getSocialBindData();
            AuthClient.bindWechatByAccountId(socialBindData.getKey(), account, (AuthCallback<UserInfo>) (code, message, data) -> {
                if (code == 200) {
                    Authing.getPublicConfig((config) -> {
                        if (getContext() instanceof AuthActivity) {
                            AuthActivity activity1 = (AuthActivity) getContext();
                            AuthFlow flow1 = (AuthFlow) activity1.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                            List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                            if (shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                                flow1.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                                FlowHelper.handleUserInfoComplete(SocialBindSelectAccountListView.this, missingFields);
                            } else {
                                AuthFlow.Callback<UserInfo> cb = flow1.getAuthCallback();
                                if (cb != null) {
                                    cb.call(getContext(), code, message, userInfo);
                                }

                                Intent intent = new Intent();
                                intent.putExtra("user", userInfo);
                                activity1.setResult(AuthActivity.OK, intent);
                                activity1.finish();
                            }
                        }
                    });
                } else {
                    Util.setErrorText(this, message);
                }
            });
        }
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }


    private TextView createUserNameTextView(String userName) {
        TextView userNameText = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        userNameText.setLayoutParams(params);
        userNameText.setGravity(Gravity.CENTER_VERTICAL);
        userNameText.setText(userName);
        userNameText.setTextColor(getResources().getColor(R.color.authing_text_black));
        userNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return userNameText;
    }

    private TextView createUserPhoneTextView(String userName) {
        TextView userNameText = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        userNameText.setLayoutParams(params);
        userNameText.setGravity(Gravity.CENTER_VERTICAL);
        userNameText.setText(userName);
        userNameText.setTextColor(getResources().getColor(R.color.authing_text_gray));
        userNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return userNameText;
    }

}
