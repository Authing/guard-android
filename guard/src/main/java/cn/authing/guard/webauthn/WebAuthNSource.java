package cn.authing.guard.webauthn;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.util.List;

import cn.authing.guard.data.Config;
import cn.authing.guard.util.Util;
import cn.authing.webauthn.authenticator.internal.CredentialStore;
import cn.authing.webauthn.authenticator.internal.PublicKeyCredentialSource;

public class WebAuthNSource {

    @SuppressLint("UnsafeOptInUsageWarning")
    public static List<PublicKeyCredentialSource> getSourceList(Activity activity, Config config) {
        CredentialStore credentialStore = new CredentialStore(activity);
        return credentialStore.loadAllCredentialSources(Util.getIdentifierHost(config));
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    public static void deleteSource(Activity activity, Config config, byte[] mUserHandler) {
        if (mUserHandler != null) {
            CredentialStore credentialStore = new CredentialStore(activity);
            credentialStore.deleteAllCredentialSources(Util.getIdentifierHost(config), mUserHandler);
        }
    }

    @SuppressLint("UnsafeOptInUsageWarning")
    public static void deleteAllSource(Activity activity, Config config) {
        CredentialStore credentialStore = new CredentialStore(activity);
        credentialStore.deleteAllCredentialSources(Util.getIdentifierHost(config));
    }
}
