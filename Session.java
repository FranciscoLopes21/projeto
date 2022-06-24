package com.example.crowdzero_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setId(String id) {
        prefs.edit().putString("id", id).commit();
    }

    public String getId() {
        String id = prefs.getString("id","");
        return id;
    }
}
