package cn.authing.webauthn.authenticator.internal.ui

import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import cn.authing.webauthn.data.PublicKeyCredentialRpEntity
import cn.authing.webauthn.data.PublicKeyCredentialUserEntity
import cn.authing.webauthn.error.CancelledException
import cn.authing.webauthn.error.ErrorReason
import cn.authing.webauthn.error.UnknownException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import cn.authing.webauthn.R
import cn.authing.webauthn.authenticator.internal.PublicKeyCredentialSource
import cn.authing.webauthn.authenticator.internal.ui.dialog.*
import cn.authing.webauthn.data.*
import cn.authing.webauthn.error.*
import cn.authing.webauthn.util.WAKLogger
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@TargetApi(Build.VERSION_CODES.M)
class DefaultUserConsentUI(
    private val activity: FragmentActivity
): UserConsentUI {

    companion object {
        val TAG = DefaultUserConsentUI::class.simpleName
        const val REQUEST_CODE = 6749
    }

    var keyguardResultListener: KeyguardResultListener? = null

    override val config = UserConsentUIConfig()

    override var isOpen: Boolean = false
        private set

    private var cancelled: ErrorReason? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        WAKLogger.d(TAG, "onActivityResult")

        return if (requestCode == REQUEST_CODE) {

            WAKLogger.d(TAG, "This is my result")
            keyguardResultListener?.let {
                if (resultCode == RESULT_OK) {
                    WAKLogger.d(TAG, "OK")
                    it.onAuthenticated()
                } else {
                    WAKLogger.d(TAG, "Failed")
                    it.onFailed()
                }
            }

            keyguardResultListener = null

            true

        } else {

            false

        }
    }

    private fun onStartUserInteraction() {
        isOpen = true
        cancelled = null
    }

    private fun <T> finish(cont: Continuation<T>, result: T) {
        WAKLogger.d(TAG, "finish")
        isOpen = false
        if (cancelled != null) {
            cont.resumeWithException(cancelled!!.rawValue)
        } else {
            cont.resume(result)
        }
    }

    private fun <T> fail(cont: Continuation<T>) {
        WAKLogger.d(TAG, "fail")
        isOpen = false
        if (cancelled != null) {
            cont.resumeWithException(cancelled!!.rawValue)
        } else {
            cont.resumeWithException(CancelledException())
        }
    }

    private fun <T> fail(cont: Continuation<T>, errorMessage: String) {
        WAKLogger.d(TAG, "fail")
        isOpen = false
        cont.resumeWithException(UnknownException(errorMessage))
    }

    override fun cancel(reason: ErrorReason) {
        cancelled = reason
    }

    override suspend fun requestUserConsent(
        rpEntity: PublicKeyCredentialRpEntity,
        userEntity: PublicKeyCredentialUserEntity,
        requireUserVerification: Boolean
    ): String = suspendCoroutine { cont ->

        WAKLogger.d(TAG, "requestUserConsent")

        onStartUserInteraction()

        activity.runOnUiThread {

            WAKLogger.d(TAG, "requestUserConsent switched to UI thread")

            // TODO make this configurable
//            val dialog = DefaultRegistrationConfirmationDialog(config)
//
//            dialog.show(activity, rpEntity, userEntity, object :
//                RegistrationConfirmationDialogListener {
//
//                override fun onCreate(keyName: String) {
//                    if (requireUserVerification) {
//                        showKeyguard(cont, keyName)
//                    } else {
//                        finish(cont, keyName)
//                    }
//                }
//
//                override fun onCancel() {
//                    fail(cont)
//                }
//
//            })

            showKeyguard(cont, userEntity.name)

        }
    }

    private fun getDefaultKeyName(username: String): String {
        val date = DateFormat.format("yyyyMMdd", Calendar.getInstance())
        return "$username($date)"
    }

    override suspend fun requestUserSelection(
        sources:                 List<PublicKeyCredentialSource>,
        requireUserVerification: Boolean
    ): PublicKeyCredentialSource = suspendCoroutine { cont ->

        WAKLogger.d(TAG, "requestUserSelection")

        onStartUserInteraction()

        activity.runOnUiThread {

            if (sources.size == 1 && !config.alwaysShowKeySelection) {

                WAKLogger.d(TAG, "found 1 source, skip selection")

                executeSelectionVerificationIfNeeded(
                    requireUserVerification = requireUserVerification,
                    source                  = sources[0],
                    cont                    = cont
                )

            } else {

                WAKLogger.d(TAG, "show selection dialog")

                val dialog = DefaultSelectionConfirmationDialog(config)

                dialog.show(activity, sources, object :
                    SelectionConfirmationDialogListener {

                    override fun onSelect(source: PublicKeyCredentialSource) {
                        WAKLogger.d(TAG, "selected")
                        executeSelectionVerificationIfNeeded(
                            requireUserVerification = requireUserVerification,
                            source                  = source,
                            cont                    = cont
                        )
                    }

                    override fun onCancel() {
                        WAKLogger.d(TAG, "canceled")
                        fail(cont)
                    }
                })

            }
        }
    }

    private fun executeSelectionVerificationIfNeeded(
        requireUserVerification: Boolean,
        source:                  PublicKeyCredentialSource,
        cont:                    Continuation<PublicKeyCredentialSource>
    ) {
        if (requireUserVerification) {
            showKeyguard(cont, source)
        } else {
            finish(cont, source)
        }
    }

    private fun <T> showKeyguard(cont: Continuation<T>, consentResult: T) {

        WAKLogger.d(TAG, "showKeyguard")

//        val keyguardManager =
//            activity.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
//
//        if (!keyguardManager.isKeyguardSecure) {
//            WAKLogger.d(TAG, "keyguard is not secure")
//
//            showErrorDialog(cont, config.messageKeyguardNotSetError)
//
//        } else {
//            WAKLogger.d(TAG, "keyguard is secure")
//
//            keyguardResultListener = object : KeyguardResultListener {
//
//                override fun onAuthenticated() {
//                    WAKLogger.d(TAG, "keyguard authenticated")
//                    finish(cont, consentResult)
//                }
//
//                override fun onFailed() {
//                    WAKLogger.d(TAG, "failed keyguard authentication")
//                    fail(cont)
//                }
//            }
//
//            val intent =
//                keyguardManager.createConfirmDeviceCredentialIntent(
//                    config.messageKeyguardTitle, config.messageKeyguardDescription)
//            activity.startActivityForResult(intent, REQUEST_CODE)
//        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity,
            executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    WAKLogger.d(TAG, "failed keyguard authentication")
                    //errorCode = 13 errorMessage = 取消
                    //errorCode = 7 errorMessage = 尝试次数过多，请稍后重试。
                    //errorCode = 9 errorMessage = 尝试次数过多。指纹传感器已停用。
                    Toast.makeText(activity, errString, Toast.LENGTH_SHORT).show()
                    fail(cont)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    WAKLogger.d(TAG, "keyguard authenticated")
                    finish(cont, consentResult)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    WAKLogger.d(TAG, "failed keyguard authentication")
                    //fail(cont)
                }
            })

        val promptInfo = PromptInfo.Builder()
            .setTitle(activity.getString(R.string.authing_biometric_title))
            .setSubtitle(activity.getString(R.string.authing_biometric_tip))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText(activity.getString(android.R.string.cancel))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun <T> showErrorDialog(cont: Continuation<T>, reason: String) {

        val dialog = VerificationErrorDialog(config)

        dialog.show(activity, reason, object: VerificationErrorDialogListener {
            override fun onComplete() {
                fail(cont)
            }
        })

    }
}
