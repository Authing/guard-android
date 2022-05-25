package cn.authing.appauth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.EndSessionRequest;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;

import cn.authing.R;
import cn.authing.guard.Authing;
import cn.authing.guard.activity.UserProfileActivity;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class AppAuthActivity extends AppCompatActivity {

    private static final String TAG = "AppAuthActivity";

    private static String REDIRECT_URL = "cn.guard://authing.cn/redirect";

    private static final int RC_AUTH = 1000;
    private static final int RC_END = 1001;

    AuthorizationService authService;
    AuthState authState;
    AuthorizationServiceConfiguration configuration;

    LoadingButton btn;

    UserInfo userInfo = new UserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_auth);
        btn = findViewById(R.id.btn_loading);

        if (Authing.getCurrentUser() == null){
            btn.startLoadingVisualEffect();
            Authing.getPublicConfig(config -> {
                if (config != null && config.getRedirectUris().size() > 0) {
                    REDIRECT_URL = config.getRedirectUris().get(0);
                }
                String identifier = config.getIdentifier();
                Uri authEndpoint = Uri.parse(Authing.getScheme() + "://" + identifier + "." + Authing.getHost() + "/oidc/auth");
                Uri tokenEndpoint = Uri.parse(Authing.getScheme() + "://" + identifier + "." + Authing.getHost() + "/oidc/token");
                Uri regEndpoint = Uri.parse(Authing.getScheme() + "://" + identifier + "." + Authing.getHost() + "/oidc/reg");
                String host = Util.getHost(config);
                Uri endEndpoint = Uri.parse(Authing.getScheme() + "://" + host + "/login/profile/logout?redirect_uri=" + REDIRECT_URL);
                configuration = new AuthorizationServiceConfiguration(authEndpoint, tokenEndpoint, regEndpoint, endEndpoint);
                AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(Authing.getScheme() + "://" + identifier + "." + Authing.getHost() + "/oidc"),
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


        findViewById(R.id.btn_user_profile).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_user_profile_web).setOnClickListener(v -> {
            Authing.getPublicConfig(config -> {
                Intent browserIntent = new Intent();
                browserIntent.setAction("android.intent.action.VIEW");
                //browserIntent.setPackage("com.android.chrome");
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/u?app_id=" + Authing.getAppId() + "&back_app_url=cn.guard://appauth/redirect";
                Uri content_url = Uri.parse(url);
                browserIntent.setData(content_url);
                startActivity(browserIntent);
            });
        });

        findViewById(R.id.btn_logout).setOnClickListener((v -> {
            Authing.getPublicConfig(config -> {
                EndSessionRequest request = new EndSessionRequest.Builder(configuration)
                        .setIdTokenHint(userInfo.getIdToken())
                        .setPostLogoutRedirectUri(Uri.parse(REDIRECT_URL))
                        .build();

                authService = new AuthorizationService(this);
                Intent authIntent = authService.getEndSessionRequestIntent(request);
                startActivityForResult(authIntent, RC_END);
            });
        }));
    }

    private void startAuth(AuthorizationServiceConfiguration serviceConfig) {

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        Authing.getAppId(), // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        Uri.parse(REDIRECT_URL)); // the redirect URI to which the auth response is sent

        AuthorizationRequest authRequest = authRequestBuilder
                .setScope("openid profile email phone address offline_access role extended_fields")
                .setPrompt("consent") // for refresh token
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
                            Log.d(TAG, resp1.idToken);
                            Log.d(TAG, "at:" + resp1.accessToken);
                            Log.d(TAG, "rt:" + resp1.refreshToken);
                            userInfo.setIdToken(resp1.idToken);
                            btn.setVisibility(View.GONE);
                            getUserInfo(resp1.accessToken, resp1.refreshToken);
                        });
                    } else {
                        // authorization failed, check ex for more details
                    }
                });
        } else if (requestCode == RC_END)  {
            ALog.d(TAG, "logged out");
            Safe.logoutUser(Authing.getCurrentUser());
            Authing.setCurrentUser(null);
        }
    }

    private void getUserInfo(String accessToken, String refreshToken) {
        userInfo.setAccessToken(accessToken);
        userInfo.setRefreshToken(refreshToken);
        OIDCClient.getUserInfoByAccessToken(userInfo, (code, message, data)->{
            if (code == 200) {
//                updateToken(data.getRefreshToken());
            }
        });
    }

    private void updateToken(String rt) {
        OIDCClient.getNewAccessTokenByRefreshToken(rt, (code, message, data)->{
            if (code == 200) {
                Log.d(TAG, "new at:" + data.getAccessToken());
                Log.d(TAG, "new id token:" + data.getIdToken());
                Log.d(TAG, "new rt:" + data.getRefreshToken());

                runOnUiThread(()->{
                    AuthFlow.showUserProfile(this);
                });
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            Log.d(TAG, "onNewIntent uri = " + uri);
            if (uri != null) {
                boolean isAccountDeleted = uri.getBooleanQueryParameter("is_account_deleted", false);
                if (isAccountDeleted){
                    Safe.logoutUser(Authing.getCurrentUser());
                    Authing.setCurrentUser(null);
                }
            }
        }
    }

}