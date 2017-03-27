package sun.smsdome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class FullLogActivity extends AppCompatActivity {

    private ListView listView;
    private SmsAdapter adapter;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = (ListView)findViewById(R.id.list);
        adapter = new SmsAdapter(this, null);
        listView.setAdapter(adapter);

        new SmsLoadTask().execute();


        //register receiver for data updated.
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    new SmsLoadTask().execute();
                }
            };
            IntentFilter intentFilter = new IntentFilter(HomeActivity.ACTION_DATA_UPDATED);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    public class SmsLoadTask extends AsyncTask<Void, Void, List<DomeSms>>{
        @Override
        protected List<DomeSms> doInBackground(Void... voids) {
            return DomeSmsHelper.getInstance(FullLogActivity.this).queryAll(10);
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
            holder.time.setText(DateUtil.formateTimestamp(sms.getTimestamp()));
            if(!TextUtils.isEmpty(sms.getServerResponse())){
                holder.response.setText(sms.getServerResponse());
                holder.response.setVisibility(View.VISIBLE);
            }else{
                holder.response.setVisibility(View.INVISIBLE);
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
}
