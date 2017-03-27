package sun.smsdome;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by sun on 2017/1/21.
 */

public class DeviceUtil {

    private static String deviceId;

    public static String getDeviceId(Context context){
        if(TextUtils.isEmpty(deviceId)){
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        }
        return deviceId;
    }


}
