package com.fedortsyganov.iptest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;


public class LauncherActivity extends FragmentActivity
{
    private static final String FIRST_TIME = "FIRST_TIME";
    private File folder;
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private static final String SORT_STATION_AZ = "SortStationAZ";
    private static final String SORT_STATION_ZA = "SortStationZA";
    private static final String SORT_COUNTRY_AZ = "SortCountryAZ";
    private static final String SORT_COUNTRY_ZA = "SortCountryZA";
    private static final String NOTIFICATION_CONTROLS = "NotificationCont";
    private static final String PATH_TO_CODA = "/data/data/com.fedortsyganov.iptest/coda";
    public static Typeface typefaceRobotThin, typefaceRobotoRegular;
    private Intent killNotiService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_launcher);

        killNotiService = new Intent(getBaseContext(), KillNotificationsService.class);
        startService(killNotiService);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        typefaceRobotThin = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Thin.ttf");
        typefaceRobotoRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Regular.ttf");

        folder = new File(PATH_TO_CODA);
        preferences = getSharedPreferences(STATION_TO_SAVE, MODE_PRIVATE);
        prefEditor = preferences.edit();

        if (folder.exists())
        {
            startActivity(new Intent(this, RadioMainPageActivity.class));
        }
        else
        {
            folder.mkdir();
            prefEditor.putBoolean(FIRST_TIME, true).commit();
            prefEditor.putBoolean(SORT_STATION_AZ, false).commit();
            //prefEditor.putBoolean(getString(R.string.preferences_wifi), false).commit();
            prefEditor.putBoolean(SORT_STATION_ZA, false).commit();
            prefEditor.putBoolean(SORT_COUNTRY_AZ, true).commit();
            prefEditor.putBoolean(SORT_COUNTRY_ZA, false).commit();
            prefEditor.putBoolean(NOTIFICATION_CONTROLS, false).commit();
            prefEditor.putInt("ListPosition", 1).commit();
            startActivity(new Intent(this, RadioMainPageActivity.class));
        }
    }

}
