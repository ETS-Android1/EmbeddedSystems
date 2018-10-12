package com.example.chriseze.spo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.chriseze.spo.Models.SessionManager;
import com.example.chriseze.spo.Models.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.mateware.snacky.Snacky;

import static com.example.chriseze.spo.Models.URLs.URL_USER_LOGIN;
import static com.example.chriseze.spo.Models.URLs.URL_USER_SIGNUP;

public class Login extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mEdUsername, mEdPassword;
    private TextView mTvSignup, mTvText1, mTvText2;
    private Button mBtnLogin;
    private UserInfo userInfo;

    Typeface fontTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        this.setTitle("Login");

        userInfo = new UserInfo(this);


        mEdPassword = (EditText)findViewById(R.id.password);
        mEdUsername = (EditText)findViewById(R.id.username);

        mTvSignup = (TextView)findViewById(R.id.signup);
        mBtnLogin = (Button)findViewById(R.id.login);

        mTvText1 = findViewById(R.id.text);
        mTvText2 = findViewById(R.id.text2);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        mTvText1.setTypeface(fontTitle);
        mTvText2.setTypeface(fontTitle);
        mTvSignup.setTypeface(fontTitle);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mEdPassword.getText().toString();
                String username = mEdUsername.getText().toString();
                loginAction(username, password);
            }
        });

        mTvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Signup.class));
                finish();
            }
        });
    }
    private void loginAction(final String username, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject user = jsonObject.getJSONObject("user");

                            String username = user.getString("username");
                            String id = user.getString("_id");

                            userInfo.setUser(username, id);


                            Log.d("TAG", "This is for login action");

                            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(Login.this)
                                .setText("Unable to Signin")
                                .setTextTypeface(fontTitle)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .error()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
            finish();
        }
    }
}
