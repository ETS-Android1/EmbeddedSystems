package com.example.chriseze.spo.Models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CHRIS EZE on 6/29/2018.
 */

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "session";
    private static final String KEY = "isloggedin";
    private Context context;

    public SessionManager(Context context){
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        this.context = context;
    }

    public void setLogin(boolean isLogin){
        editor.putBoolean(KEY, isLogin);
        editor.apply();
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY, false);
    }

}
