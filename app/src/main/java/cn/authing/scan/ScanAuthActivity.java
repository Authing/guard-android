package cn.authing.scan;

import static cn.authing.guard.activity.AuthActivity.OK;
import static cn.authing.guard.activity.AuthActivity.RC_LOGIN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dommy.qrcode.util.Constant;
import com.google.zxing.activity.CaptureActivity;

import org.json.JSONObject;

import cn.authing.R;
import cn.authing.guard.Authing;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.activity.AppScanLoginActivity;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.ToastUtil;

public class ScanAuthActivity extends AppCompatActivity {

    private static final String TAG = "ScanAuthActivity";

    final int REQ_PERM_CAMERA = 1000;
    int REQ_QR_CODE = 1001;
    Button btnScan;
    TextView tvRes;

    String random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_auth);

        btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener((v -> clicked()));

        if (null != Authing.getCurrentUser()){
            btnScan.setText("Start scan");
        }

        tvRes = findViewById(R.id.tv_res);
    }

    private void clicked() {
        if (Authing.getCurrentUser() == null) {
            AuthFlow.start(this);
        } else {
            startQrCode();
        }
    }

    private void startQrCode() {
        tvRes.setText("");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
            }
            // 申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQ_QR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == OK && requestCode == RC_LOGIN) {
                btnScan.setText("Start scan");
            } else if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
                ALog.d(TAG, "scan result:" + scanResult);
                try {
                    JSONObject obj = new JSONObject(scanResult);
                    if (obj.has("random")) {
                        random = obj.getString("random");
                        AuthClient.markQRCodeScanned(random, (code, message, data1) -> {
                            ALog.d(TAG, "markQRCodeScanned result:" + code + " " + message);
                            if (code == 200) {
                                Intent intent = new Intent(this, AppScanLoginActivity.class);
                                intent.putExtra("random", random);
                                startActivity(intent);
                            } else {
                                runOnUiThread(() -> {
                                    if (code == 2004){
                                        ToastUtil.showCenter(ScanAuthActivity.this, "扫描登录失败，用户不存在于 Web 端应用中！");
                                    } else {
                                        ToastUtil.showCenter(ScanAuthActivity.this, message);
                                    }
                                });
                                setRes(message);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void setRes(String res) {
        runOnUiThread(()->{
            tvRes.setText(res);
        });
    }
}