package cn.authing.webauthn.ctap.response

import cn.authing.webauthn.error.ErrorReason
import cn.authing.webauthn.util.CBORWriter
import cn.authing.webauthn.util.WAKLogger

@ExperimentalUnsignedTypes
class MakeCredentialResponseBuilder(
    private val attestationObject: cn.authing.webauthn.authenticator.AttestationObject
) {

    companion object {
        val TAG = MakeCredentialResponseBuilder::class.simpleName
    }

    fun build(): Pair<ByteArray?, ErrorReason?>  {
        WAKLogger.d(TAG, "build")

        return try {

            val map = mutableMapOf<String, Any>()

            map["attStmt"]  = attestationObject.attStmt
            map["fmt"]      = attestationObject.fmt
            map["authData"] = attestationObject.authData.toBytes()!!

            val result = CBORWriter().putStringKeyMap(map).compute()
            Pair(result, null)

        } catch (e: Exception) {

            WAKLogger.w(TAG, "failed to build CBOR: $e - ${e.message}")

            Pair(null, ErrorReason.Unknown)

        }

    }

}