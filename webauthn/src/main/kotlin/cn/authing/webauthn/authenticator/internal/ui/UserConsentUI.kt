package cn.authing.webauthn.authenticator.internal.ui

import android.content.Intent
import cn.authing.webauthn.data.PublicKeyCredentialRpEntity
import cn.authing.webauthn.data.PublicKeyCredentialUserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import cn.authing.webauthn.authenticator.internal.PublicKeyCredentialSource
import cn.authing.webauthn.error.ErrorReason
import cn.authing.webauthn.data.*

interface KeyguardResultListener {
    fun onAuthenticated()
    fun onFailed()
}

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
interface UserConsentUI {

    val config: UserConsentUIConfig

    val isOpen: Boolean

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean

    fun cancel(reason: ErrorReason)

    suspend fun requestUserConsent(
        rpEntity: PublicKeyCredentialRpEntity,
        userEntity: PublicKeyCredentialUserEntity,
        requireUserVerification: Boolean
    ): String

    suspend fun requestUserSelection(
        sources:                 List<PublicKeyCredentialSource>,
        requireUserVerification: Boolean
    ): PublicKeyCredentialSource

}

