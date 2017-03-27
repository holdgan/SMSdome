package sun.smsdome.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sun.smsdome.DebugLog;

/**
 * Created by yangyang on 2017/1/21.
 */
public class DomeSmsHelper {

    final String TAG = "DomeSmsHelper";

    private DomeDatebase database;
    private Context context;

    private static DomeSmsHelper instance;

    public DomeSmsHelper(Context context){
        database = new DomeDatebase(context);
        this.context = context.getApplicationContext();
    }


    public static synchronized DomeSmsHelper getInstance(Context context){
        if(instance == null){
            instance = new DomeSmsHelper(context);
        }
        return instance;
    }

    public boolean hasSms(DomeSms sms){
        boolean result = false;
        SQLiteDatabase db = database.getReadableDatabase();
        String[] projection = {"count(*)"};
        String where = DomeSms.COLUMNS.HASH_ID + "= ?";
        String[] whereClauz = {sms.getHashId()+""};
        Cursor cursor = db.query(DomeSms.COLUMNS.TABLE_NAME, projection,where, whereClauz, null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                int count = cursor.getInt(0);
                result = count > 0;
            }
            cursor.close();
        }
        return result;
    }

    public List<DomeSms> queryAll(){
        return queryAll(-1);
    }

    public List<DomeSms> queryAll(int limit){
        List<DomeSms> result = Collections.EMPTY_LIST;
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor;
        if(limit>0){
            String queryLimit = limit + "";
            cursor = db.query(DomeSms.COLUMNS.TABLE_NAME, DomeSms.COLUMNS.PROJECTION, null, null, null, null, DomeSms.COLUMNS.ORDER, queryLimit);
        }else{
            cursor = db.query(DomeSms.COLUMNS.TABLE_NAME, DomeSms.COLUMNS.PROJECTION, null, null, null, null, DomeSms.COLUMNS.ORDER);
        }
        if(cursor != null){
            result = new ArrayList<DomeSms>(cursor.getCount());
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                result.add(new DomeSms(cursor));
            }
            cursor.close();
        }
        return result;
    }

    public void insertMessage(DomeSms sms){
        SQLiteDatabase db = database.getWritableDatabase();
        long id = db.insertWithOnConflict(DomeSms.COLUMNS.TABLE_NAME, null, formate(sms), SQLiteDatabase.CONFLICT_REPLACE);
        DebugLog.log(TAG, "insert row id:"+ id);
    }

    public boolean updateServerResponse(DomeSms sms){
        ContentValues values = new ContentValues();
        values.put(DomeSms.COLUMNS.SERVER_RESPONSE, sms.getServerResponse());
        SQLiteDatabase db = database.getWritableDatabase();
        String where = DomeSms.COLUMNS.HASH_ID + "=?";
        String[] whereClause = {sms.getHashId()+""};
        int affected = db.update(DomeSms.COLUMNS.TABLE_NAME, values, where, whereClause);
        return affected > 0;
    }

    public ContentValues formate(DomeSms sms){
        ContentValues values = new ContentValues(5);
        values.put(DomeSms.COLUMNS.HASH_ID, sms.getHashId());
        values.put(DomeSms.COLUMNS.SENDER, sms.getSender());
        values.put(DomeSms.COLUMNS.CONTENT, sms.getContent());
        values.put(DomeSms.COLUMNS.TIMESTAMP, sms.getTimestamp());
        values.put(DomeSms.COLUMNS.SERVER_RESPONSE, sms.getServerResponse());
        return values;
    }

}
