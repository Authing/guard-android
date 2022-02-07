package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;

public class AuthActivity extends AppCompatActivity {

    public static final int RC_LOGIN = 1024;
    public static final int OK = 42;

    public static final String AUTH_FLOW = "auth_flow";
    public static final String CONTENT_LAYOUT_ID = "content_layout_id";

    public static final String EVENT_VERIFY_CODE_ENTERED = "verify_code_entered";

    protected AuthFlow flow;

    private Map<String, List<EventListener>> eventMap = new HashMap<>();

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == OK && data != null) {
            UserInfo userInfo = (UserInfo)data.getSerializableExtra("user");
            Intent intent = new Intent();
            intent.putExtra("user", userInfo);
            setResult(OK, intent);
            finish();
        }
    }

    public AuthFlow getFlow() {
        return flow;
    }

    public void setFlow(AuthFlow flow) {
        this.flow = flow;
    }

    public void subscribe(String channel, EventListener listener) {
        if (!TextUtils.isEmpty(channel) && listener != null) {
            List<EventListener> fans = eventMap.get(channel);
            if (fans == null) {
                fans = new ArrayList<>();
                eventMap.put(channel, fans);
            }
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
