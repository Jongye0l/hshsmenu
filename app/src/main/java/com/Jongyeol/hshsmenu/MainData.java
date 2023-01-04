package com.Jongyeol.hshsmenu;

import android.content.SharedPreferences;

import com.google.android.gms.ads.AdRequest;

import java.util.Date;

public class MainData {
    public AdRequest adRequest;
    public Date date;
    public String today = "", lunchText = "", dinnerText = "", menuText = "";
    public SharedPreferences preferences;
    public MainData(SharedPreferences preferences) {
        adRequest = new AdRequest.Builder().build();
        date = new Date();
        this.preferences = preferences;
    }
}
