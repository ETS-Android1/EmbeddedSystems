package com.example.chriseze.timer_socket;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "timer";
    private static final String KEY = "isloggedin";

    public SessionManager(Context context){
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLogin(boolean isloggedIn){
        editor.putBoolean(KEY, isloggedIn);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return prefs.getBoolean(KEY, false);
    }
}
