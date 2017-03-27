package sun.smsdome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    static final String TAG = "SmsReceiver";

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"action: "+action);
//        if (SMS_RECEIVED_ACTION.equals(action)) {
//            Bundle bundle = intent.getExtras();
//            StringBuffer messageContent = new StringBuffer();
//            if (bundle != null) {
//                Object[] pdus = (Object[]) bundle.get("pdus");
//                for (Object pdu : pdus) {
//                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
//                    DomeSms sms = new DomeSms(message);
//                    Log.d(TAG,"receive sms: " + sms);
//                    UploadService.startActionProcess(context, sms);
//                }
//
//            }
//        }

    }
}
