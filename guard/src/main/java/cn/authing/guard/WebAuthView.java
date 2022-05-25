package cn.authing.guard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;
import java.util.List;
import java.util.Map;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthRequest;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class WebAuthView extends WebView {

    public interface WebViewListener {
        void onLoaded();
    }

    private static final String TAG = "WebAuthView";

    private final AuthRequest authRequest = new AuthRequest();
    private WebViewListener listener;
    private boolean loadingEventFired;
    private WebAuthViewCallback callback;

    public interface WebAuthViewCallback {
        void call(UserInfo userInfo);
    }

    public WebAuthView(@NonNull Context context) {
        super(context);
        init();
    }

    public WebAuthView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebAuthView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        Analyzer.report("WebAuthView");

        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                ALog.d(TAG, "shouldOverrideUrlLoading:" + url);

                Uri uri = Uri.parse(url);
                String uuid = Util.getQueryParam(url, "uuid");
                if (uuid != null) {
                    authRequest.setUuid(uuid);
                }

                if (url.startsWith(authRequest.getRedirectURL())) {
                    try {
                        String authCode = Util.getAuthCode(url);
                        if (authCode != null) {
                            OIDCClient.authByCode(authCode, authRequest, (code, message, userInfo) -> fireCallback(code, message, userInfo));
                        } else {
                            ALog.e(TAG, url);
                            fireCallback(500, "login failed", null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if ("authz".equals(uri.getLastPathSegment()) && authRequest.getUuid() != null) {
                    if (getContext() instanceof AuthActivity) {
                        AuthActivity activity = (AuthActivity) getContext();
                        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                        if (flow.isSkipConsent()) {
                            skipConsent(uri);
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
//                ALog.d(TAG, "onLoadResource:" + url);
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                ALog.e(TAG, "onReceivedError:" + request.getUrl());
                handleAuthCode(request.getUrl().toString());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
//                ALog.e(TAG, "onReceivedHttpError:" + request.getUrl());
                if (errorResponse.getStatusCode() == 400) {
                    if (listener != null) {
                        listener.onLoaded();
                    }
                } else {
                    handleAuthCode(request.getUrl().toString());
                }
            }

            private void handleAuthCode(String url) {
                if (url.startsWith(authRequest.getRedirectURL())) {
                    try {
                        String authCode = Util.getAuthCode(url);
                        if (authCode != null) {
                            OIDCClient.authByCode(authCode, authRequest, (code, message, userInfo) -> fireCallback(code, message, userInfo));
                        } else {
                            ALog.e(TAG, url);
                            fireCallback(500, "login failed", null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // TODO login page loaded twice. Remove it after back end fix
            int count;
            @Override
            public void onPageFinished(WebView view, String url) {
                ALog.d(TAG, "onPageFinished:" + url);
                if (listener != null && "login".equals(Uri.parse(url).getLastPathSegment())) {
                    try {
                        URI u = new URI(url);
                        Map<String, List<String>> map = Util.splitQuery(u);
                        if (map.containsKey("uuid")) {
                            if (count == 1) {
                                postDelayed(()->{
                                    listener.onLoaded();
                                    loadingEventFired = true;
                                }, 300);
                            } else {
                                count++;
                                postDelayed(()->{
                                    if (!loadingEventFired) {
                                        listener.onLoaded();
                                    }
                                }, 3000);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Authing.getPublicConfig(config -> {
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                authRequest.setScope(flow.getScope());
            }
            post(()-> OIDCClient.buildAuthorizeUrl(authRequest, (ok, data) -> loadUrl(data)));
        });
    }

    public void setListener(WebViewListener listener) {
        this.listener = listener;
    }

    public void setOnLoginCallback(WebAuthViewCallback callback) {
        this.callback = callback;
    }

    private void fireCallback(int code, String message, UserInfo userInfo) {
        if (callback != null) {
            post(()-> callback.call(userInfo));
        } else if (code == 200) {
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                if (cb != null) {
                    cb.call(getContext(), code, message, userInfo);
                }

                Intent intent = new Intent();
                intent.putExtra("user", userInfo);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
            }
        }
    }

    private void skipConsent(Uri uri) {
        String url = uri.getScheme() + "://" + uri.getHost() + "/interaction/oidc/" + authRequest.getUuid() + "/confirm";
        String body = authRequest.getScopesAsConsentBody();
        ALog.d(TAG, "skipping consent:" + url);
        ALog.d(TAG, "skipping consent:" + body);
        String js = "(function f(){\n" +
            "var url = \"" + url + "\";\n" +
            "var xhr = new XMLHttpRequest();\n" +
            "console.log('executing skipping js');\n" +
            "xhr.onload = function() {\n" +
            "   console.log('status=' + xhr.status + ' responseURL=' + xhr.responseURL);" +
            "   if(xhr.status === 200) {\n" +
            "       window.location.href = xhr.responseURL;\n" +
            "   }\n" +
            "}\n" +
            "xhr.open('POST', url, true);\n" +
            "xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded; charset=utf-8');\n" +
            "xhr.send(\"" + body + "\");\n" +
        "})()";
        evaluateJavascript(js, null);
    }

    public AuthRequest getAuthRequest() {
        return authRequest;
    }
}
