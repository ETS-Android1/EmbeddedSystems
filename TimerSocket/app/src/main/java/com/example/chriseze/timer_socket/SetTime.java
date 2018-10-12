package com.example.chriseze.timer_socket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import static com.example.chriseze.timer_socket.URLs.OUTLET_CONTROL;
import static com.example.chriseze.timer_socket.URLs.TIMER_CONTROL;

public class SetTime extends AppCompatActivity {
    private Toolbar mToolbar;
    private SessionManager sessionManager;
    private Typeface mTypeface1, mTypeface2;

    private TextView mTvTitle, mTvText;
    private Button mBtnSetTime;
    private EditText mEdTime1, mEdTime2;

    private String time1, time2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        mToolbar = findViewById(R.id.appbar);
        setSupportActionBar(mToolbar);
        this.setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        sessionManager = new SessionManager(this);

        mTvTitle = findViewById(R.id.title);
        mTvText = findViewById(R.id.text);
        mEdTime1 = findViewById(R.id.ed_timer1);
        mEdTime2 = findViewById(R.id.ed_timer2);

        mBtnSetTime = findViewById(R.id.set_time);

        mTvTitle.setText("Set Timer");

        mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");

        mTvText.setTypeface(mTypeface1);
        mTvTitle.setTypeface(mTypeface2);
        mBtnSetTime.setTypeface(mTypeface2);

        mBtnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time1 = mEdTime1.getText().toString();
                time2 = mEdTime2.getText().toString();
                if (!TextUtils.isEmpty(time1) && !TextUtils.isEmpty(time2)){
                    setTime(time1, time2);
                    startActivity(new Intent(getApplicationContext(), TimerControl.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Field mustn't be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setTime(final String time1, final String time2){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, TIMER_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Snacky.builder()
//                                .setActivity(Control.this)
//                                .setText("Unable to Switch outlet")
//                                .setTextTypeface(fontTitle)
//                                .setDuration(Snacky.LENGTH_LONG)
//                                .setActionText(android.R.string.ok)
//                                .error()
//                                .show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("outlet1", time1);
                map.put("outlet2", time2);
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            sessionManager.setLogin(false);
            startActivity(new Intent(getApplicationContext(), Welcome.class));
            finish();
            return true;
        }else if(id == R.id.t_control){
            startActivity(new Intent(getApplicationContext(), TimerControl.class));
            finish();
            return true;
        }else if(id == R.id.b_control){
            startActivity(new Intent(getApplicationContext(), ButtonControl.class));
            finish();
            return true;
        }else if(id == R.id.v_control){
            startActivity(new Intent(getApplicationContext(), VoiceControl.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
