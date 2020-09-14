package com.example.later;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatArrayAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String myuid,otheruid;
    EditText editText;
    SQLiteDatabase database;
    ArrayList<MyMessage> myMessageList;
    ObjectOutputStream outputStream;

    void addMessage(MyMessage msg) {
        myMessageList.add(msg);
        mAdapter.notifyDataSetChanged();
        SQLiteDatabaseHelper.addMessage(database,msg);
    }

    void sendMessage() {
        String msg = editText.getText().toString();
        if(msg.equals("")) {
            return;
        }
        MyMessage m = new MyMessage();
        m.msg = msg;
        m.type = MyMessage.Type.CHAT;
        m.from = myuid;
        m.to = otheruid;
        m.sender = true;
        addMessage(m);

        sendToServer(m);
    }

    void sendToServer(final MyMessage msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream.writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MyMessage msg = (MyMessage) intent.getSerializableExtra("Message");
                addMessage(msg);
            }
        };

        manager.registerReceiver(receiver,new IntentFilter("CHAT_MSG_FILTER"));

        Intent intent = getIntent();

        SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(this);
        database = helper.getWritableDatabase();

        try {
            outputStream = ChatGlobal.objectOutputStream;
            otheruid = intent.getStringExtra("uid");
            if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            String othername = intent.getStringExtra("name");
            setTitle(othername);
            Toast.makeText(this, otheruid, Toast.LENGTH_SHORT).show();
            recyclerView = findViewById(R.id.myRecyclerView);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            myMessageList = SQLiteDatabaseHelper.getMessages(database,otheruid);

            mAdapter = new ChatArrayAdapter(R.layout.text_layout, myMessageList);
            recyclerView.setAdapter(mAdapter);

            editText = findViewById(R.id.writeMsgEditText);
            Button button = findViewById(R.id.send);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });

        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

}
