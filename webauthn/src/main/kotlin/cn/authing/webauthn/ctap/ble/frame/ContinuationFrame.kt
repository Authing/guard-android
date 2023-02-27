package cn.authing.webauthn.ctap.ble.frame

import cn.authing.webauthn.ctap.ble.BLEErrorType
import cn.authing.webauthn.util.ByteArrayUtil
import cn.authing.webauthn.util.WAKLogger
import java.util.*

class ContinuationFrame(
    val seq:  Int,
    val data: ByteArray
) {

    companion object {
        val TAG = ContinuationFrame::class.simpleName

        fun fromByteArray(bytes: ByteArray): Pair<ContinuationFrame?, BLEErrorType?> {
            val size = bytes.size
            if (size < 2) {
                WAKLogger.w(TAG, "invalid BLE frame: no enough size")
                return Pair(null, BLEErrorType.InvalidLen)
            }
            val seq = bytes[0].toInt()
            val data = Arrays.copyOfRange(bytes, 1, size)
            return Pair(ContinuationFrame(seq, data), null)
        }
    }

    fun toByteArray(): ByteArray {
        return ByteArrayUtil.merge(byteArrayOf(seq.toByte()), data)
    }

}
