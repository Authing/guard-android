package cn.authing.ut;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import cn.authing.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.LoadingButton;


public class UTTestActivity extends AppCompatActivity implements IHttpCallBack {

    private TestCase testCase;
    private TextView resultText;
    private LoadingButton button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ut_test);

        Intent intent = getIntent();
        if (intent != null) {
            testCase = intent.getParcelableExtra("data");
        }

        if (testCase != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(testCase.getModuleName());
                actionBar.setSubtitle(testCase.getCaseName());
            }
            TextView apiName = findViewById(R.id.text_api_name);
            apiName.setText(testCase.getApiName());
            TextView apiMethod = findViewById(R.id.text_api_method);
            apiMethod.setText(testCase.getMethod());
            TextView url = findViewById(R.id.text_url);
            url.setText(testCase.getUrl());
            TextView params = findViewById(R.id.text_params);
            params.setText(testCase.getParams());
        }

        resultText = findViewById(R.id.text_result);
        button = findViewById(R.id.btn_test);
        button.setOnClickListener(v -> sync());

    }


    private void sync() {
        HttpUtil.sync(testCase, this);
        button.startLoadingVisualEffect();
    }

    @Override
    public void showResult(String apiName, int code, String message, UserInfo data) {
        runOnUiThread(() -> {
            button.stopLoadingVisualEffect();
            if (code == 200) {
                resultText.setTextColor(Color.GREEN);
            } else {
                resultText.setTextColor(Color.RED);
            }
            CharSequence text = resultText.getText();
            if (!TextUtils.isEmpty(text)) {
                text += "\n\n";
            }
            String result = text + "code : " + code
                    + "\nmessage : " + message
                    + "\ndata : " + data;
            resultText.setText(result);
        });
    }
}
