package sun.smsdome.database;

import android.database.Cursor;
import android.telephony.SmsMessage;

import java.io.Serializable;

/**
 * Created by yangyang on 2017/1/21.
 */

public class DomeSms implements Serializable{

    public static class COLUMNS{

        public static final String HASH_ID = "hash_id";
        public static final String SENDER = "sender";
        public static final String CONTENT = "content";
        public static final String TIMESTAMP = "timestamp";
        public static final String SERVER_RESPONSE = "server_response";

        public static final int HASH_ID_INDEX = 0;
        public static final int SENDER_ID_INDEX = 1;
        public static final int CONTENT_INDEX = 2;
        public static final int TIMESTAMP_INDEX = 3;
        public static final int SERVER_RESPONSE_INDEX = 4;

        public static final String TABLE_NAME = "dome_sms";

        public static final String ORDER = TIMESTAMP + " DESC";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
                + HASH_ID + " INTEGER primary key,"
                + SENDER +" TEXT,"
                + CONTENT + " TEXT,"
                + TIMESTAMP + " DOUBLE,"
                + SERVER_RESPONSE + " TEXT" + ");";

        public static final String[] PROJECTION = {
                HASH_ID, SENDER,CONTENT, TIMESTAMP, SERVER_RESPONSE
        };
    }

    private int hashId;
    private String sender;
    private String content;
    private long timestamp;
    private String serverResponse;

    public DomeSms(Cursor cursor){
        this.hashId = cursor.getInt(COLUMNS.HASH_ID_INDEX);
        this.sender = cursor.getString(COLUMNS.SENDER_ID_INDEX);
        this.content = cursor.getString(COLUMNS.CONTENT_INDEX);
        this.timestamp = cursor.getLong(COLUMNS.TIMESTAMP_INDEX);
        this.serverResponse = cursor.getString(COLUMNS.SERVER_RESPONSE_INDEX);
    }

    public DomeSms(SmsMessage message){
        this.sender = message.getOriginatingAddress();
        this.content = message.getMessageBody();
        this.timestamp = message.getTimestampMillis();
        this.hashId = generateHashKey(this);
    }

    public DomeSms(String sender, String content, long timestamp) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.content = content;
        this.hashId = generateHashKey(this);
    }

    public int getHashId() {
        return hashId;
    }

    public void setHashId(int hashId) {
        this.hashId = hashId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }

    public static int generateHashKey(DomeSms sms){
        return (sms.getSender()+","+sms.getContent()).hashCode();
    }

    @Override
    public String toString() {
        return "DomeSms{" +
                "hashId=" + hashId +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", serverResponse='" + serverResponse + '\'' +
                '}';
    }
}
