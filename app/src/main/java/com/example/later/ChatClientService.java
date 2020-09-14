package com.example.later;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ChatClientService extends Service {
    private static final String TAG = "Service2000";
    Thread readMessage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(getApplicationContext(),"service started",Toast.LENGTH_SHORT).show();

        readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    ObjectInputStream dis = ChatGlobal.objectInputStream;
                    SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
                    Intent msgIntent;
                    ContentValues values = new ContentValues();

//                    Activity activity = (Activity)getApplicationContext();
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),"thread started",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    Log.d(TAG, "run: service tthread started");

                    while (true) {
                        // read the message sent to this client
                        MyMessage msg = (MyMessage) dis.readObject();
                        if(msg.msg.equals("logout")) {
                            break;
                        }
                        if(msg.type == MyMessage.Type.CHAT) {
                            values.clear();
//                            values.put()
                            msgIntent=new Intent();
                            msgIntent.setAction("CHAT_MSG_FILTER");
                            msgIntent.putExtra("Message",msg);
                            //Toast.makeText(getApplicationContext(),msg.from+ ""+msg.msg,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "run: "+msg.msg);
                            manager.sendBroadcast(msgIntent);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        readMessage.start();
        return START_REDELIVER_INTENT;
    }
}
