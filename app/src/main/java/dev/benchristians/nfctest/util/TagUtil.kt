package dev.benchristians.nfctest.util

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef

class TagUtil {

    companion object {

        @JvmStatic
        fun writeTag(message: NdefMessage, tag: Tag) {
            val size = message.toByteArray().size
            val ndef = Ndef.get(tag)
            ndef?.connect()

            if (ndef?.isWritable == true || ndef?.maxSize ?: Int.MAX_VALUE > size) {
                return
            }
            ndef?.writeNdefMessage(message)
        }
    }
}