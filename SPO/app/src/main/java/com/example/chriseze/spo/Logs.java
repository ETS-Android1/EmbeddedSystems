package com.example.chriseze.spo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chriseze.spo.Adapters.LogAdapter;
import com.example.chriseze.spo.Models.EnergyInfo;
import com.example.chriseze.spo.Models.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.chriseze.spo.Models.URLs.URL_ENERGY;
import static com.example.chriseze.spo.Models.URLs.URL_ENERGY_GET;
import static com.example.chriseze.spo.Models.URLs.URL_STATUS;

public class Logs extends AppCompatActivity {
    private RecyclerView mRecyclerView1, mRecyclerView2;
    private LogAdapter adapter;
    private List<EnergyInfo> energyList1, energyList2;
    private SessionManager sessionManager;

    private Toolbar mToolbar;

    private TextView mTvTitle, mTvOut1, mTvOut2, mTvStatus;
    private ImageView mImgIndicator;
    private Typeface fontTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        mToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        mImgIndicator = findViewById(R.id.indicator);
        mTvStatus = findViewById(R.id.status); //To know power supply status

        mTvTitle = findViewById(R.id.title);
        mTvTitle.setText("Logs");

        //============================================================================================//


        mRecyclerView1 = (RecyclerView)findViewById(R.id.recyclerview1);
        mRecyclerView2 = (RecyclerView)findViewById(R.id.recyclerview2);

        mTvOut1 = findViewById(R.id.out1);
        mTvOut2 = findViewById(R.id.out2);

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        mTvTitle.setTypeface(fontTitle);
        mTvOut1.setTypeface(fontTitle);
        mTvOut2.setTypeface(fontTitle);
        mTvStatus.setTypeface(fontTitle);

        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));

        energyList1 = new ArrayList<>();
        energyList2 = new ArrayList<>();

        energyLogs1(mRecyclerView1);
        energyLogs2(mRecyclerView2);
        getPowerStatus(mTvStatus);

        sessionManager = new SessionManager(this);
    }

    private void energyLogs1(final RecyclerView recyclerView){
        String completeUrl = URL_ENERGY_GET + "1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, completeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject energy = jsonArray.getJSONObject(i);
                                String energy_val = energy.getString("energy_value");
                                String timestamp = energy.getString("timestamp");

                                energyList1.add(new EnergyInfo(energy_val, timestamp));
                            }

                            adapter = new LogAdapter(getApplicationContext(), energyList1, fontTitle);
                            recyclerView.setAdapter(adapter);


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

    private void energyLogs2(final RecyclerView recyclerView){
        String completeUrl = URL_ENERGY_GET + "2";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, completeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject energy = jsonArray.getJSONObject(i);
                                String energy_val = energy.getString("energy_value");
                                String timestamp = energy.getString("timestamp");

                                energyList2.add(new EnergyInfo(energy_val, timestamp));
                            }

                            adapter = new LogAdapter(getApplicationContext(), energyList2, fontTitle);
                            recyclerView.setAdapter(adapter);


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
        getMenuInflater().inflate(R.menu.main_logs, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.meter){
            startActivity(new Intent(getApplicationContext(), Meter.class));
            finish();
        }else if(id == R.id.control){
            startActivity(new Intent(getApplicationContext(), Control.class));
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
