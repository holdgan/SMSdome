package sun.smsdome;

import android.util.Log;

/**
 * Created by sun on 2017/1/21.
 */

public class DebugLog {

    static boolean DEBUGGABLE = true;

    public static void log(String tag, String message){
        if(DEBUGGABLE){
            Log.d(tag, message);
        }
    }
}
