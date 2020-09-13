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


import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String myuid,otheruid;

    void addMessage(MyMessage msg) {

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

        try {
            otheruid = intent.getStringExtra("uid");
            if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            String othername = intent.getStringExtra("name");
            setTitle(othername);
            Toast.makeText(this, otheruid, Toast.LENGTH_SHORT).show();
            recyclerView = findViewById(R.id.myRecyclerView);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            SQLiteDatabaseHelper databaseHelper = new SQLiteDatabaseHelper(ChatActivity.this);
            final SQLiteDatabase SQLdatabase = databaseHelper.getWritableDatabase();

            final ArrayList<MyMessage> myMessageList = new ArrayList<MyMessage>();


            mAdapter = new ChatArrayAdapter(R.layout.text_layout, myMessageList);
            recyclerView.setAdapter(mAdapter);

            final EditText editText = findViewById(R.id.writeMsgEditText);
            Button button = findViewById(R.id.send);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

}
