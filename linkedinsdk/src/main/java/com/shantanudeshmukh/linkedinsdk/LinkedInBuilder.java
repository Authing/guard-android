package com.shantanudeshmukh.linkedinsdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.shantanudeshmukh.linkedinsdk.helpers.OnBasicProfileListener;

import java.util.Random;

public class LinkedInBuilder {

    public static final String TAG = "LinkedInAuth";

    private final Activity context;
    private final Intent intent;
    private String state;
    private int authType = AUTH_CODE;

    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String STATE = "state";
    public static final String AUTH_TYPE = "auth_type";

    public static final int AUTH_CODE = 1;
    public static final int AUTH_TOKEN = 2;
    public static final int AUTH_USER = 3;

    public static final int ERROR_USER_DENIED = 11;
    public static final int ERROR_FAILED = 12;


    private LinkedInBuilder(Activity context) {
        this.context = context;
        this.intent = new Intent(context, LinkedInAuthenticationActivity.class);
    }

    public static LinkedInBuilder getInstance(Activity context) {
        return new LinkedInBuilder(context);
    }

    public LinkedInBuilder setClientID(String clientID) {
        intent.putExtra(CLIENT_ID, clientID);
        return this;
    }

    public LinkedInBuilder setClientSecret(String clientSecret) {
        intent.putExtra(CLIENT_SECRET_KEY, clientSecret);
        return this;
    }

    public LinkedInBuilder setRedirectURI(String redirectURI) {
        intent.putExtra(REDIRECT_URI, redirectURI);
        return this;
    }

    public LinkedInBuilder setAuthType(int authType) {
        this.authType = authType;
        intent.putExtra(AUTH_TYPE, authType);
        return this;
    }

    public LinkedInBuilder setState(String state) {
        this.state = state;
        intent.putExtra(STATE, state);
        return this;
    }

    public void authenticate(int requestCode) {
        if (validateAuthenticationParams()) {
            if (state == null) {
                generateState();
            }
            context.startActivityForResult(intent, requestCode);
        }
    }

    private boolean validateAuthenticationParams() {

        if (intent.getStringExtra(CLIENT_ID) == null) {
            Log.e(TAG, "Client ID is required", new IllegalArgumentException());
            return false;
        }

        if (authType != AUTH_CODE) {
            if (intent.getStringExtra(CLIENT_SECRET_KEY) == null) {
                Log.e(TAG, "Client Secret is required", new IllegalArgumentException());
                return false;
            }
        }

        if (intent.getStringExtra(REDIRECT_URI) == null) {
            Log.e(TAG, "Redirect URI is required", new IllegalArgumentException());
            return false;
        }

        return true;
    }

    private void generateState() {
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmMNBVCXZLKJHGFDSAQWERTYUIOP";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        this.state = sb.toString();
        intent.putExtra(STATE, state);
    }

    public static void retrieveBasicProfile(@NonNull String accessToken, long accessTokenExpiry, @NonNull OnBasicProfileListener onBasicProfileListener) {
        new RetrieveBasicProfileAsyncTask(accessToken, accessTokenExpiry, onBasicProfileListener).execute();
    }

}
