package dev.benchristians.nfctest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import dev.benchristians.nfctest.models.DataTransferModel;
import dev.benchristians.nfctest.util.AlertUtil;
import dev.benchristians.nfctest.util.ChildBirthUtil;
import dev.benchristians.nfctest.util.TagUtil;


public class MainActivity extends Activity {
    private static final String TAG = "Main";
    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;
    EditText mNote;
    ImageView mImage;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        this.setContentView(R.layout.activity_main);
        this.findViewById(R.id.write_tag).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Write to a tag for as long as the dialog is shown.
                disableNdefExchangeMode();
                enableTagWriteMode();

                mNfcAdapter.setNdefPushMessage(getNoteAsNdef(), MainActivity.this);

                AlertUtil.showTouchButtsDialog(MainActivity.this);
            }
        });
        this.mNote = findViewById(R.id.username);
        this.mImage = findViewById(R.id.my_pet);

        this.mImage.setImageResource(R.drawable.bat);
        this.mImage.setTag(R.drawable.bat);

        // Handle all of our received NFC intents in this activity.
        this.mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        final IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (MalformedMimeTypeException e) { }
        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { tagDetected };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }
        enableNdefExchangeMode();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            AlertUtil.showJoinRequestDialog(this, msgs[0]);
        }

        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            TagUtil.writeTag(getNoteAsNdef(), detectedTag);
        }
    }

    public void handleIncomingTransfer(DataTransferModel model) {
        ChildBirthUtil.startChildBirthTimer(this, model);
        this.setNoteBody(model.getUsername() + " connected");
    }

    private void setNoteBody(String body) {
        Editable text = mNote.getText();
        text.clear();
        text.append(body);
    }

    private NdefMessage getNoteAsNdef() {
        String enteredUsername = mNote.getText().toString();
        Integer imageId = (Integer) mImage.getTag();
        DataTransferModel model = new DataTransferModel(enteredUsername, imageId, Util.getChildbirthTime());
        return model.getAsPayload();
    }

    private NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    public void enableNdefExchangeMode() {
        mNfcAdapter.setNdefPushMessage(getNoteAsNdef(), MainActivity.this);
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    public void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

}