package cn.authing.webauthn.authenticator.internal.session

import cn.authing.webauthn.data.AuthenticatorAttachment
import cn.authing.webauthn.data.AuthenticatorTransport
import cn.authing.webauthn.data.PublicKeyCredentialDescriptor
import cn.authing.webauthn.error.CancelledException
import cn.authing.webauthn.error.ErrorReason
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import cn.authing.webauthn.authenticator.AuthenticatorAssertionResult
import cn.authing.webauthn.authenticator.AuthenticatorData
import cn.authing.webauthn.authenticator.GetAssertionSession
import cn.authing.webauthn.authenticator.GetAssertionSessionListener
import cn.authing.webauthn.authenticator.internal.CredentialStore
import cn.authing.webauthn.authenticator.internal.InternalAuthenticatorSetting
import cn.authing.webauthn.authenticator.internal.key.KeySupportChooser
import cn.authing.webauthn.authenticator.internal.PublicKeyCredentialSource
import cn.authing.webauthn.authenticator.internal.ui.UserConsentUI
import cn.authing.webauthn.error.*
import cn.authing.webauthn.data.*
import cn.authing.webauthn.error.TimeoutException
import cn.authing.webauthn.util.WAKLogger
import cn.authing.webauthn.util.ByteArrayUtil

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
class InternalGetAssertionSession(
    private val setting:           InternalAuthenticatorSetting,
    private val ui:                UserConsentUI,
    private val credentialStore:   CredentialStore,
    private val keySupportChooser: KeySupportChooser
) : GetAssertionSession {

    companion object {
        val TAG = InternalGetAssertionSession::class.simpleName
    }

    private var started = false
    private var stopped = false

    override var listener: GetAssertionSessionListener? = null

    override val attachment: AuthenticatorAttachment
        get() = setting.attachment

    override val transport: AuthenticatorTransport
        get() = setting.transport


    override fun getAssertion(
        rpId:                          String,
        hash:                          ByteArray,
        allowCredentialDescriptorList: List<PublicKeyCredentialDescriptor>,
        requireUserPresence:           Boolean,
        requireUserVerification:       Boolean
    ) {
        WAKLogger.d(TAG, "getAssertion")

        GlobalScope.launch {

            val sources =
                gatherCredentialSources(rpId, allowCredentialDescriptorList)

            if (sources.isEmpty()) {
                WAKLogger.d(TAG, "allowable credential source not found, stop session")
                stop(ErrorReason.NotAllowed)
                return@launch
            }

            val cred = try {
                WAKLogger.d(TAG, "request user selection")
                ui.requestUserSelection(
                    sources                 = sources,
                    requireUserVerification = requireUserVerification
                )
            } catch (e: CancelledException) {
                WAKLogger.d(TAG, "failed to select $e")
                stop(ErrorReason.Cancelled)
                return@launch
            } catch (e: TimeoutException) {
                WAKLogger.d(TAG, "failed to select $e")
                stop(ErrorReason.Timeout)
                return@launch
            } catch (e: Exception) {
                WAKLogger.d(TAG, "failed to select $e")
                stop(ErrorReason.Unknown)
                return@launch
            }


            cred.signCount = cred.signCount + setting.counterStep

            WAKLogger.d(TAG, "update credential")

            if (!credentialStore.saveCredentialSource(cred)) {

                WAKLogger.d(TAG, "failed to update credential")
                stop(ErrorReason.Unknown)
                return@launch

            }

            val extensions = HashMap<String, Any>()

            val rpIdHash = ByteArrayUtil.sha256(rpId)

            val authenticatorData = AuthenticatorData(
                rpIdHash               = rpIdHash,
                userPresent            = (requireUserPresence || requireUserVerification),
                userVerified           = requireUserVerification,
                signCount              = cred.signCount.toUInt(),
                attestedCredentialData = null,
                extensions             = extensions
            )

            val keySupport = keySupportChooser.choose(listOf(cred.alg))
            if (keySupport == null) {
                stop(ErrorReason.Unsupported)
                return@launch
            }

            val authenticatorDataBytes = authenticatorData.toBytes()
            if (authenticatorDataBytes == null) {
                stop(ErrorReason.Unknown)
                return@launch
            }

            val dataToBeSigned =
                ByteArrayUtil.merge(authenticatorDataBytes, hash)

            val signature = keySupport.sign(cred.keyLabel, dataToBeSigned)
            if (signature == null) {
                stop(ErrorReason.Unknown)
                return@launch
            }

            val credentialId =
                if (allowCredentialDescriptorList.size != 1) { cred.id } else { null }

            val assertion =
                AuthenticatorAssertionResult(
                    credentialId      = credentialId,
                    authenticatorData = authenticatorDataBytes,
                    signature         = signature,
                    userHandle        = cred.userHandle
                )


            onComplete()

            listener?.onCredentialDiscovered(this@InternalGetAssertionSession, assertion)
        }
    }

    override fun canPerformUserVerification(): Boolean {
        WAKLogger.d(TAG, "canPerformUserVerification")
        return this.setting.allowUserVerification
    }

    override fun start() {
        WAKLogger.d(TAG, "start")
        if (stopped) {
            WAKLogger.d(TAG, "already stopped")
            return
        }
        if (started) {
            WAKLogger.d(TAG, "already started")
            return
        }
        started = true
        listener?.onAvailable(this)
    }

    override fun cancel(reason: ErrorReason) {
        WAKLogger.d(TAG, "cancel")
        if (stopped) {
            WAKLogger.d(TAG, "already stopped")
            return
        }
        if (ui.isOpen) {
            ui.cancel(reason)
            return
        }
        stop(reason)
    }

    private fun stop(reason: ErrorReason) {
        WAKLogger.d(TAG, "stop")
        if (!started) {
            WAKLogger.d(TAG, "not started")
            return
        }
        if (stopped) {
            WAKLogger.d(TAG, "already stopped")
            return
        }
        stopped = true
        listener?.onOperationStopped(this, reason)
    }

    private fun onComplete() {
        WAKLogger.d(TAG, "onComplete")
        stopped = true
    }

    private fun gatherCredentialSources(
        rpId: String,
        allowCredentialDescriptorList: List<PublicKeyCredentialDescriptor>
    ): List<PublicKeyCredentialSource> {

        return if (allowCredentialDescriptorList.isEmpty()) {

            credentialStore.loadAllCredentialSources(rpId)

        } else {

            allowCredentialDescriptorList.mapNotNull {
                credentialStore.lookupCredentialSource(it.id)
            }

        }
    }

}