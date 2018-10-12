package com.example.chriseze.timer_socket;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.chriseze.timer_socket.URLs.OUTLET_CONTROL;

public class VoiceControl extends AppCompatActivity {
    private Toolbar mToolbar;
    private Typeface mTypeface1, mTypeface2;
    private TextView mTvTitle, mTVText1, mTVText2, mTvVoice2Text1, mTvVoice2Text2, mTvOutlet1, mTextStatus1, mTextStatus2,
            mTvOutlet2, mTvSpeakText1, mTvSpeakText2;

    private ImageView mImgIndicator1, mImgIndicator2;

    private static final int REQ_CODE_SPEECH_INPUT1 = 100;
    private static final int REQ_CODE_SPEECH_INPUT2 = 101;

    private ImageButton mSpeakBtn1, mSpeakBtn2;

    private SessionManager sessionManager;

    String outlet1, outlet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);

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
        sessionManager.setLogin(true);

        getStatus();

        mTvTitle = findViewById(R.id.title);

        mTVText1 = findViewById(R.id.text1);
        mTVText2 = findViewById(R.id.text2);

        mTvVoice2Text1 = findViewById(R.id.voice_text1);
        mTvVoice2Text2 = findViewById(R.id.voice_text2);

        mTvOutlet1 = findViewById(R.id.outlet1);
        mTvOutlet2 = findViewById(R.id.outlet2);

        mTvSpeakText1 = findViewById(R.id.speak_text1);
        mTvSpeakText2 = findViewById(R.id.speak_text2);

        mSpeakBtn1 = findViewById(R.id.btnSpeak1);
        mSpeakBtn2 = findViewById(R.id.btnSpeak2);

        mTextStatus1 = findViewById(R.id.text_status1);
        mTextStatus2 = findViewById(R.id.text_status2);

        mImgIndicator1 = findViewById(R.id.status1);
        mImgIndicator2 = findViewById(R.id.status2);

        mTvTitle.setText("Voice-Control");

        mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");

        mTvTitle.setTypeface(mTypeface2);

        mTVText1.setTypeface(mTypeface2);
        mTVText2.setTypeface(mTypeface2);

        mTvVoice2Text1.setTypeface(mTypeface1);
        mTvVoice2Text2.setTypeface(mTypeface1);

        mTvOutlet1.setTypeface(mTypeface2);
        mTvOutlet2.setTypeface(mTypeface2);

        mTvSpeakText1.setTypeface(mTypeface2);
        mTvSpeakText2.setTypeface(mTypeface2);

        mTextStatus1.setTypeface(mTypeface2);
        mTextStatus2.setTypeface(mTypeface2);

        mSpeakBtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput1();
            }
        });

        mSpeakBtn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput2();
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

    private void startVoiceInput1() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I'm listening");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT1);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void startVoiceInput2() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I'm listening");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT2);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT1: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTvVoice2Text1.setText(result.get(0));
                    if (result.get(0).toLowerCase().equals("on")){
                        outlet1 = "1";
                        setSwitch1(outlet1);
                    } else{
                        outlet1 = "0";
                        setSwitch1(outlet1);
                    }
                }
                break;
            }

            case REQ_CODE_SPEECH_INPUT2: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTvVoice2Text2.setText(result.get(0));
                    if (result.get(0).toLowerCase().equals("on")){
                        outlet2 = "1";
                        setSwitch2(outlet2);
                    } else{
                        outlet2 = "0";
                        setSwitch2(outlet2);
                    }
                    getStatus();
                }
                break;
            }

        }
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
                                mTextStatus1.setText("ON");
                            }
                            else
                            {
                                mImgIndicator1.setBackgroundResource(R.drawable.status_off);
                                mTextStatus1.setText("OFF");
                            }

                            if (outlet2.equals("1"))
                            {
                                mImgIndicator2.setBackgroundResource(R.drawable.status_on);
                                mTextStatus2.setText("ON");
                            }
                            else
                            {
                                mImgIndicator2.setBackgroundResource(R.drawable.status_off);
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

    private void setSwitch1(final String state){
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, OUTLET_CONTROL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String outlet1 = jsonObject.getString("outlet1");
                            String message = jsonObject.getString("message");

                            getStatus();
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

                            getStatus();
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

}
