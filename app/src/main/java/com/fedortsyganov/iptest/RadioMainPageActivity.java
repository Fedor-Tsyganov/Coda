package com.fedortsyganov.iptest;

import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.fedortsyganov.iptest.fragments.*;
import com.fedortsyganov.iptest.helpers.ActionBarAnimation;
import com.fedortsyganov.iptest.helpers.ColorGenerator;
import com.fedortsyganov.iptest.helpers.CountryComparator;
import com.fedortsyganov.iptest.helpers.CountryReverseComparator;
import com.fedortsyganov.iptest.helpers.Debuger;
import com.fedortsyganov.iptest.helpers.NotificationControls;
import com.fedortsyganov.iptest.helpers.PlaylistHelper;
import com.fedortsyganov.iptest.helpers.StationNameComparator;
import com.fedortsyganov.iptest.helpers.StationNameReverseComparator;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.receivers.RemoteControlReceiver;
import com.fedortsyganov.iptest.remotehelpers.MediaButtonHelper;
import com.fedortsyganov.iptest.remotehelpers.RemoteControlHelper;
import com.fedortsyganov.iptest.translation.TranslateCountry;
import com.fedortsyganov.iptest.translation.TranslateGenre;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RadioMainPageActivity extends FragmentActivity implements View.OnClickListener
{
    //RadioAlarmReceiver alarmReceiver = new RadioAlarmReceiver();
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String STATION_TO_SAVE = "info";
    private static final String SEARCH = "SEARCH_FRAGMENT";
    private static final String RADIO_LIST = "RADIO_FRAGMENT";
    private static final String SETTINGS = "SETTINGS_FRAGMENT";
    private static final String STATION_PREFS = "ListPosition";
    private static final String PLAYLIST_LIST = "PLAYLIST_LIST_FRAGMENT";
    private static final String PLAYLIST = "PLAYLIST_FRAGMENT";
    private final static String RESTART = "com.fedortsyganov.iptest.RESTART_PLAYER";

    private static final String SORT_STATION_AZ = "SortStationAZ";
    private static final String SORT_STATION_ZA = "SortStationZA";
    private static final String SORT_COUNTRY_AZ = "SortCountryAZ";
    private static final String SORT_COUNTRY_ZA = "SortCountryZA";

    private static final String TAG = "MediaNotification";

    //static ProgressBar progressBar;
    public static int counter = 2;
    public static int radioStationPosition = 0;
    private Intent radioIntent;
    private FrameLayout root;
    public static FrameLayout playingStationInfo;
    private ImageView backgroundView;
    public static ImageView ivPlayerControls;
    public Button bMenu;
    public static Button bSearch, bAddPlaylist;
    private RadioNavigationDrawer navigationDrawer;
    public static int connectivityCounter = 0;
    public static int remoteControlCounter = 0;
    public SharedPreferences preferences;
    //input of raw station csv's and reader for csv
    private InputStream inputStream;
    private BufferedReader reader;
    private FragmentManager manager;
    public static TextView tvActionBarInfo;
    public static TextView tvStationName, tvStationCountry;
    public static EditText searchET;

    //station lists in alphabetical order
    public static ArrayList <RadioStation> adultContemporaryStations = new ArrayList<>();
    public static ArrayList <RadioStation> alternativeStations = new ArrayList<>();
    public static ArrayList <RadioStation> countryStations = new ArrayList<>();
    public static ArrayList <RadioStation> classicalStations = new ArrayList<>();
    public static ArrayList <RadioStation> danceStations = new ArrayList<>();
    public static ArrayList <RadioStation> jazzStations = new ArrayList<>();
    public static ArrayList <RadioStation> hiphopStations = new ArrayList<>();
    public static ArrayList <RadioStation> loungeStations = new ArrayList<>();
    public static ArrayList <RadioStation> oldiesStations = new ArrayList<>();
    public static ArrayList <RadioStation> rockStations = new ArrayList<>();
    public static ArrayList <RadioStation> studentStations = new ArrayList<>();
    public static ArrayList <RadioStation> topFourtyStations = new ArrayList<>();
    public static ArrayList <RadioStation> allStations = new ArrayList<>();
    public static ArrayList <RadioStation> testAllStations = new ArrayList<>();
    public static ArrayList <ArrayList<RadioStation>> bigArray = new ArrayList<>();
    public static boolean isPlaying = false;
    public static boolean isPaused = false;
    public static boolean applyMargin = false;
    public static boolean listsAreDifferent = false;
    public static RadioStation radioStation = null;
    //list to be operated
    public static ArrayList <RadioStation> currentStationsList;
    public static ArrayList <RadioStation> previousStationsList;
    private android.app.FragmentTransaction fragmentTransaction;
    //
    private boolean FIRST_TIME = true;
    //private GoogleAnalytics googleAnalytics;
    private GoogleApiClient mGoogleApiClient;
    public static int searchCounter = 0;
    public int image = 0;
    //private ComponentName mRemoteControlResponder;
    //private AudioManager myAudioManager;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Debuger.DEBUG)
            Log.i("onCreate", "onCreate");
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(STATION_TO_SAVE, MODE_PRIVATE);

        //myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //mRemoteControlResponder = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        //googleAnalytics = GoogleAnalytics.getInstance(RadioMainPage.this);
        //Google Analytics segments
        //Tracker t = ((CodaApplication) getApplication()).getTracker(CodaApplication.TrackerName.APP_TRACKER);
        //t.setScreenName("Home");
        //t.enableAdvertisingIdCollection(true); // Enable Advertising Features.
        //t.send(new HitBuilders.AppViewBuilder().build());
        //end of google analytics

        if (allStations.size() < 1)
            setAllStations();
        else
            FIRST_TIME = false;
        //RadioStation station  = getStation();

        root = (FrameLayout) findViewById(R.id.root);
        playingStationInfo = (FrameLayout) root.findViewById(R.id.layoutPlayingStation);
        backgroundView = (ImageView) root.findViewById(R.id.backimg);
        backgroundView.setImageResource(R.drawable.radio_mainpage_background);
        radioIntent = new Intent(getBaseContext(), MusicService.class);
        radioIntent.putExtra("run", "START");

        navigationDrawer = (RadioNavigationDrawer) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawer.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        tvActionBarInfo = (TextView) findViewById(R.id.tvActionBarInfo);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Thin.ttf");
        tvActionBarInfo.setTypeface(typeface);

        bMenu = (Button) root.findViewById(R.id.buttonMenu);
        bMenu.setOnClickListener(this);

        /*
        if (Debuger.DEBUG)
            Log.v("StationList", "Station - "+station);
        if (station != null)
        {
            if (Debuger.DEBUG)
                Log.v("StationList", ""+currentStationsList.size());
            int val = currentStationsList.size();
            for (int i = 0; i < val - 1; i++)
            {
                if (currentStationsList.get(i).equals(station))
                {
                    radioStationPosition = i;
                }
            }
        }
        */
        tvStationName = (TextView) playingStationInfo.findViewById(R.id.tvPlayingStationName);
        tvStationCountry  = (TextView) playingStationInfo.findViewById(R.id.tvPlayingStationCountry);
        ivPlayerControls = (ImageView) playingStationInfo.findViewById(R.id.ivPlayingStationControls);
        manager = getFragmentManager();
        fragmentTransaction = manager.beginTransaction();
        image = preferences.getInt(STATION_PREFS, 0);
        if (FIRST_TIME)
        {
            if (Debuger.DEBUG)
                Log.i("firstTime", "true");
            if (image == 0)
            {
                fragmentTransaction
                        //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(R.id.container, FragmentRadioList.newInstance(4, RadioNavigationDrawer.background[image]), RADIO_LIST)
                        .add(R.id.container, FragmentSearch.newInstance(3), SEARCH)
                        .add(R.id.container, FragmentSettings.newInstance(2), SETTINGS)
                        .add(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST)
                        .add(R.id.container, FragmentPlaylist.newInstance(0), PLAYLIST)
                        //.hide(FragmentSettings.fragmentSettings)
                        //.hide(FragmentSearch.fragmentSearch)
                        //.hide(FragmentRadioList.fragmentRadioList)
                        //.hide(FragmentPlaylist.fragmentPlaylist)
                        .detach(FragmentSettings.fragmentSettings)
                        .detach(FragmentSearch.fragmentSearch)
                        .detach(FragmentRadioList.fragmentRadioList)
                        .detach(FragmentPlaylist.fragmentPlaylist)
                        .commit();
            }
            else
            {
                fragmentTransaction
                        //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .add(R.id.container, FragmentRadioList.newInstance(4, RadioNavigationDrawer.background[image]), RADIO_LIST)
                        .add(R.id.container, FragmentSearch.newInstance(3), SEARCH)
                        .add(R.id.container, FragmentSettings.newInstance(2), SETTINGS)
                        .add(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST)
                        .add(R.id.container, FragmentPlaylist.newInstance(0), PLAYLIST)
                        //.hide(FragmentSettings.fragmentSettings)
                        //.hide(FragmentSearch.fragmentSearch)
                        //.hide(FragmentPlaylistList.fragmentPlaylistList)
                        //.hide(FragmentPlaylist.fragmentPlaylist)
                        .detach(FragmentSettings.fragmentSettings)
                        .detach(FragmentSearch.fragmentSearch)
                        .detach(FragmentPlaylistList.fragmentPlaylistList)
                        .detach(FragmentPlaylist.fragmentPlaylist)
                        .commit();
            }

        }
        else
        {
            if (Debuger.DEBUG)
                Log.i("firstTime", "false");
            if (image == 0)
            {
                fragmentTransaction
                        .replace(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST)
                        .commit();
            }
            else
            {
                fragmentTransaction
                        .replace(R.id.container, FragmentRadioList.newInstance(4, RadioNavigationDrawer.background[image]), RADIO_LIST)
                        .commit();
            }
        }
        FIRST_TIME = false;

        searchET = (EditText) findViewById(R.id.searchET);
        searchET.setTypeface(LauncherActivity.typefaceRobotoRegular);
        searchET.clearComposingText();

        bSearch = (Button) findViewById(R.id.buttonSearch);
        bSearch.setOnClickListener(this);
        if (radioStation == null)
            radioStation = currentStationsList.get(radioStationPosition);
        bAddPlaylist = (Button) findViewById(R.id.buttonAddPlaylist);
        bAddPlaylist.setVisibility(View.GONE);
        if (isMyServiceRunning(MusicService.class) && !isPaused && isPlaying)
        {
            setPlayerInfoControl(R.drawable.icon_pause_notification);
        }
        else if (!isMyServiceRunning(MusicService.class) && isPaused && !isPlaying)
        {
            setPlayerInfoControl(R.drawable.icon_play_notification);
        }
        else
        {
            playingStationInfo.setVisibility(View.GONE);
        }

        //checking intent to restart service

        String action = getIntent().getAction();
        if (action != null && action.equalsIgnoreCase(RESTART))
        {
            if (Debuger.DEBUG)
            {
                Log.v("RadioMainPageActivity", "RESTART");
                Log.v("RadioMainPageActivity", "currentStationsList:" + currentStationsList.size());
                Log.v("RadioMainPageActivity", "previousStationsList:" + previousStationsList.size());
                Log.v("RadioMainPageActivity", "radioStationPosition:" + radioStationPosition);
                Log.v("RadioMainPageActivity", "counter:" + counter);
            }
            MusicService.unSetSession();

            RadioMainPageActivity.applyMargin = false;
            if (MusicService.myAudioManager != null)
            {
                if (MusicService.mRemoteControlResponder != null)
                    MusicService.myAudioManager.unregisterMediaButtonEventReceiver(MusicService.mRemoteControlResponder);
                if (MusicService.remoteControlClient != null)
                    MusicService.myAudioManager.unregisterRemoteControlClient(MusicService.remoteControlClient);
            }
            if (MusicService.media != null)
            {
                MusicService.media.stop();
                MusicService.media.reset();
                MusicService.media.release();
                MusicService.media = null;
                isPlaying = false;
                isPaused = false;
            }
            stopService(new Intent(getBaseContext(), MusicService.class));
            clearNotification();
            counter = 0;
            //add restart service logic here
        }
        else
        {
            //do nothing
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i("onStart","onStart");
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        //googleAnalytics.reportActivityStart(RadioMainPage.this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (Debuger.DEBUG)
            Log.v("onStop", "MainPageActivity - onStop");
        /*
        very important - stop and hiding app!
         */
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        //if (isPaused && !isPlaying)
           // clearNotification();
        //googleAnalytics.reportActivityStop(RadioMainPage.this);
    }

    public void onBackPressed()
    {
        if (shownFragment(PLAYLIST))
        {
            ActionBarAnimation.animationPlaylistOut(getResources().getString(R.string.app_name));
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST)
                    .commit();
        }
        else
        {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            //additional code
        }

    }

    //setting logic - helper
    protected void setStation (int id, ArrayList <RadioStation> list)
    {
        inputStream = getResources().openRawResource(id);
        reader = new BufferedReader(new InputStreamReader(inputStream));
        try
        {
            String line;
            int counter = 0;
            while ((line = reader.readLine()) != null)
            {
                String [] rowData = line.split(",");
                int color = ColorGenerator.generateColor();
                if (list.size() > 0)
                {
                    while (list.get(counter-1).getColor() == color)
                        color = ColorGenerator.generateColor();
                }
                String country = TranslateCountry.translate(rowData[3], getBaseContext());
                String genre = TranslateGenre.translate(rowData[2], getBaseContext());
                RadioStation station =
                        new RadioStation(rowData[0],rowData[1],/*rowData[2]*/genre, /*rowData[3]*/country, false, color);
                list.add(station);
                allStations.add(station);
                counter++;
            }
        }
        catch (Exception e){}
        bigArray.add(list);
    }

    //setting up all stations
    private void setAllStations()
    {
        setStation(R.raw.europe_dance, danceStations);
        setStation(R.raw.europe_rock, rockStations);
        setStation(R.raw.europe_jazz, jazzStations);
        setStation(R.raw.europe_classical, classicalStations);
        setStation(R.raw.europe_country, countryStations);
        setStation(R.raw.europe_alternative_rock, alternativeStations);
        setStation(R.raw.europe_lounge, loungeStations);
        setStation(R.raw.europe_hiphop, hiphopStations);
        setStation(R.raw.europe_oldies, oldiesStations);
        setStation(R.raw.europe_student, studentStations);
        setStation(R.raw.europe_adult_contemporary, adultContemporaryStations);
        setStation(R.raw.europe_top_forty, topFourtyStations);

        //mix stations in the list
        Collections.shuffle(allStations);
        sortLists();
        /*
        if (FIRST_TIME)
        {
            currentStationsList = allStations;
        }
        */
        currentStationsList = getStationList();
        previousStationsList = currentStationsList;
    }

    private ArrayList <RadioStation> getStationList()
    {
        int position = preferences.getInt(STATION_PREFS, 0);
        ArrayList <RadioStation> s;
        switch (position)
        {
            case 0:
                s = new ArrayList<>(RadioMainPageActivity.allStations);
                break;
            case 1:
                s = new ArrayList<>(RadioMainPageActivity.danceStations);
                break;
            case 2:
                s = new ArrayList<>(RadioMainPageActivity.loungeStations);
                break;
            case 3:
                s = new ArrayList<>(RadioMainPageActivity.rockStations);
                break;
            case 4:
                s = new ArrayList<>(RadioMainPageActivity.jazzStations);
                break;
            case 5:
                s = new ArrayList<>(RadioMainPageActivity.topFourtyStations);
                break;
            case 6:
                s = new ArrayList<>(RadioMainPageActivity.classicalStations);
                break;
            case 7:
                s = new ArrayList<>(RadioMainPageActivity.hiphopStations);
                break;
            case 8:
                s = new ArrayList<>(RadioMainPageActivity.alternativeStations);
                break;
            case 9:
                s = new ArrayList<>(RadioMainPageActivity.oldiesStations);
                break;
            case 10:
                s = new ArrayList<>(RadioMainPageActivity.adultContemporaryStations);
                break;
            case 11:
                s = new ArrayList<>(RadioMainPageActivity.countryStations);
                break;
            case 12:
                s = new ArrayList<>(RadioMainPageActivity.studentStations);
                break;
            default:
                s = new ArrayList<>(RadioMainPageActivity.allStations);
                break;
        }
        return s;
    }

    /*
    private RadioStation getStation()
    {
        long startTime = System.nanoTime();
        Gson gson = new Gson();
        String json = preferences.getString("RadioStation", "");
        RadioStation station = gson.fromJson(json, RadioStation.class);
        long seconds = System.nanoTime() - startTime;
        long time = seconds/1000000;
        Log.i("Duration", "getStation: " + time + "");
        return station;
    }
    */
    /*
    private void saveStn(RadioStation station)
    {
        RadioStation myStation = new RadioStation();
        myStation.seStationName(station.getStationName());
        myStation.setStationCountry(station.getStationCountry());
        myStation.setStationGenre(station.getStationGanre());
        myStation.setStationUrl(station.getStationUrl());
        myStation.setHeader(false);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<RadioStation>() {}.getType();
        String json = gson.toJson(myStation, type);
        prefEditor.putBoolean("StationBool", true).commit();
        int num = preferences.getInt("StationNum", 0);
        num = num+1;
        prefEditor.putInt("StationNum", num).commit();
        String str = "Station"+Integer.toString(num);
        prefEditor.putString(str, json).commit();
    }
    */

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    public void sortLists()
    {
        int size = bigArray.size();
        if (preferences.getBoolean(SORT_STATION_AZ, false))
        {
            for (int i = 0; i < size; i++)
            {
                Collections.sort(bigArray.get(i), new StationNameComparator());
            }
        }
        if (preferences.getBoolean(SORT_STATION_ZA, false))
        {
            for (int i = 0; i < size; i++)
            {
                Collections.sort(bigArray.get(i), new StationNameReverseComparator());
            }
        }
        if (preferences.getBoolean(SORT_COUNTRY_AZ, false))
        {
            for (int i = 0; i < size; i++)
            {
                Collections.sort(bigArray.get(i), new CountryComparator());
            }
        }
        if (preferences.getBoolean(SORT_COUNTRY_ZA, false))
        {
            for (int i = 0; i < size; i++)
            {
                Collections.sort(bigArray.get(i), new CountryReverseComparator());
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonSearch:
                ActionBarAnimation.animationIn();
                manager.beginTransaction()
                       .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                       .replace(R.id.container, FragmentSearch.newInstance(3), SEARCH)
                       .commit();
                break;
            case R.id.buttonMenu:
                RadioNavigationDrawer.mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            default:
                break;
        }
    }
    private boolean shownFragment(String str)
    {
        if (getFragmentManager().findFragmentByTag(str) != null)
        {
            return getFragmentManager().findFragmentByTag(str).isVisible();
        }
        else
        {
            return false;
        }
    }

    private void setPlayerInfoControl(int icon)
    {
        tvStationName.setText(previousStationsList.get(radioStationPosition).getStationName());
        tvStationCountry.setText(previousStationsList.get(radioStationPosition).getStationCountry());
        ivPlayerControls.setImageResource(icon);
        ivPlayerControls.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (currentStationsList!=null && previousStationsList!=null)
                {
                    if (!currentStationsList.equals(previousStationsList))
                    {
                        listsAreDifferent = true;
                    } else
                    {
                        listsAreDifferent = false;
                    }
                    currentStationsList = new ArrayList<>(previousStationsList);
                }
                sendBroadcast(new Intent("com.fedortsyganov.iptest.STOP_PLAY"));
            }
        });
        playingStationInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!currentStationsList.equals(previousStationsList))
                {
                    listsAreDifferent = true;
                } else
                {
                    listsAreDifferent = false;
                }
                currentStationsList = new ArrayList<>(previousStationsList);
                startActivity(new Intent(getApplicationContext(), RadioPlayerActivity.class));
            }
        });
        playingStationInfo.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (Debuger.DEBUG)
            Log.i("onPause", "onPause");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //Integer numbr = new Integer(counter);
        //numbr = numbr+3;
        //if (isPaused && !isPlaying)
         //   new NotificationControls(numbr, getApplicationContext());
        if (Debuger.DEBUG)
            Log.i("onRestart", "onRestart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //myAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
        /*
        Very important - resume after hiding app (not closing)
         */
        if (Debuger.DEBUG)
            Log.i("onResume", "onResume");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RadioMainPageActivity.applyMargin = false;
        if (MusicService.sessionCompat != null)
        {
            MusicService.sessionCompat.setActive(false);
            MusicService.sessionCompat.release();
        }
        if (MusicService.myAudioManager != null)
        {
            MusicService.myAudioManager.unregisterMediaButtonEventReceiver(MusicService.mRemoteControlResponder);
            MusicService.myAudioManager.unregisterRemoteControlClient(MusicService.remoteControlClient);
        }
        clearNotification();
        //MediaButtonHelper.unregisterMediaButtonEventReceiverCompat(MusicService.myAudioManager, MusicService.mRemoteControlResponder);
        //RemoteControlHelper.unregisterRemoteControlClient(MusicService.myAudioManager, MusicService.remoteControlClientCompat);

        if (Debuger.DEBUG)
            Log.v("onDestroy", "MainPageActivity - onDestroy()");
        if (Debuger.DEBUG)
            Log.v("IN_ACTIVITY", " DESTROY INSIDE ACTIVITY");
        if (MusicService.ALARM_PLAYED)
        {
            if (Debuger.DEBUG)
                Log.v("ALARM", " onDestroy() - stop alarm service");
            //RadioAlarmReceiver.completeWakefulIntent(MusicService.wakefulIntent);
        }
        if (MusicService.media != null &&
                (MusicService.media.isPlaying() || !MusicService.media.isPlaying())
                )
        {
            if (Debuger.DEBUG)
                Log.v("IN_ACTIVITY", " DESTROY INSIDE IF statement");
            MusicService.media.stop();
            MusicService.media.reset();
            MusicService.media.release();
            MusicService.media = null;
            stopService(radioIntent);
        }
        stopService(new Intent(getBaseContext(), KillNotificationsService.class));
    }


    private void clearNotification()
    {
        NotificationManager mNotificationManager = (NotificationManager) (getSystemService(Context.NOTIFICATION_SERVICE));
        mNotificationManager.cancel(TAG, 01);
    }

}