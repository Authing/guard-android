package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import cn.authing.guard.R;

public class UserProfileActivity extends BaseAuthActivity {

    private UserProfileFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_activity_user_profile);
        fragment = (UserProfileFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_user_profile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000) {
            fragment.uploadAvatar(data.getData());
        }
    }
}
