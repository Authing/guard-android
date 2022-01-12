package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URL;
import java.util.List;
import java.util.Map;

import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.PKCE;
import cn.authing.guard.util.Util;

public class WebAuthView extends WebView {

    private static final String TAG = "WebAuthView";

    private String host;
    private WebAuthViewCallback callback;
    private String codeVerifier;

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

    public WebAuthView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);

        codeVerifier = PKCE.generateCodeVerifier();

        Authing.getPublicConfig(config -> {
            host = config.getIdentifier();
            String url = "https://" + host + ".authing.cn/login?app_id=" + Authing.getAppId()
                    + "&code_challenge=" + PKCE.generateCodeChallenge(codeVerifier)
                    + "&code_challenge_method=" + PKCE.getCodeChallengeMethod();
            loadUrl(url);
        });

        setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith(Const.DEFAULT_REDIRECT_URL)) {
                    try {
                        URL u = new URL(url);
                        Map<String, List<String>> map = Util.splitQuery(u, "UTF-8");
                        if (map.containsKey("code")) {
                            String authCode = map.get("code").get(0);
                            AuthClient.authByCode(authCode, codeVerifier, Const.DEFAULT_REDIRECT_URL, (code, message, userInfo) -> {
                                fireCallback(userInfo);
                            });
                        } else {
                            ALog.e(TAG, url);
                            fireCallback(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void setOnLoginCallback(WebAuthViewCallback callback) {
        this.callback = callback;
    }

    private void fireCallback(UserInfo userInfo) {
        if (callback != null) {
            callback.call(userInfo);
        }
    }
}
