package com.example.chriseze.timer_socket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class TimerControl extends AppCompatActivity {
    private Toolbar mToolbar;
    private Typeface mTypeface1, mTypeface2;
    private TextView mTvTitle, mTvText1, mTvText2, mTvTime1,  mTvText3, mTvText4, mTvTime2, mTextStatus1, mTextStatus2;
    private SessionManager sessionManager;
    private Button mBtnSetTime, mBtnOut1, mBtnOut2;

    private String outlet1, outlet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_control);
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

        getTime();
        getStatus();

        mTvTitle = findViewById(R.id.title);
        mTvText1 = findViewById(R.id.text1);
        mTvText2 = findViewById(R.id.text2);
        mTvTime1 = findViewById(R.id.time1);

        mTvText3 = findViewById(R.id.text3);
        mTvText4 = findViewById(R.id.text4);
        mTvTime2 = findViewById(R.id.time2);

        mTextStatus1 = findViewById(R.id.text_status1);
        mTextStatus2 = findViewById(R.id.text_status2);

        mBtnOut1 = findViewById(R.id.btn_out1);
        mBtnOut2 = findViewById(R.id.btn_out2);

        mBtnSetTime = findViewById(R.id.add_btn);
        mTvTitle.setText("Timer-Control");

        mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");

        mTvText1.setTypeface(mTypeface1);
        mTvText3.setTypeface(mTypeface1);

        mTvTitle.setTypeface(mTypeface2);
        mTvTime1.setTypeface(mTypeface2);
        mTvTime2.setTypeface(mTypeface2);
        mTvText2.setTypeface(mTypeface2);
        mTvText4.setTypeface(mTypeface2);
        mTextStatus1.setTypeface(mTypeface2);
        mTextStatus2.setTypeface(mTypeface2);

        mBtnOut1.setTypeface(mTypeface2);
        mBtnOut2.setTypeface(mTypeface2);

        mBtnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SetTime.class));
                finish();
            }
        });

        mBtnOut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch1("1");
            }
        });

        mBtnOut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch2("1");
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            sessionManager.setLogin(false);
            startActivity(new Intent(getApplicationContext(), Welcome.class));
            finish();
            return true;
        }else if(id == R.id.v_control){
            startActivity(new Intent(getApplicationContext(), VoiceControl.class));
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void getStatus(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, OUTLET_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject outlet = new JSONObject(response);
                            outlet1 = outlet.getString("outlet1");
                            outlet2 = outlet.getString("outlet2");

                            if (outlet1.equals("1"))
                            {
                                mTextStatus1.setText("ON");
                            }
                            else
                            {
                                mTextStatus1.setText("OFF");
                            }

                            if (outlet2.equals("1"))
                            {
                                mTextStatus2.setText("ON");
                            }
                            else
                            {
                                mTextStatus2.setText("OFF");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    public void getTime(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, TIMER_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject outlet = new JSONObject(response);
                            outlet1 = outlet.getString("outlet1");
                            outlet2 = outlet.getString("outlet2");

                            mTvTime1.setText(outlet1);
                            mTvTime2.setText(outlet2);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setSwitch1(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, OUTLET_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet1 = jsonObject.getString("outlet1");

                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), "Timer started!", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("outlet1", state);
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setSwitch2(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, OUTLET_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet2 = jsonObject.getString("outlet2");
                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), "Timer started!", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("outlet2", state);
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}

