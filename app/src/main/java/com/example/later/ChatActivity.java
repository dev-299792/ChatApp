package com.example.later;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatArrayAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String myuid, otheruid;
    EditText editText;
    SQLiteDatabase database;
    ArrayList<MyMessage> myMessageList;

    //sOCKET RELATED
    Socket socket = null;
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;

    //image related
    private StorageReference mStorageRef;
    private Uri filePath;
    String imageKey;
    Uri downloadUri;

    void addMessage(MyMessage msg) {
        myMessageList.add(msg);
        mAdapter.notifyDataSetChanged();
        SQLiteDatabaseHelper.addMessage(database, msg);
    }

    void sendImage(MyMessage m) {

        m.from = myuid;
        m.to = otheruid;
        m.sender = true;
        addMessage(m);
        sendToServer(m);
    }

    void sendMessage() {
        String msg = editText.getText().toString();
        if (msg.equals("")) {
            return;
        }
        MyMessage m = new MyMessage();
        m.msg = msg;
        m.type = MyMessage.Type.CHAT;
        m.from = myuid;
        m.to = otheruid;
        m.sender = true;
        addMessage(m);

        editText.setText("");

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
                                Toast.makeText(getApplicationContext(), msg.msg, Toast.LENGTH_SHORT).show();
                                addMessage(msg);
                            }
                        });
                    }
                } catch (Exception e) {
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
                    socket = new Socket("13.126.130.234", 1234);
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    MyMessage uidMsg = new MyMessage(myuid, myuid, "", MyMessage.Type.UID);
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
                    MyMessage uidMsg = new MyMessage(myuid, myuid, "", MyMessage.Type.EXIT);
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
            mStorageRef = FirebaseStorage.getInstance().getReference();

            otheruid = intent.getStringExtra("uid");
            if (otheruid != null && otheruid.length() <= 11)
                otheruid = "+91" + otheruid;
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                if (myuid != null && myuid.length() <= 11)
                    myuid = "+91" + myuid;
            }
            String othername = intent.getStringExtra("name");
            setTitle(othername);
            Toast.makeText(this, otheruid, Toast.LENGTH_SHORT).show();
            recyclerView = findViewById(R.id.myRecyclerView);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            myMessageList = SQLiteDatabaseHelper.getMessages(database, otheruid);

            mAdapter = new ChatArrayAdapter(R.layout.text_layout, myMessageList);
            recyclerView.setAdapter(mAdapter);

            editText = findViewById(R.id.writeMsgEditText);
            Button button = findViewById(R.id.send);
            Button imgButton = findViewById(R.id.imgButton);
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choose();
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001) {
            filePath = data.getData();
            uploadImage();
//            Log.d(TAG, "onActivityResult: "+imageKey);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Global.bitmap = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void choose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2001);
    }

    public String uploadImage() {
        final String key = myuid + (long) (Math.random() * 23862362334L);
        imageKey=null;
        downloadUri=null;
        try {
            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                final StorageReference ref = mStorageRef.child("images/" + key);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 25, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();

                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageKey = key;
                        MyMessage msg = new MyMessage(key,myuid,otheruid, MyMessage.Type.IMAGE);
                        sendImage(msg);
                    }
                });
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();
                    }
                });

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        Log.i("url", ref.getDownloadUrl().toString());
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri download = task.getResult();
                            downloadUri = download;
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return key;
    }

}
