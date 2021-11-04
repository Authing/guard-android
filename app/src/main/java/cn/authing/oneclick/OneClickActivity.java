package cn.authing.oneclick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import cn.authing.R;

public class OneClickActivity extends AppCompatActivity {

    private final static String TAG = "OneClickActivity";

    private boolean prefetchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);

        final QuickLogin quickLogin = QuickLogin.getInstance(this, "74ae90bd84f74b69a88b578bbbbcdcfd");
        quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                //预取号成功
                prefetchResult = true;
                Log.d(TAG, "Got phone:" + mobileNumber);
            }

            @Override
            public void onGetMobileNumberError(String YDToken, String msg) {
                Log.e(TAG, "Got phone error:" + msg);
            }
        });

        Button btn = findViewById(R.id.btn_one_click);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefetchResult) {
                    quickLogin.onePass(new QuickLoginTokenListener() {
                        @Override
                        public void onGetTokenSuccess(String YDToken, String accessCode) {
                            quickLogin.quitActivity();
                            //一键登录成功 运营商token：accessCode获取成功
                            //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                            Log.e(TAG, "onGetTokenSuccess:" + accessCode);
                        }

                        @Override
                        public void onGetTokenError(String YDToken, String msg) {
                            quickLogin.quitActivity();
                            Log.e(TAG, "onGetTokenError:" + msg);
                        }
                    });
                }
            }
        });
    }
}