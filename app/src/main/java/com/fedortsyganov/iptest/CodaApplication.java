package com.fedortsyganov.iptest;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mopub.common.MoPub;
import io.fabric.sdk.android.Fabric;
import java.util.HashMap;
/**
 * Created by fedortsyganov on 2/18/15.
 */
public class CodaApplication extends Application
{
    private final static String FLURRY_APIKEY = "JMNSNTZG8G4Z9FG9PHHY";
    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new MoPub());
        // configure Flurry
        FlurryAgent.setLogEnabled(false);
        // init Flurry
        FlurryAgent.init(this, FLURRY_APIKEY);

    }

    private static final String PROPERTY_ID ="UA-40660764-3";

    public static int GENERAL_TRACKER = 0;
    public enum TrackerName
    {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public CodaApplication ()
    {
        super();
    }
    public synchronized Tracker getTracker(TrackerName appTracker)
    {
        if (!mTrackers.containsKey(appTracker))
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (appTracker == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : (appTracker == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(appTracker, t);
        }
        return mTrackers.get(appTracker);
    }

}
