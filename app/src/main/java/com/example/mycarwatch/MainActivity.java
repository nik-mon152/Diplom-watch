package com.example.mycarwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    int limit = 2;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        handler = new Handler();
        onEverySecond.run();
    }
    Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {

            count++;
            if (count == limit){
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }else {handler.postDelayed(onEverySecond, 1000);
            }
        }
    };
}