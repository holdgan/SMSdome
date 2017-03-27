package sun.smsdome.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by yangyang on 2017/1/21.
 */

public class DomeDatebase extends SQLiteOpenHelper {

    public static final int VERSION_INIT = 1;

    static final String DATABASE_NAME = "dome.db";

    public DomeDatebase(Context context){
        super(context, DATABASE_NAME, null, VERSION_INIT);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DomeSms.COLUMNS.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
