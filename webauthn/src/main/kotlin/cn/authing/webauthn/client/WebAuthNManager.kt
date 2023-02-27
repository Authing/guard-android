package cn.authing.webauthn.client

import android.os.Build
import androidx.fragment.app.FragmentActivity
import cn.authing.webauthn.data.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import cn.authing.webauthn.authenticator.COSEAlgorithmIdentifier
import cn.authing.webauthn.authenticator.internal.ui.UserConsentUI
import cn.authing.webauthn.authenticator.internal.ui.UserConsentUIFactory
import cn.authing.webauthn.util.ByteArrayUtil
import java.util.*

interface WebAuthAttestationCallback {
    fun onResult(rep: PublicKeyCredential<AuthenticatorAttestationResponse>)
    fun onError(error: String);
}

interface WebAuthAssertionCallback {
    fun onResult(rep: PublicKeyCredential<AuthenticatorAssertionResponse>)
    fun onError(error: String);
}

class WebAuthManager(val activity: FragmentActivity) {

    lateinit var attestationCallback: WebAuthAttestationCallback
    lateinit var assertionCallback: WebAuthAssertionCallback
    var consentUI: UserConsentUI? = null
    var webAuthnClient: WebAuthnClient? = null

    fun startAttestation(
        userId: String,
        username: String,
        userDisplayName: String,
        userIconURL: String,
        relyingParty: String,
        relyingPartyICON: String,
        challenge: String,
        userVerification: UserVerificationRequirement,
        attestationConveyance: AttestationConveyancePreference,
        attestationCallback: WebAuthAttestationCallback
    ) {
        this.attestationCallback = attestationCallback
        GlobalScope.launch {
            onExecute(
                userId = userId,
                username = username,
                userDisplayName = userDisplayName,
                userIconURL = userIconURL,
                relyingParty = relyingParty,
                relyingPartyICON = relyingPartyICON,
                challenge = challenge,
                userVerification = userVerification,
                attestationConveyance = attestationConveyance
            )
        }
    }

    private suspend fun onExecute(
        userId: String,
        username: String,
        userDisplayName: String,
        userIconURL: String,
        relyingParty: String,
        relyingPartyICON: String,
        challenge: String,
        userVerification: UserVerificationRequirement,
        attestationConveyance: AttestationConveyancePreference
    ) {
        val options = PublicKeyCredentialCreationOptions()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            options.challenge = Base64.getUrlDecoder().decode(challenge)
        } else {
            options.challenge = ByteArrayUtil.decodeBase64URL(challenge);
        }
        options.user.id = ByteArrayUtil.decodeBase64URL(userId)
        options.user.name = username
        options.user.displayName = userDisplayName
        options.user.icon = userIconURL
        options.rp.id = relyingParty
        options.rp.name = relyingParty
        options.rp.icon = relyingPartyICON
        options.attestation = attestationConveyance

        options.addPubKeyCredParam(
            alg = COSEAlgorithmIdentifier.es256
        )

        options.authenticatorSelection = AuthenticatorSelectionCriteria(
            requireResidentKey = true,
            userVerification = userVerification
        )

        val webAuthnClient = createWebAuthnClient(relyingParty)

        try {
            val cred = webAuthnClient.create(options)
            attestationCallback.onResult(cred)
        } catch (e: Exception) {
            attestationCallback.onError(e.toString())
        }
    }

    private fun createWebAuthnClient(relyingParty: String): WebAuthnClient {
        consentUI = UserConsentUIFactory.create(activity)
        val webAuthnClient = WebAuthnClient.create(
            activity = activity,
            origin = "https://$relyingParty",
            ui = consentUI!!
        )
        webAuthnClient.maxTimeout = 30
        webAuthnClient.defaultTimeout = 20
        return webAuthnClient
    }

    fun startAssertion(
        relyingParty: String,
        challenge: String,
        credId: String,
        userVerification: UserVerificationRequirement,
        assertionCallback: WebAuthAssertionCallback
    ) {
        this.assertionCallback = assertionCallback;
        GlobalScope.launch {
            onExecute(
                relyingParty = relyingParty,
                challenge = challenge,
                credId = credId,
                userVerification = userVerification
            )
        }
    }

    private suspend fun onExecute(
        relyingParty: String, challenge: String,
        credId: String, userVerification: UserVerificationRequirement
    ) {
        val options = PublicKeyCredentialRequestOptions()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            options.challenge = Base64.getUrlDecoder().decode(challenge)
        } else {
            options.challenge = ByteArrayUtil.decodeBase64URL(challenge);
        }
        options.rpId = relyingParty
        options.userVerification = userVerification
        var credentialId = ByteArrayUtil.decodeBase64URL(credId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            credentialId = Base64.getUrlDecoder().decode(credId)
        }
        if (credId.isNotEmpty()) {
            options.addAllowCredential(
                credentialId = credentialId,
                transports = mutableListOf(AuthenticatorTransport.Internal)
            )
        }
        webAuthnClient = createWebAuthnClient(relyingParty)
        try {
            val cred = webAuthnClient!!.get(options)
            assertionCallback.onResult(cred)
        } catch (e: Exception) {
            assertionCallback.onError(e.toString())
        } finally {
            consentUI = null
        }
    }

}