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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatArrayAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String myuid,otheruid;
    EditText editText;
    SQLiteDatabase database;
    ArrayList<MyMessage> myMessageList;

    //sOCKET RELATED
    Socket socket=null;
    ObjectOutputStream outputStream=null;
    ObjectInputStream inputStream=null;

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

    void startThreads() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final MyMessage msg = (MyMessage) inputStream.readObject();
                        msg.sender = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),msg.msg,Toast.LENGTH_SHORT).show();
                                addMessage(msg);
                            }
                        });
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket("13.126.130.234",1234);
                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                        MyMessage uidMsg = new MyMessage(myuid,myuid,"", MyMessage.Type.UID);
                        outputStream.writeObject(uidMsg);
                        inputStream = new ObjectInputStream(socket.getInputStream());
                        startThreads();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyMessage uidMsg = new MyMessage(myuid,myuid,"", MyMessage.Type.UID);
                    outputStream.writeObject(uidMsg);
                    socket.close();
                    outputStream.close();
                    inputStream.close();
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

        Intent intent = getIntent();

        SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(this);
        database = helper.getWritableDatabase();

        try {

            otheruid = intent.getStringExtra("uid");
            if(otheruid!=null && otheruid.length()<=11)
                otheruid = "+91"+otheruid;
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                if(myuid!=null && myuid.length()<=11)
                    myuid = "+91"+myuid;
            }
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
