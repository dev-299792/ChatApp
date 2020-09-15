
package com.example.later;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ChatListActivity extends AppCompatActivity {

    public static View.OnClickListener onClickListener;
    RecyclerView recyclerView;
    ChatListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    //    ArrayList<Item> list;
    ArrayList<String> list;
    ArrayList<String> uid;
    ArrayList<String> unread;
    FloatingActionButton floatingActionButton;

    void update()
    {
        recyclerView = findViewById(R.id.chatListRecylerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatListAdapter(R.layout.chat_list_item_layout,list,unread);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        startActivity(new Intent(this,TempActivity.class));

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ContactListActivity.class));
            }
        });

        list = new ArrayList<>();
        uid = new ArrayList<>();
        unread = new ArrayList<>();


        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("name",list.get(position));
                intent.putExtra("uid", uid.get(position));
                Toast.makeText(ChatListActivity.this,uid.get(position),Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        };
        
    }
}





