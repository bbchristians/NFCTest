package dev.benchristians.nfctest.models

import android.nfc.NdefMessage
import android.nfc.NdefRecord

class DataTransferModel constructor(
    val username: String,
    val imageId: Int,
    val childTime: Long
) {

    fun getAsPayload(): NdefMessage {
        val username = NdefRecord(
            NdefRecord.TNF_MIME_MEDIA, "text/plain".toByteArray(),
            byteArrayOf(), this.username.toByteArray()
        )
        val imageId = NdefRecord(
            NdefRecord.TNF_MIME_MEDIA, "text/plain".toByteArray(),
            byteArrayOf(), this.imageId.toString().toByteArray()
        )
        val childTime = NdefRecord(
            NdefRecord.TNF_MIME_MEDIA, "text/plain".toByteArray(),
            byteArrayOf(), this.childTime.toString().toByteArray()
        )
        return NdefMessage(arrayOf(username, imageId, childTime))
    }

    companion object {

        @JvmStatic
        fun createFromMessage(message: NdefMessage) =
                DataTransferModel(
                    String(message.records[0].payload),
                    String(message.records[1].payload).toInt(),
                    String(message.records[2].payload).toLong()
                )
    }
}