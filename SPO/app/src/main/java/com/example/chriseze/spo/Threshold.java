package com.example.chriseze.spo;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chriseze.spo.Models.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.mateware.snacky.Snacky;

import static com.example.chriseze.spo.Models.URLs.URL_ENERGY;
import static com.example.chriseze.spo.Models.URLs.URL_STATUS;
import static com.example.chriseze.spo.Models.URLs.URL_THRESHOLD;

public class Threshold extends AppCompatActivity {
    private TextView mTvText, mTvOut1, mTvOut2, mTvTitle, mTvTextOut1, mTvTextOut2, mTvThres1, mTvThres2, mTvStatus;
    private EditText mEdOutlet1, mEdOutlet2;
    private Button mBtnOutlet1, mBtnOutlet2;
    private ImageView mImgIndicator;

    private Typeface fontTitle, font2;
    private Toolbar mToolbar;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);

        mToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Threshold");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        mTvTitle = (TextView)findViewById(R.id.title);
        mTvTitle.setText("Threshold");

        mImgIndicator = (ImageView)findViewById(R.id.indicator);
        mTvStatus = findViewById(R.id.status); //To know power supply status

        //============================================================================================//

        sessionManager = new SessionManager(this);

        mTvOut1 = findViewById(R.id.out1);
        mTvOut2 = findViewById(R.id.out2);
        mTvText = findViewById(R.id.text);

        mTvTextOut1 = findViewById(R.id.text_out1);
        mTvTextOut2 = findViewById(R.id.text_out2);
        mTvThres1 = findViewById(R.id.thres1);
        mTvThres2 = findViewById(R.id.thres2);

        mBtnOutlet1 = findViewById(R.id.outlet1_btn);
        mBtnOutlet2 = findViewById(R.id.outlet2_btn);

        mEdOutlet1 = findViewById(R.id.outlet1);
        mEdOutlet2 = findViewById(R.id.outlet2);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        font2 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Regular.ttf");
        mTvTitle.setTypeface(fontTitle);
        mTvText.setTypeface(fontTitle);
        mTvThres1.setTypeface(fontTitle);
        mTvThres2.setTypeface(fontTitle);

        mBtnOutlet2.setTypeface(font2);
        mBtnOutlet1.setTypeface(font2);
        mTvOut2.setTypeface(font2);
        mTvOut1.setTypeface(font2);

        mTvTextOut1.setTypeface(font2);
        mTvTextOut2.setTypeface(font2);

        mTvStatus.setTypeface(font2);

        getThreshold();
        getPowerStatus(mTvStatus);

        mBtnOutlet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String threshold = mEdOutlet1.getText().toString();
                if (!TextUtils.isEmpty(threshold)){
                    setThreshold1(threshold);
                    getThreshold();
                }
                getThreshold();

            }
        });

        mBtnOutlet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String threshold = mEdOutlet2.getText().toString();
                if (!TextUtils.isEmpty(threshold)){
                    setThreshold2(threshold);
                    getThreshold();
                }
                getThreshold();

            }
        });

    }

    private void clearEnergyRecords(){
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL_ENERGY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");

                            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
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

    private void setThreshold1(final String threshold){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_THRESHOLD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            getThreshold();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("outlet1", threshold);
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setThreshold2(final String threshold){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_THRESHOLD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            getThreshold();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("outlet2", threshold);
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void getThreshold(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_THRESHOLD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet1 = jsonObject.getString("Outlet1");
                            String outlet2 = jsonObject.getString("Outlet2");

                            mTvThres1.setText(outlet1);
                            mTvThres2.setText(outlet2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(Threshold.this)
                                .setText("Unable to fetch threshold values")
                                .setTextTypeface(fontTitle)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .error()
                                .show();
                    }
                });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void getPowerStatus(final TextView statusText){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            Calendar currentTime = Calendar.getInstance();
                            int seconds = currentTime.get(Calendar.SECOND);


                            if ((seconds - 10) <= Integer.parseInt(status) || (seconds - 15) <= Integer.parseInt(status)) {
                                mImgIndicator.setBackgroundResource(R.drawable.online);
                                statusText.setText("Power ON");
                            } else{
                                mImgIndicator.setBackgroundResource(R.drawable.offline);
                                statusText.setText("Power OFF");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_control, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.meter){
            startActivity(new Intent(getApplicationContext(), Meter.class));
            finish();
        }else if(id == R.id.logs){
            startActivity(new Intent(getApplicationContext(), Logs.class));
            finish();
        } else if (id == R.id.logout){
            sessionManager.setLogin(false);
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }else if(id == R.id.clear_energy){
            clearEnergyRecords();
        } else if (id == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

}
