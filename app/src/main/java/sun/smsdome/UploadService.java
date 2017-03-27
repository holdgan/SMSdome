package sun.smsdome;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import sun.smsdome.OkHttpClientManager.Param;
import sun.smsdome.database.DomeSms;
import sun.smsdome.database.DomeSmsHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadService extends IntentService {

    public static final String TAG = "UploadService";

    final String SERVER_SECRETE = "";

    public static final String FROM_PHONE_NUMBER_1 = "95533";
    public static final String FROM_PHONE_NUMBER_2 = "95555";
    public static final String FILETER_STRING_1 = "0305";
    public static final String FILETER_STRING_3 = "0147";
    public static final String FILETER_STRING_2 = "110925403110104";
    public static final String BANK_NAME_1 = "建设银行";
    public static final String BANK_NAME_2 = "招商银行";

    private static final String ACTION_UPLOAD = "sun.smsdome.action.upload";
    private static final String ACTION_PROCESS = "sun.smsdome.action.process";

    private static final String EXTRA_SMS = "sun.smsdome.extra.SMS";

    public UploadService() {
        super("UploadService");
    }

    /**
     * Starts this service to perform action upload with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpload(Context context, DomeSms sms) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(EXTRA_SMS, sms);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action prcess with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionProcess(Context context, DomeSms sms) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(ACTION_PROCESS);
        intent.putExtra(EXTRA_SMS, sms);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD.equals(action)) {
                DomeSms sms = (DomeSms) intent.getSerializableExtra(EXTRA_SMS);
                handleActionUpload(sms);
            } else if (ACTION_PROCESS.equals(action)) {
                DomeSms sms = (DomeSms) intent.getSerializableExtra(EXTRA_SMS);
                handleActionProcess(sms);
            }
        }
    }

    /**
     * Handle action upload in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpload(DomeSms sms) {
        if (sms != null) {
            Log.d(TAG, "handleActionUpload:" + sms.toString());
            String deviceId = DeviceUtil.getDeviceId(this);
            uploadMessage(sms, deviceId);
        }
    }

    /**
     * Handle action process in the provided background thread with the provided
     * parameters.
     */
    private void handleActionProcess(DomeSms sms) {
        Log.d(TAG, "handleActionProcess:" + sms != null ? sms.toString() : "");
        int validValue = isSmsValidWithoutExistenceCheck(this, sms);
        if (validValue == 1) {
            UploadService.startActionUpload(this, sms);
        } else if (validValue == 0) {
            DomeSmsHelper.getInstance(this).insertMessage(sms);
            sendNotifyBroadcast();
        }
    }

    private void uploadMessage(DomeSms sms, String device_id) {
        String url = "";
//        String url = "ssdsds";
        Param[] params = new Param[]{
                new Param("secret", SERVER_SECRETE),
                new Param("from", sms.getSender()),
                new Param("message", sms.getContent()),
                new Param("sent_timestamp", sms.getTimestamp() + ""),
                new Param("sent_to", ""),
                new Param("message_id", CursomUtils.stringToMD5(sms.getHashId() + "")),
                new Param("device_id", device_id)
        };

        String response;
        try {
            response = OkHttpClientManager.postAsString(url, params);
        } catch (IOException e) {
            response = e.getMessage();
        }
        DebugLog.log(TAG, "response:" + response);

        sms.setServerResponse(response);
        DomeSmsHelper.getInstance(this).insertMessage(sms);
        sendNotifyBroadcast();
    }

    private void sendNotifyBroadcast() {
        Intent intent = new Intent(HomeActivity.ACTION_DATA_UPDATED);
        sendBroadcast(intent);
    }

    /**
     * is sms valid
     *
     * @param sms
     * @return -1 for ignore, 0 for ignore but need record, 1 for need upload.
     */
    public static int isSmsValidWithoutExistenceCheck(Context context, DomeSms sms) {
        int result = -1;
        if (sms != null) {
            //filter sms
            System.out.println(sms.getSender() + FROM_PHONE_NUMBER_1);

            if (sms.getSender().contains(FROM_PHONE_NUMBER_1) && (sms.getContent().contains(FILETER_STRING_1) || sms.getContent().contains(FILETER_STRING_3)) && sms.getContent().contains(BANK_NAME_1)) {
                result = 1;
            } else if (sms.getSender().contains(FROM_PHONE_NUMBER_2) && sms.getContent().contains(FILETER_STRING_2) && sms.getContent().contains(BANK_NAME_2)) {
                result = 1;
            } else {
                result = 0;
            }
        }
        return result;
    }

    /**
     * is sms valid
     *
     * @param sms
     * @return -1 for ignore, 0 for ignore but need record, 1 for need upload.
     */
    public static int isSmsValid(Context context, DomeSms sms) {
        int result = -1;
        if (sms != null) {
            boolean hasSms = DomeSmsHelper.getInstance(context).hasSms(sms);
            if (!hasSms) {
                //filter sms
                if (sms.getSender().contains(FROM_PHONE_NUMBER_1) && (sms.getContent().contains(FILETER_STRING_1) || sms.getContent().contains(FILETER_STRING_3)) && sms.getContent().contains(BANK_NAME_1)) {
                    result = 1;
                } else if (sms.getSender().contains(FROM_PHONE_NUMBER_2) && sms.getContent().contains(FILETER_STRING_2) && sms.getContent().contains(BANK_NAME_2)) {
                    result = 1;
                } else {
                    result = 0;
                }
            }
        }
        return result;
    }
}
