package cn.authing.guard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;

public class AuthingRegisterActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStyle.setEditTextBackground(0);
        GlobalStyle.setEditTextLayoutBackground((R.drawable.authing_edit_text_layout_background));

        setContentView(R.layout.activity_register_authing);
    }
}