package cn.authing.guard.oneclick;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netease.nis.quicklogin.QuickLogin;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.NetworkUtils;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class OneClickAuthButton extends LoadingButton {

    private OneClick oneClick;
    private final boolean showLoading;
    private final boolean showByConfig;
    private boolean isConfigured;
    private final boolean linkNetWork;

    public OneClickAuthButton(@NonNull Context context) {
        this(context, null);
    }

    public OneClickAuthButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public OneClickAuthButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("OneClickAuthButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_one_click);
        }
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "gravity") == null) {
            setGravity(Gravity.CENTER);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OneClickAuthButton);
        showByConfig = array.getBoolean(R.styleable.OneClickAuthButton_showByConfig, false);
        linkNetWork = array.getBoolean(R.styleable.OneClickAuthButton_linkNetWork, false);
        showLoading = array.getBoolean(R.styleable.OneClickAuthButton_showLoading, true);
        array.recycle();

        if (linkNetWork) {
            startListeningNetWork();
        }

        if (showByConfig) {
            getConfigured();
        }

        post(this::refreshVisible);

        initClick(context);
    }

    private void getConfigured() {
        post(() -> Authing.getPublicConfig((config -> {
            if (config == null) {
                return;
            }
            List<SocialConfig> socialConfigs = config.getSocialConfigs();
            if (socialConfigs == null || socialConfigs.isEmpty()) {
                return;
            }
            for (int i = 0, n = socialConfigs.size(); i < n; ++i) {
                SocialConfig sc = socialConfigs.get(i);
                String type = sc.getType();
                if (Const.EC_TYPE_YI_DUN.equals(type)) {
                    isConfigured = true;
                    break;
                }
            }
        })));
    }

    private void initClick(Context context) {
        setOnClickListener((v) -> {
            if (showLoading) {
                startLoadingVisualEffect();
            }
            if (null == oneClick) {
                oneClick = new OneClick(context);
            }
            oneClick.start((code, message, userInfo) -> callBack(v, code, message, userInfo));
        });
    }

    private void refreshVisible() {
        if (linkNetWork) {
            int net = QuickLogin.getInstance().checkNetWork(getContext());
            if (!NetworkUtils.isMobileEnabled(getContext()) || net == 4 || net == 5) {
                setVisibility(GONE);
            } else {
                refreshVisibleOnlyByConfig();
            }
        } else {
            refreshVisibleOnlyByConfig();
        }
    }

    private void refreshVisibleOnlyByConfig() {
        if (showByConfig) {
            if (isConfigured) {
                setVisibility(View.VISIBLE);
            } else {
                setVisibility(GONE);
            }
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    private void callBack(View v, int code, String message, UserInfo userInfo) {
        if (showLoading) {
            stopLoadingVisualEffect();
        }
        if (code == 200 && userInfo != null) {
            Authing.getPublicConfig((config) -> {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                    if (Util.shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                        flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                        FlowHelper.handleUserInfoComplete(this, missingFields);
                    } else {
                        AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                        if (cb != null) {
                            cb.call(getContext(), code, message, userInfo);
                        }

                        post(() -> {
                            Intent intent = new Intent();
                            intent.putExtra("user", userInfo);
                            activity.setResult(AuthActivity.OK, intent);
                            activity.finish();
                        });
                    }
                }
            });
        } else {
            if (!TextUtils.isEmpty(message) && !"cancel".equals(message)) {
                post(() -> ToastUtil.showCenter(getContext(), message));
            }
        }
    }

    private void startListeningNetWork() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        ConnectivityManager connMgr = (ConnectivityManager) getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return;
        }
        connMgr.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                post(() -> refreshVisible());
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                post(() -> refreshVisible());
            }

        });
    }
}
