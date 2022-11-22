package cn.authing.guard.mfa;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Safe;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.ImageUtil;

/**
 * OTP 二维码图片
 */
public class OtpQrCodeImageView extends AppCompatImageView {

    private Bitmap bitmap;

    public OtpQrCodeImageView(Context context) {
        this(context, null);
    }

    public OtpQrCodeImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OtpQrCodeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        AuthClient.getOtpQrCode((AuthCallback<JSONObject>) (code, message, data) -> {
            if (code == 200 && data != null) {
                if (data.has("qrcode_data_url")) {
                    try {
                        String qrcode_data_url = data.getString("qrcode_data_url");
                        bitmap = ImageUtil.stringToBitmap(qrcode_data_url);
                        if (bitmap != null) {
                            post(() -> setBackground(new BitmapDrawable(getResources(), bitmap)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (data.has("recovery_code")) {
                    try {
                        String recovery_code = data.getString("recovery_code");
                        Safe.saveRecoveryCode(recovery_code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        setOnLongClickListener(v -> {
            if (bitmap != null) {
                showSaveImageDialog();
            }
            return false;
        });
    }

    private void showSaveImageDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(getContext().getString(R.string.authing_bind_otp_save_qr_code))
                .setPositiveButton(R.string.authing_confirm, (dialog, which) -> saveImage())
                .setNegativeButton(R.string.authing_cancel, null)
                .show();
    }

    private void saveImage() {
        int REQUEST_CODE_CONTACT = 101;
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //验证是否许可权限
        boolean hasPermission = true;
        for (String str : permissions) {
            if (getContext().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                if (getContext() instanceof AuthActivity) {
                    ((AuthActivity) getContext()).setQrCodeBitmap(bitmap);
                }
                ((Activity) getContext()).requestPermissions(permissions, REQUEST_CODE_CONTACT);
                hasPermission = false;
            }
        }

        if (hasPermission) {
            ImageUtil.saveImage(getContext(), bitmap);
        }
    }


}
