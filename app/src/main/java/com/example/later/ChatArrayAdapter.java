package com.example.later;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatArrayAdapter extends RecyclerView.Adapter<ChatArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<MyMessage> itemList;
    private StorageReference reference;
    static final long ONE_MEGABYTE = 1024 * 1024;


    // Constructor of the class
    public ChatArrayAdapter(int layoutId, ArrayList<MyMessage> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.reference = FirebaseStorage.getInstance().getReference("images/");
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
        final ImageView l,r;
        l= holder.leftImg;
        r=holder.rightImg;

        left.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
        l.setVisibility(View.GONE);
        r.setVisibility(View.GONE);

        MyMessage msg = itemList.get(listPosition);

        if(msg.type == MyMessage.Type.CHAT) {
            if (msg.isSender()) {
                right.setVisibility(View.VISIBLE);
                right.setText(msg.msg);
            } else {
                left.setText(msg.msg);
                left.setVisibility(View.VISIBLE);
            }
        }

       else if(msg.type == MyMessage.Type.IMAGE) {
            if (msg.msg != null) {
                StorageReference storageReference = reference.child(msg.msg);
                if (msg.isSender()) {
                    r.setVisibility(View.VISIBLE);
                } else {
                    l.setVisibility(View.VISIBLE);
                }
                storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        l.setImageBitmap(bitmap);
                        r.setImageBitmap(bitmap);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView left,right;
        public ImageView leftImg,rightImg;
        public boolean sender;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            left = itemView.findViewById(R.id.leftTextView);
            right = itemView.findViewById(R.id.rightTextView);
            leftImg = itemView.findViewById(R.id.leftImageView);
            rightImg = itemView.findViewById(R.id.rightImageView);

            sender = (right.getVisibility() != View.GONE||rightImg.getVisibility() != View.GONE);

        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + (sender?right.getText().toString():left.getText().toString()));
        }
    }
}
