package cn.authing.ut;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cn.authing.R;
import cn.authing.guard.data.UserInfo;


public class UTTestAllActivity extends AppCompatActivity implements IHttpCallBack {

    private TextView resultText;
    private TextView resultSuccess;
    private TextView resultFail;
    private LinearLayout resultLayout;
    private ArrayList<TestCase> mAllTestList;
    private int count = 0;
    private int successCount = 0;
    private int failCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ut_test_all);

        Intent intent = getIntent();
        if (intent != null) {
            mAllTestList = intent.getParcelableArrayListExtra("data");
        }

        if (mAllTestList != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("全部用例");
                actionBar.setSubtitle(mAllTestList.size() + "");
            }
        }

        resultText = findViewById(R.id.text_result);
        resultSuccess = findViewById(R.id.text_success);
        resultFail = findViewById(R.id.text_fail);
        resultLayout = findViewById(R.id.result_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sync();

        //showResult("sendSms", 500, "未知错误", null);
    }

    private void sync() {
        resultLayout.removeAllViews();
        for (int i = 0; i < mAllTestList.size(); i++) {
            HttpUtil.sync(mAllTestList.get(i), this);
        }
    }

    @Override
    public void showResult(String apiName, int code, String message, UserInfo data) {
        runOnUiThread(() -> {
            count++;
            if (code == 500 || code == 404 || code == 408) {
                failCount++;
            } else {
                successCount++;
            }
            resultText.setText(count + "");
            resultSuccess.setText(successCount + "");
            resultFail.setText(failCount + "");

            addResult(apiName, code, message);
        });
    }


    private void addResult(String apiName, int code, String message) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        String text = apiName + " : code = " + code + " message = " + message;
        textView.setText(text);

        //部分文字改变颜色
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        ForegroundColorSpan graySpan = new ForegroundColorSpan(Color.GRAY);
        ForegroundColorSpan readSpan = new ForegroundColorSpan(Color.RED);
        //这里注意一定要先给textview赋值
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        //为不同位置字符串设置不同颜色
        builder.setSpan(blueSpan, 0, apiName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (code == 500 || code == 404 || code == 408) {
            int startIndex = text.indexOf(String.valueOf(code));
            int endIndex = text.indexOf(String.valueOf(code)) + String.valueOf(code).length();
            builder.setSpan(readSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //builder.setSpan(graySpan, endIndex, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } //else {
        //builder.setSpan(graySpan, apiName.length(), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //}
        //最后为textview赋值
        textView.setText(builder);

        resultLayout.addView(textView);
    }

}
