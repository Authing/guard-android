package cn.authing.guard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.profile.UpdateCustomDataButton;

public class UpdateCustomDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_profile_update_custom_data);

        UserInfo.CustomData data = (UserInfo.CustomData) getIntent().getSerializableExtra("data");
        if (data == null) {
            return;
        }

        EditTextLayout et = findViewById(R.id.et_value);
        et.getEditText().setText(data.getValue());

        UpdateCustomDataButton btn = findViewById(R.id.btn_update_custom_data);
        btn.setOnClickListener((v -> {
            try {
                btn.startLoadingVisualEffect();
                JSONObject object = new JSONObject();
                object.put(data.getKey(), et.getEditText().getText());
                AuthClient.updateCustomUserInfo(object, ((code, message, res) -> {
                    btn.stopLoadingVisualEffect();
                    if (code == 200) {
                        UserInfo user = Authing.getCurrentUser();
                        user.setCustomData(data.getKey(), et.getEditText().getText().toString());
                        finish();
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}