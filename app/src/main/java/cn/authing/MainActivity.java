package cn.authing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import cn.authing.guard.Authing;
import cn.authing.guard.activity.UserProfileActivity;
import cn.authing.guard.network.AuthClient;

public class MainActivity extends UserProfileActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btn = findViewById(R.id.btn_logout);
        btn.setOnClickListener(v -> logout());
    }

    private void logout() {
        AuthClient.logout((code, message, data)->{
            Intent intent = new Intent(this, SampleListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}