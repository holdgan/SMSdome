package sun.smsdome;

/**
 * Created by sun on 2017/2/20.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import sun.smsdome.database.DomeSms;


public class ActionBroadCast extends BroadcastReceiver {

    private static int num = 0;
    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("ActionBroadCast", "New Message !" + num++);

        long nowTime=System.currentTimeMillis();//获取系统时间的13位的时间戳
        long afterMinTime=nowTime-120000;
        String timeStr=String.valueOf(afterMinTime);


        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = context.getContentResolver().query(uri, projection, "date >?",new String[]{timeStr}, "date desc");      // 获取手机内部短信

        if(cur != null){
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                int index_id = cur.getColumnIndex("_id");
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                String strId = cur.getString(index_id);
                String strAddress = cur.getString(index_Address);
                int intPerson = cur.getInt(index_Person);
                String strbody = cur.getString(index_Body);
                long longDate = cur.getLong(index_Date);
                int intType = cur.getInt(index_Type);

                System.out.println(strbody+"dsdsdssddsdsdsd");

                DomeSms sms = new DomeSms(strAddress, strbody, longDate);
//                int validValue = UploadService.isSmsValid(context, sms);
//                if(validValue == 1){
                    UploadService.startActionProcess(context, sms);
//                }else if(validValue == 0){
//                    DomeSmsHelper.getInstance(context).insertMessage(sms);
//                }
            }

            cur.close();

    }    }

}