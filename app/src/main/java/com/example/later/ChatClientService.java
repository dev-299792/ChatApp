package com.example.later;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ChatClientService extends Service {
    Thread readMessage = new Thread(new Runnable()
    {
        @Override
        public void run() {
            try {
                ObjectInputStream dis = new ObjectInputStream(ChatGlobal.socket.getInputStream());
                SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(getApplicationContext());
                SQLiteDatabase database = helper.getWritableDatabase();
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
                Intent msgIntent;
                ContentValues values = new ContentValues();
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
                        manager.sendBroadcast(msgIntent);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!readMessage.isAlive())
            readMessage.start();
        return START_REDELIVER_INTENT;
    }
}
