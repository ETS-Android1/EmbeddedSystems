package com.example.chriseze.spo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chriseze.spo.Models.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.mateware.snacky.Snacky;

import static com.example.chriseze.spo.Models.URLs.URL_ENERGY;
import static com.example.chriseze.spo.Models.URLs.URL_MASTER;
import static com.example.chriseze.spo.Models.URLs.URL_OUTLET;
import static com.example.chriseze.spo.Models.URLs.URL_STATUS;

public class Control extends AppCompatActivity {
    private ImageView imgStatus1, imgStatus2;
    private Button BtnMasterOn, BtnMasterOff, btnOn1, btnOn2, btnOff1, btnOff2;
    private String outlet1 = "", outlet2 = "",  energy_value = "";
    private Toolbar mToolbar;

    private TextView mTvTitle, mTvSetTime, mTvSetLimit, mTextStatus1, mTextStatus2, mTvStatus;
    private ImageView mImgIndicator;

    private SessionManager sessionManager;

    private Typeface fontTitle, font2;

    private CardView mCardTime, mCardThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mToolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        this.setTitle("Control");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        mTvTitle = (TextView)findViewById(R.id.title);
        mTvTitle.setText("Control");

        mImgIndicator = (ImageView)findViewById(R.id.indicator);
        mTvStatus = findViewById(R.id.status); //To know power supply status

        //============================================================================================//

        BtnMasterOn = (Button)findViewById(R.id.master_button_on);
        BtnMasterOff = (Button)findViewById(R.id.master_button_off);
        imgStatus1 = (ImageView)findViewById(R.id.status1);
        imgStatus2 = (ImageView)findViewById(R.id.status2);
        btnOn1 = (Button) findViewById(R.id.on1);
        btnOff1 = (Button) findViewById(R.id.off1);

        btnOn2 = (Button) findViewById(R.id.on2);
        btnOff2 = (Button) findViewById(R.id.off2);

        mCardThreshold = findViewById(R.id.card_threshold);
        mCardTime = findViewById(R.id.card_time);

        mTvSetLimit = findViewById(R.id.text_threshold);
        mTvSetTime = findViewById(R.id.text_time);

        mTextStatus1 = findViewById(R.id.text_status1);
        mTextStatus2 = findViewById(R.id.text_status2);


        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        font2 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Regular.ttf");
        mTvTitle.setTypeface(fontTitle);
        BtnMasterOff.setTypeface(font2);
        BtnMasterOn.setTypeface(font2);
        btnOff1.setTypeface(font2);
        btnOff2.setTypeface(font2);
        btnOn1.setTypeface(font2);
        btnOn2.setTypeface(font2);

        mTvSetTime.setTypeface(font2);
        mTvSetLimit.setTypeface(font2);

        mTextStatus1.setTypeface(font2);
        mTextStatus2.setTypeface(font2);

        mTvStatus.setTypeface(font2);

        mCardTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Timed.class));
            }
        });

        mCardThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Threshold.class));
            }
        });



        getPowerStatus(mTvStatus);
        getOutletStatus();

        btnOn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOutletStatus();
                setSwitch1("1");
            }
        });
        btnOff1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOutletStatus();
                setSwitch1("0");
            }
        });
        btnOn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOutletStatus();
                setSwitch2("1");
            }
        });
        btnOff2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOutletStatus();
                setSwitch2("0");
            }
        });
        BtnMasterOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMaster("1");
            }
        });
        BtnMasterOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMaster("0");
            }
        });

        sessionManager = new SessionManager(this);

    }

    private void getOutletStatus(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_OUTLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject outlet = new JSONObject(response);
                            outlet1 = outlet.getString("Outlet1");
                            outlet2 = outlet.getString("Outlet2");

                            if (outlet1.equals("1"))
                            {
                                imgStatus1.setBackgroundResource(R.drawable.online);
                                mTextStatus1.setText("ON");
                            }
                            else
                            {
                                imgStatus1.setBackgroundResource(R.drawable.offline);
                                mTextStatus1.setText("OFF");
                            }

                            if (outlet2.equals("1"))
                            {
                                imgStatus2.setBackgroundResource(R.drawable.online);
                                mTextStatus2.setText("ON");
                            }
                            else
                            {
                                imgStatus2.setBackgroundResource(R.drawable.offline);
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
                                .setActivity(Control.this)
                                .setText("Unable to fetch outlets status")
                                .setTextTypeface(fontTitle)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .error()
                                .show();
                    }
                });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }




    private void setSwitch1(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_OUTLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject result = jsonObject.getJSONObject("result");
                            String outlet1 = result.getString("outlet1");

                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (outlet1.equals("ON")){
                                imgStatus1.setBackgroundResource(R.drawable.online);
                            }else{
                                imgStatus1.setBackgroundResource(R.drawable.offline);
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
                                .setActivity(Control.this)
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
                map.put("state1", state);
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setSwitch2(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_OUTLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject result = jsonObject.getJSONObject("result");
                            String outlet2 = result.getString("outlet2");
                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (outlet2.equals("ON")){
                                imgStatus2.setBackgroundResource(R.drawable.online);
                            }else{
                                imgStatus2.setBackgroundResource(R.drawable.offline);
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
                                .setActivity(Control.this)
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
                map.put("state2", state);
                return map;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setMaster(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_MASTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            getOutletStatus();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(Control.this)
                                .setText("Unable to Switch master")
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
                map.put("state", state);
                return map;
            }
        };
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
        }
        return true;
    }

}
