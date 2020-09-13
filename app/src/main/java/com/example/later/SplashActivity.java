package com.example.later;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

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
                /* Create an Intent that will start the Activity. */
                if(currentUser != null && currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().equals(""))
                    startActivity(new Intent(getApplicationContext(),ChatListActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(),AgreeAndContinue.class));
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}