package cn.authing.webauthn.authenticator

import co.nstant.`in`.cbor.CborBuilder
import co.nstant.`in`.cbor.CborEncoder
import co.nstant.`in`.cbor.CborException
import cn.authing.webauthn.util.WAKLogger
import java.io.ByteArrayOutputStream
import java.util.ArrayList

@ExperimentalUnsignedTypes
class AttestationObject(
    val fmt:      String,
    val authData: cn.authing.webauthn.authenticator.AuthenticatorData,
    val attStmt:  Map<String, Any>
) {

    companion object {
        val TAG = cn.authing.webauthn.authenticator.AttestationObject::class.simpleName
    }

    fun toNone(): cn.authing.webauthn.authenticator.AttestationObject {
        return cn.authing.webauthn.authenticator.AttestationObject(
            fmt = "none",
            authData = this.authData,
            attStmt = HashMap()
        )
    }

    fun isSelfAttestation(): Boolean {
        WAKLogger.d(cn.authing.webauthn.authenticator.AttestationObject.Companion.TAG, "isSelfAttestation")
        if (this.fmt != "packed") {
            return false
        }
        if (this.attStmt.containsKey("x5c")) {
            return false
        }
        if (this.attStmt.containsKey("ecdaaKeyId")) {
            return false
        }
        if (this.authData.attestedCredentialData != null) {
            return false
        }
        if (this.authData.attestedCredentialData!!.aaguid.any { it != 0x00.toByte() }) {
            return false
        }
        return true
    }

    fun toBytes(): ByteArray? {
        WAKLogger.d(cn.authing.webauthn.authenticator.AttestationObject.Companion.TAG, "toBytes")

        return try {
            val authDataBytes = this.authData.toBytes()
            if (authDataBytes == null) {
                WAKLogger.d(cn.authing.webauthn.authenticator.AttestationObject.Companion.TAG, "failed to build authenticator data")
                return null
            }
//            val map = LinkedHashMap<String, Any>()
//            map["authData"] = authDataBytes
//            map["fmt"]      = this.fmt
//            map["attStmt"]  = this.attStmt
//
//            WAKLogger.d(TAG, "AUTH_DATA: " + ByteArrayUtil.toHex(authDataBytes))
//
//            return CBORWriter().putStringKeyMap(map).compute()
            val baos = ByteArrayOutputStream()
            attStmt["x5c"] as ArrayList<*>?;
            try {
                CborEncoder(baos).encode(
                    CborBuilder()
                        .addMap()
                        .put("authData", authDataBytes)
                        .put("fmt", this.fmt)
                        .putMap("attStmt")
                        .put("alg", attStmt["alg"] as Long)
                        .put("sig", attStmt["sig"] as ByteArray?)
                        .end()
                        .end()
                        .build()
                )
            } catch (e: CborException) {
                //throw VirgilException("couldn't serialize to cbor", e)
            }
            return baos.toByteArray()



        } catch (e: Exception) {
            WAKLogger.d(cn.authing.webauthn.authenticator.AttestationObject.Companion.TAG, "failed to build attestation binary: " + e.localizedMessage)
            null

        }

    }

}
