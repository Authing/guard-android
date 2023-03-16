package cn.authing.guard.social.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import cn.authing.guard.R;
import cn.authing.guard.social.web.helpers.OnBasicProfileListener;
import cn.authing.guard.social.web.helpers.RequestHandler;
import cn.authing.guard.social.web.helpers.WebAuthUser;

public class WebAuthActivity extends AppCompatActivity {

    private static String CLIENT_ID;
    private static String CLIENT_SECRET_KEY;
    private static String STATE; //for security
    private static String SCOPE;
    private static String REDIRECT_URI;
    private static int AUTH_TYPE;
    private static String AUTHORIZATION_URL;
    private static String ACCESS_TOKEN_URL;

    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";
    private static final String SCOPE_PARAM = "scope";

    private WebView webView;
    private AlertDialog progressDialog;

    private WebAuthUser linkedInUser = new WebAuthUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_activity_web_auth);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        AUTH_TYPE = getIntent().getIntExtra(WebAuthBuilder.AUTH_TYPE, WebAuthBuilder.AUTH_CODE);
        AUTHORIZATION_URL = getIntent().getStringExtra(WebAuthBuilder.AUTHORIZATION_URL);
        ACCESS_TOKEN_URL = getIntent().getStringExtra(WebAuthBuilder.ACCESS_TOKEN_URL);
        CLIENT_ID = getIntent().getStringExtra(WebAuthBuilder.CLIENT_ID);
        CLIENT_SECRET_KEY = getIntent().getStringExtra(WebAuthBuilder.CLIENT_SECRET_KEY);
        REDIRECT_URI = getIntent().getStringExtra(WebAuthBuilder.REDIRECT_URI);
        STATE = getIntent().getStringExtra(WebAuthBuilder.STATE);
        SCOPE = getIntent().getStringExtra(WebAuthBuilder.SCOPE);

        webView = findViewById(R.id.web_view_auth_login);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.clearHistory();
        webView.clearCache(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);

        showProgressDialog();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                hideProgressDialog();
            }

            //to support below Android N we need to use the deprecated method only
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String authorizationUrl = request.getUrl().toString();
                showProgressDialog();

                if (authorizationUrl.startsWith(REDIRECT_URI)) {

                    Uri uri = Uri.parse(authorizationUrl);
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(STATE)) {
                        Log.e(WebAuthBuilder.TAG, "State token doesn't match");
                        return true;
                    }

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        Intent intent = new Intent();
                        intent.putExtra("err_code", WebAuthBuilder.ERROR_USER_DENIED);
                        intent.putExtra("err_message", "Authorization not received. User didn't allow access to account.");
                        setResult(Activity.RESULT_CANCELED, intent);
                        finish();
                    }

                    if (AUTH_TYPE == WebAuthBuilder.AUTH_CODE) {
                        linkedInUser.setCode(authorizationToken);
                        Intent intent = new Intent();
                        intent.putExtra("social_login", linkedInUser);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        new RetrieveDataAsyncTask().execute(authorizationToken);
                    }
                } else {
                    //Default behaviour
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }

        });

        String authUrl = getAuthorizationUrl();
        webView.loadUrl(authUrl);
    }


    private class RetrieveDataAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(String... tokens) {
            if (tokens.length > 0) {
                String authorizationToken = tokens[0];

                try {

                    retrieveAccessTokenFromAPI(authorizationToken);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return false;
                }

            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean didSuccess) {
            super.onPostExecute(didSuccess);

            if (linkedInUser.getAccessToken() != null) {
                if (AUTH_TYPE == WebAuthBuilder.AUTH_TOKEN) {
                    hideProgressDialog();
                    Intent intent = new Intent();
                    intent.putExtra("social_login", linkedInUser);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return;
                }

                WebAuthBuilder.retrieveBasicProfile(linkedInUser.getAccessToken(), linkedInUser.getAccessTokenExpiry(), new OnBasicProfileListener() {
                    @Override
                    public void onDataRetrievalStart() {
                    }

                    @Override
                    public void onDataSuccess(WebAuthUser linkedInUser) {
                        hideProgressDialog();
                        Intent intent = new Intent();
                        intent.putExtra("social_login", linkedInUser);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onDataFailed(int errCode, String errMessage) {
                        hideProgressDialog();
                        Intent intent = new Intent();
                        intent.putExtra("err_code", errCode);
                        intent.putExtra("err_message", errMessage);
                        setResult(Activity.RESULT_CANCELED, intent);
                        finish();
                    }
                });

            } else {

                hideProgressDialog();
                Intent intent = new Intent();
                intent.putExtra("err_code", WebAuthBuilder.ERROR_FAILED);
                intent.putExtra("err_message", "AUTHORIZATION FAILED");
                setResult(Activity.RESULT_CANCELED, intent);
                finish();

            }

        }
    }


    /**
     * Method that retrieves authentication token using authorization token
     */
    private void retrieveAccessTokenFromAPI(String authorizationToken) throws IOException, JSONException {
        String accessTokenUrl = getAccessTokenUrl(authorizationToken);
        String result = RequestHandler.sendPost(accessTokenUrl, new JSONObject());
        if (result != null) {
            JSONObject resultJson = new JSONObject(result);
            int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;
            String accessToken1 = resultJson.has("access_token") ? resultJson.getString("access_token") : null;

            if (expiresIn > 0 && accessToken1 != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, expiresIn);
                long expireDate = calendar.getTimeInMillis();
                linkedInUser.setAccessToken(accessToken1);
                linkedInUser.setAccessTokenExpiry(expireDate);
            } else {
                Log.e(WebAuthBuilder.TAG, "Access Token Expired or Doesn't exist");
            }
        } else {
            Log.e(WebAuthBuilder.TAG, "Failed To Retrieve Access Token");
        }
    }


    /**
     * Method that generates the url for get the access token from the Service
     *
     * @return String - access token url
     */
    private static String getAccessTokenUrl(String authorizationToken) {
        return ACCESS_TOKEN_URL
                + QUESTION_MARK
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + authorizationToken
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + CLIENT_SECRET_KEY;
    }


    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return String - authorization url
     */
    private static String getAuthorizationUrl() {
        return AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND + SCOPE_PARAM + EQUALS + SCOPE;
    }

    private void showProgressDialog() {
        if (!WebAuthActivity.this.isFinishing()) {
            if (progressDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WebAuthActivity.this);
                builder.setCancelable(false); // if you want user to wait for some process to finish,
                builder.setView(R.layout.authing_layout_web_auth_progress);
                progressDialog = builder.create();
            }
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (!WebAuthActivity.this.isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
