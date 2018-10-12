package com.example.chriseze.gera;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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


import de.mateware.snacky.Snacky;

import static com.example.chriseze.gera.URLs.URL_COMMAND;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mImgIndicator1, mImgIndicator2;
    private Button mBtnON1, mBtnOFF1, mBtnON2, mBtnOFF2;
    private SessionManager sessionManager;
    private TextView mControl, mControlS1, mControlS2, mStatus1, mStatus2;
    private SwipeRefreshLayout mSwipeRefresh;
    private Typeface fontTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        this.setTitle("EMS");

        sessionManager = new SessionManager(this);
        sessionManager.setLogin(true);

        mBtnOFF1 = (Button)findViewById(R.id.off1);
        mBtnON1 = (Button)findViewById(R.id.on1);
        mImgIndicator1 = (ImageView)findViewById(R.id.indicator1);

        mBtnOFF2 = (Button)findViewById(R.id.off2);
        mBtnON2 = (Button)findViewById(R.id.on2);
        mImgIndicator2 = (ImageView)findViewById(R.id.indicator2);

        mControl = (TextView)findViewById(R.id.control_outlet);
        mControlS1 = (TextView)findViewById(R.id.control_outlet1);
        mControlS2 = (TextView)findViewById(R.id.control_outlet2);

        mStatus1 = (TextView)findViewById(R.id.status1);
        mStatus2 = (TextView)findViewById(R.id.status2);

        mSwipeRefresh = findViewById(R.id.swRefresh);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Bd.ttf");
        mControl.setTypeface(fontTitle);
        mControlS1.setTypeface(fontTitle);
        mControlS2.setTypeface(fontTitle);

        mStatus1.setTypeface(fontTitle);
        mStatus2.setTypeface(fontTitle);

        getState();

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getState();
                mSwipeRefresh.setRefreshing(false);
            }
        });


        mBtnON1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandOut1("1");
                getState();
                getState();
            }
        });
        mBtnOFF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandOut1("0");
                getState();
                getState();
            }
        });

        mBtnON2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandOut2("1");
                getState();
                getState();
            }
        });
        mBtnOFF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandOut2("0");
                getState();
                getState();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.logout){
            sessionManager.setLogin(false);
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        return true;
    }

    private void getState(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_COMMAND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet1 = jsonObject.getString("Outlet1");
                            String outlet2 = jsonObject.getString("Outlet2");
                            if(outlet1.equals("1")){
                                mImgIndicator1.setImageResource(R.drawable.idea);
                                mStatus1.setText("ON");
                            }else{
                                mImgIndicator1.setImageResource(R.drawable.light_bulb_off);
                                mStatus1.setText("OFF");
                            }

                            if(outlet2.equals("1")){
                                mImgIndicator2.setImageResource(R.drawable.idea);
                                mStatus2.setText("ON");
                            }else{
                                mImgIndicator2.setImageResource(R.drawable.light_bulb_off);
                                mStatus2.setText("OFF");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(MainActivity.this)
                                .setText("Unable to get outlet states")
                                .setTextTypeface(fontTitle)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .error()
                                .show();

                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void commandOut1(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_COMMAND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(MainActivity.this)
                                .setText("Unable to Switch outlet")
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
                map.put("outlet1", state);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void commandOut2(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_COMMAND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(MainActivity.this)
                                .setText("Unable to Switch outlet")
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
                map.put("outlet2", state);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}

