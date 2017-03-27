package sun.smsdome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import sun.smsdome.database.DomeSms;
import sun.smsdome.database.DomeSmsHelper;

public class HomeActivity extends AppCompatActivity {

    final String SMS_URI_INBOX = "content://sms/inbox";

    public static final String ACTION_DATA_UPDATED = "sun.smsdome.action.data_updated";

    private ListView listView;
    private SmsAdapter adapter;

    private ProgressDialog progressDialog;

    private BroadcastReceiver broadcastReceiver;


    //定义AlarmManager
    private AlarmManager am = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = (ListView)findViewById(R.id.list);
        adapter = new SmsAdapter(this, null);
        listView.setAdapter(adapter);

        TextView textView = new TextView(this);
        textView.setText("empty");

        listView.setEmptyView(textView);

        new InitTask().execute();

        //register receiver for data updated.
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    new SmsLoadTask().execute();
                }
            };
            IntentFilter intentFilter = new IntentFilter(ACTION_DATA_UPDATED);
            registerReceiver(broadcastReceiver, intentFilter);
        }


        //实例化AlarmManager
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 注册广播
//        IntentFilter filter1 = new IntentFilter();
//        filter1.addAction("com.xx.alarm");
//        registerReceiver(alarmReceiver, filter1);
//        Intent intent = new Intent();
//        intent.setAction("com.xx.alarm");
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(this, ActionBroadCast.class), 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),

                1000*60, pi);// 马上开始，每分钟触发一次
    }


//    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
////            Toast.makeText(context, ++i, 0).show();
//            queryTimePhoneSms();
//        }
//        //判断是否同一个时间内
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!= null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    public class SmsLoadTask extends AsyncTask<Void, Void, List<DomeSms>>{
        @Override
        protected List<DomeSms> doInBackground(Void... voids) {
            return DomeSmsHelper.getInstance(HomeActivity.this).queryAll();
        }

        @Override
        protected void onPostExecute(List<DomeSms> list) {
            super.onPostExecute(list);
            adapter.updateList(list);
        }
    }

    public class SmsAdapter extends BaseAdapter{

        private List<DomeSms> list;
        private LayoutInflater layoutInflater;
        private Context context;

        public SmsAdapter(Context context, List<DomeSms> list) {
            this.list = list;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list != null? list.size() :0;
        }

        @Override
        public DomeSms getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).getHashId();
        }

        @Override
        public View getView(int position, View contentView, ViewGroup viewGroup) {
            ViewHolder holder;
            if(contentView == null){
                holder = new ViewHolder();
                contentView = layoutInflater.inflate(R.layout.list_item, null);
                holder.sender = (TextView)contentView.findViewById(R.id.sender);
                holder.content = (TextView)contentView.findViewById(R.id.content);
                holder.time = (TextView)contentView.findViewById(R.id.time);
                holder.response = (TextView)contentView.findViewById(R.id.response);
                contentView.setTag(holder);
            }else{
                holder = (ViewHolder) contentView.getTag();
            }
            DomeSms sms = getItem(position);
            holder.sender.setText(sms.getSender());
            holder.content.setText(sms.getContent());
            holder.time.setText(DateUtil.formateTimestamp(sms.getTimestamp())+" ("+sms.getTimestamp()+")");
            if(!TextUtils.isEmpty(sms.getServerResponse())){
                holder.response.setText(sms.getServerResponse());
                holder.response.setVisibility(View.VISIBLE);
                if(sms.getServerResponse().contains("true")){
                    holder.response.setTextColor(getResources().getColor(R.color.greed));
                }else if(sms.getServerResponse().contains("false")){
                    holder.response.setTextColor(getResources().getColor(R.color.red));
                }
            }else{
                holder.response.setVisibility(View.GONE);
            }
            return contentView;
        }

        public void updateList(List<DomeSms> list){
            this.list = list;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder{
        private TextView sender, content, time, response;
    }

    public class InitTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(HomeActivity.this, null, "Loading...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            queryPhoneSms();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new SmsLoadTask().execute();
            progressDialog.dismiss();
        }
    }

    private void queryPhoneSms(){
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");      // 获取手机内部短信

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

                DomeSms sms = new DomeSms(strAddress, strbody, longDate);
                int validValue = UploadService.isSmsValid(this, sms);
                if(validValue == 1){
                    UploadService.startActionUpload(this, sms);
                }else if(validValue == 0){
                    DomeSmsHelper.getInstance(this).insertMessage(sms);
                }
            }

            cur.close();
        }

    }

    private void queryTimePhoneSms(){

        long nowTime=System.currentTimeMillis();//获取系统时间的13位的时间戳
        long afterMinTime=nowTime-60000;
        long afterTwoMinTime=nowTime-120000;
//        String timeStr=String.valueOf(time);


        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = getContentResolver().query(uri, projection, "between date " +afterTwoMinTime+" and "+afterMinTime, null, "date desc");      // 获取手机内部短信

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

                DomeSms sms = new DomeSms(strAddress, strbody, longDate);
                int validValue = UploadService.isSmsValid(this, sms);
                if(validValue == 1){
                    UploadService.startActionUpload(this, sms);
                }else if(validValue == 0){
                    DomeSmsHelper.getInstance(this).insertMessage(sms);
                }
            }

            cur.close();
        }

    }

}
