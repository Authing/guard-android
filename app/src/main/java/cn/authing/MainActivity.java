package cn.authing;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import cn.authing.guard.activity.UserProfileActivity;
import cn.authing.guard.network.AuthClient;

public class MainActivity extends UserProfileActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
        findViewById(R.id.btn_delete).setOnClickListener(v -> delete());
    }

    private void logout() {
        AuthClient.logout((code, message, data)->{
            Intent intent = new Intent(this, SampleListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void delete() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(cn.authing.guard.R.string.authing_delete_account).setMessage(cn.authing.guard.R.string.authing_delete_account_tip)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> AuthClient.deleteAccount((code, message, data) -> {
                    if (code == 200) {
                        finish();
                    } else {
                        runOnUiThread(()-> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
                    }
                }))
                .setNegativeButton(android.R.string.no, null).show();
    }
}
