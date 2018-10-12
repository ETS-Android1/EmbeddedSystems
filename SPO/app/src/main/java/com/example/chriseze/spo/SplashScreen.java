package com.example.chriseze.spo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chriseze.spo.Models.SessionManager;

public class SplashScreen extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.setLogin(true);

        TextView textView = findViewById(R.id.text);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");
        textView.setTypeface(typeface);

        sessionManager = new SessionManager(this);
        sessionManager.setLogin(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Meter.class));
                finish();
            }
        }, 2000);
    }
}
