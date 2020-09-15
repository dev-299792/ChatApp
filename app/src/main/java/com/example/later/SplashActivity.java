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

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "splashactivity32";
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private FirebaseUser currentUser;

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
                if(currentUser != null && currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().equals(""))
                    startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(),AgreeAndContinue.class));
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}