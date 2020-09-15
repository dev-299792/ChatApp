package com.example.later;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.editTextNumber);
                String num = editText.getText().toString();
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("name","test user");
                intent.putExtra("uid",num);
                startActivity(intent);
            }
        });

    }
}