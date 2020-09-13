package com.example.later;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    //    ArrayList<Item> list;
    ArrayList<String> list;
    ArrayList<String> unread;
    int listItemLayout;
    public ChatListAdapter(int listItemLayout,ArrayList<String> list,ArrayList<String> unread)
    {
        this.listItemLayout = listItemLayout;
        this.list = list;
        this.unread = unread;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout,parent,false);
        view.setOnClickListener(ChatListActivity.onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView nameTextView = holder.nameTextView;
        TextView unreadTextView = holder.unreadTextView;
        String name = list.get(position);
        String x = unread.get(position);
        nameTextView.setText(name);
        //nameTextView.setBackgroundColor();

        if(Integer.parseInt(x)!=0)
            unreadTextView.setText(x);
        else
            unreadTextView.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTextView,unreadTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemView.setOnClickListener(new ChatListActivity.);
            nameTextView = itemView.findViewById(R.id.chatListName);
            unreadTextView = itemView.findViewById(R.id.chatListUnread);
        }

//        @Override
//        public void onClick(View v) {
//            //Toast.makeText(ChatListActivity.this,list.get(getLayoutPosition()).uid,Toast.LENGTH_SHORT).show();
//        }
    }
}