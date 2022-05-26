package cn.authing.otp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AuthenticatorDetailDialog extends Dialog {

    private Context mContext;
    private TextView mAccount;
    private TextView mDigits;
    private TextView mInterval;
    private TextView mAlgorithm;
    private TextView mIssuer;

    private TOTPEntity mData;

    public AuthenticatorDetailDialog(Context context) {
        super(context);
        initView(context);
    }

    public AuthenticatorDetailDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected AuthenticatorDetailDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = d.getHeight();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.authing_authenticator_detail, null);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        mAccount = view.findViewById(R.id.text_account);
        mDigits = view.findViewById(R.id.text_digits);
        mInterval = view.findViewById(R.id.text_interval);
        mAlgorithm = view.findViewById(R.id.text_algorithm);
        mIssuer = view.findViewById(R.id.text_issuer);
        Button button = view.findViewById(R.id.btn_delete);
        button.setBackgroundColor(Color.parseColor("#d81e06"));
        button.setOnClickListener(v -> {
            if (null == mData){
                Toast.makeText(mContext, mContext.getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.sure_delete))
                    .setPositiveButton(mContext.getString(R.string.ok), (dialog, which) -> {
                        TOTP.delete(mContext, mData);
                        dismiss();
                    })
                    .setNegativeButton(mContext.getString(R.string.cancel), (dialog, which) -> dialog.dismiss()).show();
        });
    }

    @SuppressLint("DefaultLocale")
    public void setData(TOTPEntity data){
        if (null == data){
            return;
        }
        this.mData = data;
        mAccount.setText(data.getAccount());
        mDigits.setText(String.valueOf(data.getDigits()));
        mInterval.setText(String.format("%ds", data.getPeriod()));
        mAlgorithm.setText(data.getAlgorithm());
        mIssuer.setText(data.getIssuer());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

}
