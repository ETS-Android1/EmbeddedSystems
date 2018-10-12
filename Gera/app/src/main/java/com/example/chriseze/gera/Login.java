package com.example.chriseze.gera;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import static com.example.chriseze.gera.URLs.URL_USER_LOGIN;

public class Login extends AppCompatActivity {

    private EditText mEdUsername, mEdPassword;
    private TextView mTvSignup, mTvText, mTvDontHave;
    private Button mBtnLogin;
    private UserInfo userInfo;
    private Typeface fontTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfo = new UserInfo(this);

        mEdPassword = (EditText) findViewById(R.id.password);
        mEdUsername = (EditText) findViewById(R.id.username);

        mTvSignup = (TextView) findViewById(R.id.signup);
        mBtnLogin = (Button) findViewById(R.id.login);

        mTvText = findViewById(R.id.text);
        mTvDontHave = findViewById(R.id.dont_have_an_acct);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Bd.ttf");
        mTvSignup.setTypeface(fontTitle);
        mTvText.setTypeface(fontTitle);
        mTvDontHave.setTypeface(fontTitle);

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

    private void loginAction(final String username, final String password) {
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

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
