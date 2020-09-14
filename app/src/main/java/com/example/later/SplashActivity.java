package com.example.later;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.example.later.ChatGlobal.objectOutputStream;
import static com.example.later.ChatGlobal.socket;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "splashactivity32";
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private FirebaseUser currentUser;
    Socket s;

    void log(String s) {
        Log.d(TAG, "log: "+s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        currentUser =  FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                /* Create an Intent that will start the Activity. */
                socket  = s;
                Log.i(TAG, "run: "+s);

                if(currentUser != null && currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().equals("")) {
                    try {
                            objectOutputStream = new ObjectOutputStream(s.getOutputStream());
                            MyMessage message = new MyMessage();
                            message.type = MyMessage.Type.UID;
                            message.msg = currentUser.getPhoneNumber();
                            message.from = currentUser.getPhoneNumber();
                            objectOutputStream.writeObject(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                }
                else
                    startActivity(new Intent(getApplicationContext(),AgreeAndContinue.class));
            }
        }, SPLASH_DISPLAY_LENGTH);

        if(socket == null || socket.isClosed() ) {

            Log.d(TAG, "onCreate: socket null");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        s = new Socket("13.233.198.83",1234);

                        Log.d(TAG,"run: "+ s +" "+(s == null) );
                        log("run: "+ s +" "+(s == null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            t.start();


        }
        else {
            Log.d(TAG, "onCreate: socket not null");
        }
    }
}