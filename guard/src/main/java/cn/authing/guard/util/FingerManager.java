package cn.authing.guard.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import cn.authing.guard.R;

public class FingerManager {

    public void startBiometric(AppCompatActivity activity, FingerCallback fingerCallback) {
        if (activity == null) {
            return;
        }
        Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt biometricPrompt = new BiometricPrompt(activity,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                ToastUtil.showCenter(activity, "" + errString);
                if (fingerCallback != null) {
                    fingerCallback.onError();
                }
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (fingerCallback != null) {
                    fingerCallback.onSucceeded();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                ToastUtil.showCenter(activity, "Authentication failed");
                if (fingerCallback != null) {
                    fingerCallback.onFailed();
                }
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.authing_biometric_title))
                .setSubtitle(activity.getString(R.string.authing_biometric_tip))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText(activity.getString(R.string.authing_cancel))
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    public interface FingerCallback {
        void onError();

        void onSucceeded();

        void onFailed();
    }
}
