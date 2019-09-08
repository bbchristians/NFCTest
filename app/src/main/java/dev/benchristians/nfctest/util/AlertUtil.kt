package dev.benchristians.nfctest.util

import android.app.AlertDialog
import android.nfc.NdefMessage
import dev.benchristians.nfctest.MainActivity
import dev.benchristians.nfctest.models.DataTransferModel

class AlertUtil {

    companion object {

        @JvmStatic
        fun showJoinRequestDialog(ctx: MainActivity, msg: NdefMessage) {
            val model = DataTransferModel.createFromMessage(msg)
            AlertDialog.Builder(ctx).setTitle(model.username + " wants to join your game! Allow?")
                .setPositiveButton("Yes") { _, _ -> ctx.handleIncomingTransfer(model) }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        @JvmStatic
        fun showTouchButtsDialog(ctx: MainActivity) {
            AlertDialog.Builder(ctx).setTitle("Touch butts with your friend!")
                .setOnCancelListener {
                    ctx.disableTagWriteMode()
                    ctx.enableNdefExchangeMode()
                }.create().show()
        }
    }
}