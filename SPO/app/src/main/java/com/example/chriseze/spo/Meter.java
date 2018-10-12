package com.example.chriseze.spo;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chriseze.spo.Models.EnergyInfo;
import com.example.chriseze.spo.Models.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.mateware.snacky.Snacky;

import static com.example.chriseze.spo.Models.URLs.URL_ENERGY;
import static com.example.chriseze.spo.Models.URLs.URL_ENERGY_GET;
import static com.example.chriseze.spo.Models.URLs.URL_OUTLET;
import static com.example.chriseze.spo.Models.URLs.URL_STATUS;

public class Meter extends AppCompatActivity {
    private Toolbar mToolbar;

    private TextView mTextStatus, mTvTitle;
    private ImageView mImgIndicator;

    private TextView mTvOutletType, mTvEnergyValue, mTvCost, mTvStatus, mTvOutlet;
    private ImageView imgStatus;
    private Button mBtn1, mBtn2, mBtn3;
    private List<EnergyInfo> energyList;
    private String outlet1 = "", outlet2 = "", energy_value = "", timestamp = "";
    private SessionManager sessionManager;

    private Typeface fontTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);

        mToolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        this.setTitle("Meter");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        mTextStatus = (TextView)findViewById(R.id.text_status);
        mImgIndicator = (ImageView)findViewById(R.id.indicator);
        mTvStatus = findViewById(R.id.status); //To know power supply status

        mTvTitle = (TextView)findViewById(R.id.title);
        mTvTitle.setText("Meter");

        //============================================================================================//

        mTvCost = (TextView)findViewById(R.id.cost);
        mTvEnergyValue = (TextView)findViewById(R.id.energy_value);
        mTvOutletType = (TextView)findViewById(R.id.outlet_type);
        mTvOutlet = (TextView)findViewById(R.id.outlet);

        imgStatus = (ImageView)findViewById(R.id.statuss);
        mBtn1 = (Button)findViewById(R.id.button1);
        mBtn2 = (Button)findViewById(R.id.button2);

        energyList = new ArrayList<>();

        fontTitle = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");

        mTvTitle.setTypeface(fontTitle);
        mTvCost.setTypeface(fontTitle);
        mTvEnergyValue.setTypeface(fontTitle);
        mTvOutletType.setTypeface(fontTitle);
        mTvOutlet.setTypeface(fontTitle);

        mTextStatus.setTypeface(fontTitle);

        mTvStatus.setTypeface(fontTitle);

        getPowerStatus(mTvStatus);
        getEnergy("1");
        mTvOutletType.setText("1");
        getOutletStatus(1);

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEnergy("1");
                mTvOutletType.setText("1");
                getOutletStatus(1);
            }
        });
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEnergy("2");
                mTvOutletType.setText("2");
                getOutletStatus(2);
            }
        });

        sessionManager = new SessionManager(this);
    }

    private void getEnergy(String id){
        String completeUrl = URL_ENERGY_GET + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, completeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject result = jsonArray.getJSONObject(i);
                                energy_value = result.getString("energy_value");
                                timestamp = result.getString("timestamp");

                                energyList.add(new EnergyInfo(energy_value, timestamp));
                                break;
                            }

                            mTvEnergyValue.setText(energy_value);
                            double cost =  24.3 * Double.parseDouble(energy_value);
                            mTvCost.setText(cost + "");

//                            String prevTimestamp = energyList.get(energyList.size()-2).getTimestamp();
                            

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snacky.builder()
                                .setActivity(Meter.this)
                                .setText("Unable to fetch energy consumed")
                                .setTextTypeface(fontTitle)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .error()
                                .show();
                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }

    private void getOutletStatus(final int outlet){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_OUTLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject outletJSON = new JSONObject(response);
                            outlet1 = outletJSON.getString("Outlet1");
                            outlet2 = outletJSON.getString("Outlet2");


                            if (outlet == 1){
                                if (outlet1.equals("1")){
                                    imgStatus.setBackgroundResource(R.drawable.online);
                                    mTextStatus.setText("ON");
                                }
                                else{
                                    imgStatus.setBackgroundResource(R.drawable.offline);
                                    mTextStatus.setText("OFF");
                                }

                            } else if (outlet == 2){
                                if (outlet2.equals("1")){
                                    imgStatus.setBackgroundResource(R.drawable.online);
                                    mTextStatus.setText("ON");
                                }
                                else{
                                    imgStatus.setBackgroundResource(R.drawable.offline);
                                    mTextStatus.setText("OFF");
                                }
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
        getMenuInflater().inflate(R.menu.main_meter, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.control){
            startActivity(new Intent(getApplicationContext(), Control.class));
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
