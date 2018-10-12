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
import android.widget.ImageView;
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

import static com.example.chriseze.timer_socket.URLs.MASTER_CONTROL;
import static com.example.chriseze.timer_socket.URLs.OUTLET_CONTROL;

public class ButtonControl extends AppCompatActivity {
    private Toolbar mToolbar;
    private SessionManager sessionManager;

    private Typeface mTypeface1, mTypeface2;
    private TextView mTvOutlet1, mTvOutlet2, mTvTitle, mTvText;
    private Button mBtnTurnOn1, mBtnTurnOn2, mBtnTurnOff1, mBtnTurnOff2, mBtnMasterOn, mBtnMasterOff;

    private ImageView mImgIndicator1, mImgIndicator2;

    private String outlet1, outlet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_control);

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

        getStatus();

        mTvTitle = findViewById(R.id.title);
        mTvOutlet1 = findViewById(R.id.outlet1);
        mTvOutlet2 = findViewById(R.id.outlet2);
        mTvText = findViewById(R.id.text);

        mBtnTurnOn1 = findViewById(R.id.turn_on1);
        mBtnTurnOn2 = findViewById(R.id.turn_on2);
        mBtnTurnOff1 = findViewById(R.id.turn_off1);
        mBtnTurnOff2 = findViewById(R.id.turn_off2);

        mBtnMasterOn = findViewById(R.id.master_on);
        mBtnMasterOff = findViewById(R.id.master_off);

        mImgIndicator1 = findViewById(R.id.img_outlet1);
        mImgIndicator2 = findViewById(R.id.img_outlet2);

        mTvTitle.setText("Button-Control");

        mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");

        mTvOutlet1.setTypeface(mTypeface1);
        mTvOutlet2.setTypeface(mTypeface1);
        mTvText.setTypeface(mTypeface1);
        mTvTitle.setTypeface(mTypeface2);

        mBtnTurnOn1.setTypeface(mTypeface2);
        mBtnTurnOn2.setTypeface(mTypeface2);
        mBtnTurnOff1.setTypeface(mTypeface2);
        mBtnTurnOff2.setTypeface(mTypeface2);

        mBtnTurnOn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch1("1");
                getStatus();
            }
        });

        mBtnTurnOn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch2("1");
                getStatus();
            }
        });

        mBtnTurnOff1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch1("0");
                getStatus();
            }
        });

        mBtnTurnOff2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitch2("0");
                getStatus();
            }
        });

        mBtnMasterOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                master("1");
                getStatus();
            }
        });

        mBtnMasterOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                master("0");
                getStatus();
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

    private void setSwitch1(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, OUTLET_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet1 = jsonObject.getString("outlet1");

                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (outlet1.equals("1")){
                                mImgIndicator1.setBackgroundResource(R.drawable.status_on);
                            }else{
                                mImgIndicator1.setBackgroundResource(R.drawable.status_off);
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
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

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
                                mImgIndicator1.setBackgroundResource(R.drawable.status_on);
                            }
                            else
                            {
                                mImgIndicator1.setBackgroundResource(R.drawable.status_off);
                            }

                            if (outlet2.equals("1"))
                            {
                                mImgIndicator2.setBackgroundResource(R.drawable.status_on);
                            }
                            else
                            {
                                mImgIndicator2.setBackgroundResource(R.drawable.status_off);
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

    private void master(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, MASTER_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String master = jsonObject.getString("master");

                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (master.equals("1")){
                                mImgIndicator1.setBackgroundResource(R.drawable.status_on);
                                mImgIndicator2.setBackgroundResource(R.drawable.status_on);
                            }else{
                                mImgIndicator1.setBackgroundResource(R.drawable.status_off);
                                mImgIndicator2.setBackgroundResource(R.drawable.status_off);
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
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("master", state);
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}
