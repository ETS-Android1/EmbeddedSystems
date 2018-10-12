package com.example.chriseze.spo;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
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
import static com.example.chriseze.spo.Models.URLs.URL_OUTLET;
import static com.example.chriseze.spo.Models.URLs.URL_STATUS;
import static com.example.chriseze.spo.Models.URLs.URL_THRESHOLD;
import static com.example.chriseze.spo.Models.URLs.URL_TIMED;

public class Timed extends AppCompatActivity {
    private TextView mTvTitle, mTvOutlet1, mTvOutlet2, mTvText, mTvTimeOut1, mTvTimeOut2, mTextStatus1, mTextStatus2;
    private ImageView mImgIndicator, mImgStatus1, mImgStatus2;
    private Button mBtnOutlet1, mBtnOutlet2;
    private TextView mTvStatus;

    private Typeface fontTitle, font2;
    private Toolbar mToolbar;

    private TimePickerDialog mTimePicker;

    private int hour = 0, minute = 0, cHour1 = 0, cMin1 = 0, cHour2 = 0, cMin2 = 0;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed);

        mToolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Timed");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        mTvTitle = (TextView)findViewById(R.id.title);
        mTvTitle.setText("Timed");

        mImgIndicator = (ImageView)findViewById(R.id.indicator);
        mTvStatus = findViewById(R.id.status); //To know power supply status

        //============================================================================================//

        mTvOutlet1 = findViewById(R.id.text_out1);
        mTvOutlet2 = findViewById(R.id.text_out2);
        mTvTimeOut1 = findViewById(R.id.time_out1);
        mTvTimeOut2 = findViewById(R.id.time_out2);
        mTvText = findViewById(R.id.text);

        mTextStatus1 = findViewById(R.id.text_status1);
        mTextStatus2 = findViewById(R.id.text_status2);

        mBtnOutlet1 = findViewById(R.id.button1);
        mBtnOutlet2 = findViewById(R.id.button2);

        mImgStatus1 = findViewById(R.id.status1);
        mImgStatus2 = findViewById(R.id.status2);

        sessionManager = new SessionManager(this);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        font2 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Regular.ttf");
        mTvTitle.setTypeface(fontTitle);
        mTvText.setTypeface(fontTitle);

        mBtnOutlet1.setTypeface(font2);
        mBtnOutlet2.setTypeface(font2);
        mTvTimeOut1.setTypeface(font2);
        mTvTimeOut2.setTypeface(font2);
        mTvOutlet1.setTypeface(font2);
        mTvOutlet2.setTypeface(font2);
        mTextStatus1.setTypeface(font2);
        mTextStatus2.setTypeface(font2);

        mTvStatus.setTypeface(font2);

        getTime();
        getPowerStatus(mTvStatus);
        getOutletStatus();


        mTvTimeOut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minute = currentTime.get(Calendar.MINUTE);
                mTimePicker = new TimePickerDialog(Timed.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        cHour1 = selectedHour;
                        cMin1 = selectedMinute;
                        mTvTimeOut1.setText(cHour1 + ":" + cMin1);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Set Time");
                mTimePicker.show();
            }
        });

        mTvTimeOut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minute = currentTime.get(Calendar.MINUTE);
                mTimePicker = new TimePickerDialog(Timed.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        cHour2 = selectedHour;
                        cMin2 = selectedMinute;
                        mTvTimeOut2.setText(cHour2 + ":" + cMin2);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Set Time");
                mTimePicker.show();
            }
        });

        mBtnOutlet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimed1(cHour1 + "", cMin1 + "");
                getTime();
            }
        });

        mBtnOutlet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimed2(cHour2 + "", cMin2 + "");
                getTime();
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

    private void setTimed1(final String hour, final String min){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_TIMED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            getTime();
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
                map.put("outlet1_hour", hour);
                map.put("outlet1_min", min);
                map.put("tag1", "1");
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setTimed2(final String hour, final String min){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_TIMED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            getTime();
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
                map.put("outlet2_hour", hour);
                map.put("outlet2_min", min);
                map.put("tag2", "1");
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void getTime(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TIMED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet1_hour = jsonObject.getString("Outlet1 Hour");
                            String outlet1_min = jsonObject.getString("Outlet1 Min");
                            String outlet2_hour = jsonObject.getString("Outlet2 Hour");
                            String outlet2_min = jsonObject.getString("Outlet2 Min");

                            mTvTimeOut1.setText(outlet1_hour + ":" + outlet1_min);
                            mTvTimeOut2.setText(outlet2_hour + ":" + outlet2_min);
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

    private void getOutletStatus(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_OUTLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject outlet = new JSONObject(response);
                            String outlet1 = outlet.getString("Outlet1");
                            String outlet2 = outlet.getString("Outlet2");

                            if (outlet1.equals("1"))
                            {
                                mImgStatus1.setBackgroundResource(R.drawable.online);
                                mTextStatus1.setText("ON");
                            }
                            else
                            {
                                mImgStatus1.setBackgroundResource(R.drawable.offline);
                                mTextStatus1.setText("OFF");
                            }

                            if (outlet2.equals("1"))
                            {
                                mImgStatus2.setBackgroundResource(R.drawable.online);
                                mTextStatus2.setText("ON");
                            }
                            else
                            {
                                mImgStatus2.setBackgroundResource(R.drawable.offline);
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
                        Snacky.builder()
                                .setActivity(Timed.this)
                                .setText("Unable to fetch time")
                                .setTextTypeface(fontTitle)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .error()
                                .show();
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
