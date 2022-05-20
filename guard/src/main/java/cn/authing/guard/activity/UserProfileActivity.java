package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class UserProfileActivity extends BaseAuthActivity {

    private UserProfileFragment fragment;
    protected Button logoutButton;
    protected Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setStatusBarColor(this, R.color.authing_background);
        setContentView(R.layout.authing_activity_user_profile);
        fragment = (UserProfileFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_user_profile);
        if (null != fragment){
            logoutButton = fragment.getLogoutButton();
            deleteButton = fragment.getDeleteButton();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000) {
            fragment.uploadAvatar(data.getData());
        }
    }

}
