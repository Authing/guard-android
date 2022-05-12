package cn.authing;

import android.content.Intent;
import android.os.Bundle;

import cn.authing.guard.activity.UserProfileActivity;

public class MainActivity extends UserProfileActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLogoutSuccess() {
        super.onLogoutSuccess();
        Intent intent = new Intent(this, SampleListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAccountDeletedSuccess() {
        super.onAccountDeletedSuccess();
    }

}
