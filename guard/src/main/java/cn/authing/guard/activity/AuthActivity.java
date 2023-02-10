package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.ss.android.larksso.LarkSSO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.CircularAnimatedView;
import cn.authing.guard.social.FaceBook;
import cn.authing.guard.social.Google;
import cn.authing.guard.util.Util;

public class AuthActivity extends AppCompatActivity {

    public static final int RC_LOGIN = 1024;
    public static final int OK = 42;

    public static final String AUTH_FLOW = "auth_flow";
    public static final String CONTENT_LAYOUT_ID = "content_layout_id";

    public static final String EVENT_VERIFY_CODE_ENTERED = "verify_code_entered";

    protected AuthFlow flow;

    private final Map<String, List<EventListener>> eventMap = new HashMap<>();
    private FrameLayout loadingContainer;
    private View loading;

    public interface EventListener {
        void happened(String what);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        Util.setStatusBarColor(this, R.color.authing_status_bar_bg);

        Intent intent = getIntent();
        flow = (AuthFlow) intent.getSerializableExtra(AUTH_FLOW);
        if (flow != null) {
            int layoutId = intent.getIntExtra(CONTENT_LAYOUT_ID, 0);
            if (layoutId == 0) {
                layoutId = flow.getIndexLayoutId();
            }
            if (layoutId != 0) {
                setContentView(layoutId);
            }
        }

        if (Authing.isGettingConfig()) {
            FrameLayout rootLayout = findViewById(android.R.id.content);
            loadingContainer = new FrameLayout(this);
            loadingContainer.setBackgroundColor(0xffffffff);
            loading = new CircularAnimatedView(this);
            int size = (int)Util.dp2px(this, 88);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
            lp.gravity = Gravity.CENTER;
            loading.setLayoutParams(lp);
            loading.setVisibility(View.INVISIBLE);
            loadingContainer.addView(loading);
            rootLayout.addView(loadingContainer);

            // some tolerance for blank screen. otherwise the loading will blink
            new Handler().postDelayed(this::showLoading, 500);
        } else if (Authing.getAppId() == null) {
            Toast.makeText(this, R.string.authing_uninitialized, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Class.forName("com.ss.android.larksso.LarkSSO");
            LarkSSO.inst().parseIntent(this, getIntent());
        } catch( ClassNotFoundException e ) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Class.forName("com.ss.android.larksso.LarkSSO");
            LarkSSO.inst().parseIntent(this, intent);
        } catch( ClassNotFoundException e ) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Class.forName("com.ss.android.larksso.LarkSSO");
            LarkSSO.inst().parseIntent(this, data);
        } catch( ClassNotFoundException e ) {
        }
        if (requestCode == RC_LOGIN && resultCode == OK && data != null) {
            UserInfo userInfo = (UserInfo)data.getSerializableExtra("user");
            Intent intent = new Intent();
            intent.putExtra("user", userInfo);
            setResult(OK, intent);
            finish();
        }
        if (requestCode == Google.RC_SIGN_IN && data != null) {
            data.setAction("cn.authing.guard.broadcast.GOOGLE_LOGIN");
            sendBroadcast(data);
        }
        FaceBook.getInstance().onActivityResult(requestCode, resultCode, data);
    }


    private void showLoading() {
        if (!Authing.isGettingConfig()) {
            loadingContainer.setVisibility(View.GONE);
            return;
        }

        // still requesting config. show loading for some minimum time to avoid blinking
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(()-> Authing.getPublicConfig((config)-> {
            if (config == null) {
                FrameLayout rootLayout = findViewById(android.R.id.content);
                loading.setVisibility(View.GONE);
                TextView tv = new TextView(this);
                tv.setText(R.string.authing_no_network);
                FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvlp.gravity = Gravity.CENTER;
                tv.setLayoutParams(tvlp);
                rootLayout.addView(tv);
            } else {
                loadingContainer.setVisibility(View.GONE);
            }
        }), 888);
    }

    public AuthFlow getFlow() {
        return flow;
    }

    public void setFlow(AuthFlow flow) {
        this.flow = flow;
    }

    public void subscribe(String channel, EventListener listener) {
        if (!TextUtils.isEmpty(channel) && listener != null) {
            List<EventListener> fans = eventMap.computeIfAbsent(channel, k -> new ArrayList<>());
            fans.add(listener);
        }
    }

    public void fire(String channel, String what) {
        if (TextUtils.isEmpty(channel)) {
            return;
        }

        List<EventListener> fans = eventMap.get(channel);
        if (fans != null) {
            for (EventListener listener : fans) {
                listener.happened(what);
            }
        }
    }
}
