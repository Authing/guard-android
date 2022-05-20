package cn.authing.push;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cn.authing.guard.activity.AuthActivity;

public class DeepLinkActivity extends AuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_activity_deep_link);
        Intent intent = getIntent();
        if (null != intent) {
            String sessionId = intent.getStringExtra("sessionId");
            Push.authConfirm(sessionId, ((ok, data) -> runOnUiThread(()-> handleResult(ok, data))));
        }
    }

    private void handleResult(boolean ok, String message) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        if (ok) {
            finish();
        } else {
            Toast.makeText(this, "Auth failed " + message, Toast.LENGTH_SHORT).show();
        }
    }
}