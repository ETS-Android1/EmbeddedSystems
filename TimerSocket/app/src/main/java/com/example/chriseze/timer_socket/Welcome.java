package com.example.chriseze.timer_socket;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {
    private Typeface mTypeface1, mTypeface2;
    private TextView mTvTitle, mTvWelcome;
    private Button mBtnLogin, mBtnSignup;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), VoiceControl.class));
            finish();
        }
        setContentView(R.layout.activity_welcome);

        mTvTitle = findViewById(R.id.text);
        mTvWelcome = findViewById(R.id.welcome);
        mBtnLogin = findViewById(R.id.login_btn);
        mBtnSignup = findViewById(R.id.signup_btn);

        mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");

        mBtnSignup.setTypeface(mTypeface2);
        mBtnLogin.setTypeface(mTypeface2);
        mTvTitle.setTypeface(mTypeface2);
        mTvWelcome.setTypeface(mTypeface1);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Signup.class));
                finish();
            }
        });
    }
}
