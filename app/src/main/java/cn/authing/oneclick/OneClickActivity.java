package cn.authing.oneclick;

import android.os.Bundle;
import cn.authing.R;
import cn.authing.guard.activity.AuthActivity;

public class OneClickActivity extends AuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click);
    }
}