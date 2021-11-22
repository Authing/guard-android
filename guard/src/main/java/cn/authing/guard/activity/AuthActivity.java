package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;

public class AuthActivity extends AppCompatActivity {

    public static final int RC_LOGIN = 1024;
    public static final int OK = 42;

    public static final String AUTH_FLOW = "auth_flow";
    public static final String CONTENT_LAYOUT_ID = "content_layout_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Intent intent = getIntent();
        AuthFlow flow = (AuthFlow) intent.getSerializableExtra(AUTH_FLOW);
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
        if (requestCode == RC_LOGIN && resultCode == OK) {
            UserInfo userInfo = (UserInfo)data.getSerializableExtra("user");
            Intent intent = new Intent();
            intent.putExtra("user", userInfo);
            setResult(OK, intent);
            finish();
        }
    }
}
