package sun.smsdome;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sun on 2017/1/21.
 */

public class DateUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");

    public static String formateTimestamp(long timestamp){
        return simpleDateFormat.format(new Date(timestamp));
    }
}
