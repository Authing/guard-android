package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.PKCE;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class WebAuthView extends WebView {

    private static final String REDIRECT_URL = "https://guard.authing/redirect";
    private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");

    private String host;
    private WebAuthViewCallback callback;
    private String codeVerifier;

    public interface WebAuthViewCallback {
        void call(UserInfo userInfo);
    }

    public WebAuthView(@NonNull Context context) {
        this(context, null);
    }

    public WebAuthView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebAuthView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WebAuthView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

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
                if (url.startsWith(REDIRECT_URL)) {
                    try {
                        URL u = new URL(url);
                        Map<String, List<String>> map = Util.splitQuery(u, "UTF-8");
                        String code = map.get("code").get(0);
                        code2Token(code);
                    } catch (MalformedURLException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                // WebView不加载该Url
                return false;
            }
        });
    }

    public void setOnLoginCallback(WebAuthViewCallback callback) {
        this.callback = callback;
    }

    private void code2Token(String code) {
        new Thread() {
            public void run() {
                _code2Token(code);
            }
        }.start();
    }

    private void _code2Token(String code) {
        String url = "https://" + host + ".authing.cn/oidc/token";
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        String body = "client_id="+Authing.getAppId()
                + "&grant_type=authorization_code"
                + "&code=" + code
                + "&code_verifier=" + codeVerifier
                + "&redirect_uri=" + REDIRECT_URL;
        RequestBody requestBody = RequestBody.create(body, FORM);
        builder.post(requestBody);

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() == 201 || response.code() == 200) {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(s);
                UserInfo userInfo = new UserInfo();
                String accessToken = json.getString("access_token");
                String idToken = json.getString("id_token");
                userInfo.setAccessToken(accessToken);
                userInfo.setIdToken(idToken);
                fireCallback(userInfo);
            } else {
                fireCallback(null);
            }
        } catch (Exception e){
            e.printStackTrace();
            fireCallback(null);
        }
    }

    private void fireCallback(UserInfo userInfo) {
        if (callback != null) {
            callback.call(userInfo);
        }
    }
}
