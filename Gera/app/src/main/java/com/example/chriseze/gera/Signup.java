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

import static com.example.chriseze.gera.URLs.URL_USER_SIGNUP;

public class Signup extends AppCompatActivity {

    private EditText mEdUsername, mEdPassword;
    private TextView mTvLogin, mTvText, mTvHave;
    private Button mBtnSignup;
    private Typeface fontTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEdPassword = (EditText)findViewById(R.id.password);
        mEdUsername = (EditText)findViewById(R.id.username);

        mTvLogin = (TextView)findViewById(R.id.login);
        mBtnSignup = (Button)findViewById(R.id.signup);

        mTvText = findViewById(R.id.text);
        mTvHave = findViewById(R.id.have_an_acct);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Bd.ttf");
        mTvLogin.setTypeface(fontTitle);
        mTvText.setTypeface(fontTitle);
        mTvHave.setTypeface(fontTitle);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mEdPassword.getText().toString();
                String username = mEdUsername.getText().toString();
                signupAction(password, username);
            }
        });

        mTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
    private void signupAction(final String password, final String username){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USER_SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject user = jsonObject.getJSONObject("user");

                            UserInfo userInfo = new UserInfo(getApplicationContext());
                            userInfo.setUser(user.getString("username"), user.getString("_id"));


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
        } ;
        Volley.newRequestQueue(this).add(stringRequest);
    }
}

