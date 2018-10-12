package com.example.chriseze.gera;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static final String PREF_NAME = "session";
    private static final String KEY = "isloggedin";
    private Context context;

    public SessionManager(Context context){
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        this.context = context;
    }

    public static void setLogin(boolean isLogin){
        editor.putBoolean(KEY, isLogin);
        editor.apply();
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY, false);
    }

}

