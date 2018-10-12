package com.example.chriseze.gera;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {
    private static final String PREF_NAME = "SPO";
    private static final String USERNAME = "username";
    private static final String _ID = "_id";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public UserInfo(Context context){
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setUser(String username, String _id){;
        editor.putString(USERNAME, username);
        editor.putString(_ID, _id);
        editor.apply();
    }
    public String getUsername(){
        return preferences.getString(USERNAME, "");
    }
    public String getID(){
        return preferences.getString(_ID, "");
    }
}
