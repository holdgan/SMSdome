package sun.smsdome;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sun on 16/5/19.
 */
public class CursomUtils {

    // 判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,1,6,7,8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // 判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String stringAddress(String address) {
        if (!TextUtils.isEmpty(address)) {
            address = address.substring(0, 4) + "\t\t" + address.substring(4, 8) + "\t\t" + address.substring(8, 12) + "\t\t" + address.substring(12, 17) + "\n" + address.substring(17, 21) + "\t\t" + address.substring(21, 25) + "\t\t" + address.substring(25, 29) + "\t\t" + address.substring(29, 34);
        } else {
            return "";
        }
        return address;
    }

    public static String stringAddress2(String address) {
        if (!TextUtils.isEmpty(address)) {
            address = address.substring(0, 4) + "\t\t" + address.substring(4, 8) + "\t\t" + address.substring(8, 12) + "\t\t" + address.substring(12, 17) + "\t\t" + address.substring(17, 21) + "\t\t" + address.substring(21, 25) + "\t\t" + address.substring(25, 29) + "\t\t" + address.substring(29, 34);
        } else {
            return "";
        }
        return address;
    }


    /**
     * 实现文本复制功能
     * add by wangqianzhou
     * @param content
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copy(String content, Context context)
    {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
    /**
     * 实现粘贴功能
     * add by wangqianzhou
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static String paste(Context context)
    {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

}
