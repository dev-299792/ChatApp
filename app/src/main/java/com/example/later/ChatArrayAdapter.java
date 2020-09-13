package com.example.later;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatArrayAdapter extends RecyclerView.Adapter<ChatArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<MyMessage> itemList;

    // Constructor of the class
    public ChatArrayAdapter(int layoutId, ArrayList<MyMessage> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        TextView left,right;
        left = holder.left;
        right = holder.right;

        MyMessage msg = itemList.get(listPosition);

        if(msg.isSender())
        {
            left.setVisibility(View.GONE);
            right.setText(msg.msg);
        }
        else
        {
            left.setText(msg.msg);
            right.setVisibility(View.GONE);
        }
        //itemSent.setText(itemList.get(listPosition).getName());
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView left,right;
        public boolean sender;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            left = itemView.findViewById(R.id.leftTextView);
            right = itemView.findViewById(R.id.rightTextView);

            sender = right.getVisibility() != View.GONE;

        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + (sender?right.getText().toString():left.getText().toString()));
        }
    }
}
