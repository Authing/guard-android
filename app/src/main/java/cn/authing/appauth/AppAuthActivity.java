package cn.authing.appauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;

import cn.authing.R;
import cn.authing.guard.Authing;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.util.Util;

public class AppAuthActivity extends AppCompatActivity {

    private static final String TAG = "AppAuthActivity";

    private static final int RC_AUTH = 1000;

    AuthorizationService authService;
    AuthState authState;

    TextView tvTitle;
    TextView tvRes;
    LoadingButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_auth);

        tvTitle = findViewById(R.id.tv_title);
        tvRes = findViewById(R.id.tv_idtoken);
        btn = findViewById(R.id.btn_loading);

        btn.startLoadingVisualEffect();

        Authing.getPublicConfig(config -> {
            String host = config.getIdentifier();
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse("https://" + host + ".authing.cn/oidc"),
                    (serviceConfiguration, ex) -> {
                        if (ex != null) {
                            Log.e(TAG, "failed to fetch configuration");
                            return;
                        }

                        authState = new AuthState(serviceConfiguration);
                        startAuth(serviceConfiguration);
                    });
        });
    }

    private void startAuth(AuthorizationServiceConfiguration serviceConfig) {

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        Authing.getAppId(), // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        Uri.parse("cn.guard://authing.cn/redirect")); // the redirect URI to which the auth response is sent

        AuthorizationRequest authRequest = authRequestBuilder
                .setScope("openid profile email phone address offline_access role")
                .setPrompt("consent")
                .setCodeVerifier(Util.randomString(43))
                .build();

        authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        startActivityForResult(authIntent, RC_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_AUTH) {
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);
            // ... process the response or exception ...

            TokenRequest request = resp.createTokenExchangeRequest();
            authService.performTokenRequest(
                    request,
                    (resp1, ex1) -> {
                        if (resp1 != null) {
                            // exchange succeeded
                            authState.update(resp1, ex1);
                            runOnUiThread(()->{
                                tvTitle.setVisibility(View.VISIBLE);
                                tvRes.setText(resp1.idToken);
                                Log.d(TAG, resp1.idToken);
                                Log.d(TAG, "ak:" + resp1.accessToken);
                                Log.d(TAG, "rk:" + resp1.refreshToken);
                                btn.setVisibility(View.GONE);
                            });
                        } else {
                            // authorization failed, check ex for more details
                        }
                    });
        } else {
            // ...
        }
    }
}