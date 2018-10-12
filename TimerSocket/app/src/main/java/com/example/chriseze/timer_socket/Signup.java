package com.example.chriseze.timer_socket;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.chriseze.timer_socket.URLs.SIGNUP;

public class Signup extends AppCompatActivity {
    private Typeface mTypeface1, mTypeface2;
    private TextView mLoginText, mSignUpText, mLogin;
    private Button mBtnSignup;
    private EditText mEdUsername, mEdPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mLoginText = findViewById(R.id.text);
        mSignUpText = findViewById(R.id.have_an_acct);
        mLogin = findViewById(R.id.login);
        mBtnSignup = findViewById(R.id.signup_btn);
        mEdPassword = findViewById(R.id.password);
        mEdUsername = findViewById(R.id.username);

        mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");

        mSignUpText.setTypeface(mTypeface1);
        mLogin.setTypeface(mTypeface2);
        mLoginText.setTypeface(mTypeface2);
        mBtnSignup.setTypeface(mTypeface2);


        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mEdUsername.getText().toString();
                String password = mEdPassword.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                    signupAction(username, password);
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    private void signupAction(final String username, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject user = jsonObject.getJSONObject("user");

                            String username = user.getString("username");
                            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), VoiceControl.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        } ;
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
