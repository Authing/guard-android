package cn.authing.guard.complete;

import static cn.authing.guard.flow.AuthFlow.KEY_EXTENDED_FIELDS;
import static cn.authing.guard.flow.AuthFlow.KEY_USER_INFO;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.authing.guard.GetEmailCodeButton;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class UserInfoCompleteButton extends LoadingButton {

    private static final int EMAIL = 1;
    private static final int PHONE = 1 << 1;
    private static final int OTHER = 1 << 2;
    private static final int CUSTOM = 1 << 3;

    private int completeFlag;
    private ExtendedField emailField;
    private ExtendedField phoneField;
    private JSONObject otherInfoField;
    private JSONObject customField;

    private UserInfo userInfo;

    public UserInfoCompleteButton(@NonNull Context context) {
        this(context, null);
    }

    public UserInfoCompleteButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public UserInfoCompleteButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("UserInfoCompleteButton");

        if (!(getContext() instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) getContext();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setOnClickListener((v -> submit()));

        post(()->{
            View v = Util.findViewByClass(this, GetEmailCodeButton.class);
            if (v != null) {
                ((GetEmailCodeButton)v).setScene("CHANGE_EMAIL");
            }
        });
    }

    private void submit() {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        Object o = flow.getData().get(KEY_EXTENDED_FIELDS);
        if (!(o instanceof List)) {
            return;
        }

        userInfo = (UserInfo) flow.getData().get(KEY_USER_INFO);

        View view = Util.findViewByClass(this, UserInfoCompleteContainer.class);
        if (view == null) {
            return;
        }

        UserInfoCompleteContainer container = (UserInfoCompleteContainer) view;
        List<ExtendedField> extendedFields = container.getValues();
        otherInfoField = new JSONObject();
        customField = new JSONObject();
        for (ExtendedField field : extendedFields) {
            if ("email".equals(field.getName())) {
                emailField = field;
                completeFlag |= EMAIL;
            } else if ("phone".equals(field.getName())) {
                phoneField = field;
                completeFlag |= PHONE;
            } else if ("internal".equals(field.getType())) {
                try {
                    if (field.isRequired() && TextUtils.isEmpty(field.getValue())) {
                        String tip = String.format(getContext().getString(R.string.authing_field_cannot_be_empty), field.getLabel());
                        error(tip);
                        return;
                    }
                    otherInfoField.put(field.getName(), field.getValue());
                    completeFlag |= OTHER;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO wait for server side fix
//                try {
//                    if (field.isRequired() && TextUtils.isEmpty(field.getValue())) {
//                        String tip = String.format(getContext().getString(R.string.authing_field_cannot_be_empty), field.getLabel());
//                        error(tip);
//                        return;
//                    }
//                    customField.put(field.getName(), field.getValue());
//                    completeFlag |= CUSTOM;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }

        startLoadingVisualEffect();
        update();
    }

    private void update() {
        if ((completeFlag & EMAIL) != 0) {
            bindEmail();
        } else if ((completeFlag & PHONE) != 0) {
            bindPhone();
        } else if ((completeFlag & OTHER) != 0) {
            updateUserInfo();
        }  else if ((completeFlag & CUSTOM) != 0) {
            updateCustom();
        } else {
            AuthActivity activity = (AuthActivity) getContext();
            Intent intent = new Intent();
            intent.putExtra("user", userInfo);
            activity.setResult(AuthActivity.OK, intent);
            activity.finish();
        }
    }

    private void bindEmail() {
        if (shouldSkipEmail()) {
            completeFlag &= ~EMAIL;
            update();
            return;
        }

        String v = emailField.getValue();
        if (TextUtils.isEmpty(v)) {
            String tip = String.format(getContext().getString(R.string.authing_field_cannot_be_empty), emailField.getLabel());
            error(tip);
            return;
        }

        String[] splits = v.split(":");
        if (splits.length < 2) {
            error("Please enter email and verify code");
            return;
        }

        String email = splits[0];
        String vCode = splits[1];
        AuthClient.bindEmail(email, vCode, (code, message, data)->{
            if (code == 200) {
                completeFlag &= ~EMAIL;
                try {
                    userInfo = data;
                    update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                error(message);
            }
        });
    }

    private void bindPhone() {
        if (shouldSkipPhone()) {
            completeFlag &= ~PHONE;
            update();
            return;
        }

        String v = phoneField.getValue();
        if (TextUtils.isEmpty(v)) {
            String tip = String.format(getContext().getString(R.string.authing_field_cannot_be_empty), phoneField.getLabel());
            error(tip);
            return;
        }

        String[] splits = v.split(":");
        if (splits.length < 2) {
            error("Please enter phone and verify code");
            return;
        }

        String phone = splits[0];
        String vCode = splits[1];
        AuthClient.bindPhone(phone, vCode, (code, message, data)->{
            if (code == 200) {
                completeFlag &= ~PHONE;
                try {
                    userInfo = data;
                    update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                error(message);
            }
        });
    }

    private void updateUserInfo() {
        AuthClient.updateProfile(otherInfoField, (code, message, data)->{
            if (code == 200) {
                completeFlag &= ~OTHER;
                try {
                    userInfo = data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                update();
            } else {
                error(message);
            }
        });
    }

    private void updateCustom() {
        AuthClient.setCustomUserData(customField, (code, message, data)->{
            if (code == 200) {
                completeFlag &= ~CUSTOM;
                try {
                    userInfo = UserInfo.createUserInfo(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                update();
            } else {
                error(message);
            }
        });
    }

    private boolean shouldSkipEmail() {
        return shouldSkipField(emailField);
    }

    private boolean shouldSkipPhone() {
        return shouldSkipField(phoneField);
    }

    private boolean shouldSkipField(ExtendedField field) {
        if (field.isRequired()) {
            return false;
        }

        String v = field.getValue();
        if (TextUtils.isEmpty(v)) {
            return true;
        }

        String[] splits = v.split(":");
        return splits.length < 2;
    }

    private void error(String message) {
        stopLoadingVisualEffect();
        Util.setErrorText(this, message);
    }
}
