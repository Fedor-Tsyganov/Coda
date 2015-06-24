package com.fedortsyganov.iptest;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.fedortsyganov.iptest.dialogs.DialogStations;
import com.fedortsyganov.iptest.helpers.AirPlaneModChecker;
import com.fedortsyganov.iptest.helpers.Debuger;
import com.fedortsyganov.iptest.helpers.GenreListSelector;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.receivers.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RadioPlayerActivity extends FragmentActivity implements View.OnClickListener
{
    private static final String PREVIOUS_STATION = "com.fedortsyganov.iptest.PREVIOUS_STATION";
    private static final String NEXT_STATION = "com.fedortsyganov.iptest.NEXT_STATION";
    private static final String STOP_PLAY = "com.fedortsyganov.iptest.STOP_PLAY";
    public RadioAlarmReceiver alarmReceiver = new RadioAlarmReceiver();
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String NOTIFICATION_CONTROLS = "NotificationCont";
    private static final String TAG = "MediaNotification";
    private static final String STATION_PREFS = "ListPosition";
    //shared preferences file string
    private static final String STATION_TO_SAVE = "info";
    private static final String SERVICE = "SERVICE";
    private static final String SERVICE_START = "START";
    //must add a view for ads
    public static ToggleButton bPlay;
    private Button bLastStation, bNextStation, bToChat;
    public static TextView tvStation, tvCountry, tvGanre;
    protected Intent startRadioIntent;
    public static ProgressBar progressBar;
    private AdView mAdView;
    private AdRequest adRequest;
    public NotificationManager notificationManager;
    private Notification notification;
    public SharedPreferences preferences, sharedPreferences;
    public SharedPreferences.Editor prefEditor;
    private Button bBack, bStations, bAlarmClock;
    private DateTime now;
    private boolean canConnect = true;
    public static boolean changeStation = false;
    private static final int NOTIFICATION_ID = 01;
    private boolean fromNotification = false;
    public static boolean changeList = true;
    public ArrayList <RadioStation> stationsBackUp;
    public static ArrayList <RadioStation> stationsBackUpStatic;
    public int intPositionFromMemory;
    public static int mRadioStationPosition;
    private final static String RESTART = "com.fedortsyganov.iptest.RESTART_PLAYER";

    //private AudioManager mAudioManager;
    //private ComponentName mRemoteControlResponder;
    //debug
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_from_bot, R.anim.fade_out);
        setContentView(R.layout.activity_radio_player);

        if (Debuger.DEBUG)
            Log.v("onCreate", " onCreate()");
        if (true)
        {
            NotificationManager mnotificationManager = (NotificationManager) (getSystemService(Context.NOTIFICATION_SERVICE));
            mnotificationManager.cancel(TAG, NOTIFICATION_ID);
        }

        //mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //mRemoteControlResponder = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());

        startRadioIntent = new Intent(getBaseContext(), MusicService.class);
        startRadioIntent.putExtra(SERVICE, SERVICE_START);
        //setting up buttons

        bToChat = (Button) findViewById(R.id.buttonToChat);
        bToChat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getBaseContext(), ChatTest.class));
            }
        });
        bPlay = (ToggleButton) findViewById(R.id.buttonPlay);
        bLastStation = (Button) findViewById(R.id.buttonPreviousStation);
        bNextStation = (Button) findViewById(R.id.buttonNextStation);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bBack= (Button) findViewById(R.id.buttonBack);
        bBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = RadioNavigationDrawer.mPosition;
                if (RadioNavigationDrawer.mPosition != 0)
                {
                    if ( position < 13 )
                        prefEditor.putInt(STATION_PREFS, position).commit();
                    //toast("position:" + position);
                    RadioMainPageActivity.currentStationsList
                            = GenreListSelector.selectList(position, getApplicationContext());
                }
                else
                {
                    int mPos = preferences.getInt(STATION_PREFS, 0);
                    if (mPos != 0)
                        prefEditor.putInt(STATION_PREFS, mPos).commit();
                    //toast("position:" + mPos);
                    RadioMainPageActivity.currentStationsList
                            = GenreListSelector.selectList(mPos, getApplicationContext());
                }
                Intent intent = new Intent(RadioPlayerActivity.this, RadioMainPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_top);
            }

        });
        bStations = (Button) findViewById(R.id.buttonStations);
        bStations.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();
            }
        });
        bAlarmClock = (Button) findViewById(R.id.buttonAlarmClock);
        bAlarmClock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                now = DateTime.now();
                RadialTimePickerDialog timePickerDialog
                        = new RadialTimePickerDialog();
                timePickerDialog.setDoneText(getResources().getString(R.string.dialog_alarm).toUpperCase());
                timePickerDialog.setStartTime(now.getHourOfDay(), now.getMinuteOfHour());
                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                timePickerDialog.setOnTimeSetListener(new RadialTimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int i, int i2)
                    {
                        if (Debuger.DEBUG)
                            Log.d("TIMER", "HOUR: " + i + " - MINUTE: " + i2);
                        //sendAlarmNotification();
                        alarmReceiver.setAlarm(getApplicationContext(), i, i2);
                    }
                });

            }
        });

        bPlay.setOnClickListener(this);
        bLastStation.setOnClickListener(this);
        bNextStation.setOnClickListener(this);

        //setting up text views
        tvStation = (TextView) findViewById(R.id.tvRadioName);
        tvCountry = (TextView) findViewById(R.id.tvRadioCountry);
        tvGanre = (TextView) findViewById(R.id.tvRadioGanre);

        tvStation.setTypeface(LauncherActivity.typefaceRobotoRegular);
        tvCountry.setTypeface(LauncherActivity.typefaceRobotoRegular);
        tvGanre.setTypeface(LauncherActivity.typefaceRobotoRegular);

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

        preferences = getSharedPreferences(STATION_TO_SAVE, MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        fromNotification = sharedPreferences.getBoolean(NOTIFICATION_CONTROLS, false);

        if (RadioMainPageActivity.currentStationsList == null ||
                RadioMainPageActivity.previousStationsList == null)
        {
            //send intent action and set up all stations again. When done with setting up,
            //assign RadioMainPage.currentStationList or .previousStationList saved genre
            //and assign .radioStationPosition saved number and startActivity(RadioPlayerActivity)
            //or service again.
            Intent restartIntent = new Intent(getBaseContext(), RadioMainPageActivity.class);
            restartIntent.setAction(RESTART);
            startActivity(restartIntent);

            //Log.e("StationPrefs", "-> " + preferences.getInt(STATION_PREFS, 0));

            //radioStation = getStation();
            //stationsBackUp = new ArrayList<>(GenreListSelector.selectListByGenere(radioStation.getStationGanre(),getBaseContext()));

            //these two methods set up data from memory if service is killed and need recovery.

            //setRadioStationPositionFromMemory();
            //copyStaticArray();

            // SET !!!!! UP all stations again (from csv)!
        }

        updateInfoBox();


        if (isMyServiceRunning(MusicService.class) && !fromNotification)
        {
            bPlay.setChecked(true);
            if (RadioMainPageActivity.currentStationsList != null && RadioMainPageActivity.previousStationsList != null)
            {
                if (!RadioMainPageActivity.currentStationsList.equals(RadioMainPageActivity.previousStationsList))
                {
                    RadioMainPageActivity.listsAreDifferent = true;
                } else
                {
                    RadioMainPageActivity.listsAreDifferent = false;
                }
                RadioMainPageActivity.currentStationsList = new ArrayList<>(RadioMainPageActivity.previousStationsList);
            }
            else
            {
                RadioMainPageActivity.currentStationsList = new ArrayList<>();
                RadioMainPageActivity.currentStationsList.add(getStation());
            }
            if (MusicService.ALARM_PLAYED)
                RadioMainPageActivity.counter++;
        }

        prefEditor.putBoolean(NOTIFICATION_CONTROLS, false).commit();

        //add connectivity checker here => from onClick!
        if (!AirPlaneModChecker.isAirplaneModeOn(getApplicationContext()))
        {
            if (connectionChecker())
            {
                if (!isMyServiceRunning(MusicService.class) && changeStation)
                {
                    RadioMainPageActivity.previousStationsList = new ArrayList<>(RadioMainPageActivity.currentStationsList);
                    play();
                }
                else if (isMyServiceRunning(MusicService.class) && changeStation)
                {
                    //RadioMainPageActivity.previousStationsList = new ArrayList<>(RadioMainPageActivity.currentStationsList);
                    changeStation = false;
                    stopAndPlayNew();

                } else
                {
                    //enter from notification => don't change anything
                }
            }
        }
        else
        {
            toast(getString(R.string.connection_checker_airplane_mode));
        }

    }



    void copyStaticArray()
    {
        stationsBackUpStatic = new ArrayList<>(stationsBackUp);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                Log.v("myreceiver","from bluetooth !!!");
                return true;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.v("myreceiver","from bluetooth !!!");
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                Log.v("myreceiver","from bluetooth !!!");
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.v("myreceiver","from bluetooth !!!");
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                Log.v("myreceiver","from bluetooth !!!");
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                Log.v("myreceiver","from bluetooth !!!");
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v)
    {
        WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (sharedPreferences.getBoolean(getString(R.string.preferences_wifi), false))
        {
            canConnect = false;
            //toast("Wi-Fi mode ON");
            if (wifi.isWifiEnabled())
            {
                if (Debuger.DEBUG)
                    Log.v("WIFI_CODA","wifi enabled");
                if (mWifi.isConnected())
                {
                // Do whatever
                    canConnect = true;
                    if (Debuger.DEBUG)
                        Log.v("WIFI_CODA","wifi connected");
                }
                else
                {
                    if (Debuger.DEBUG)
                        Log.v("WIFI_CODA","wifi not connected");
                    bPlay.setChecked(false);
                    toast(getString(R.string.connection_checker_wifi_notcnctd));
                }
            }
            else
            {
                bPlay.setChecked(false);
                toast(getString(R.string.connection_checker_no_wifi));
            }
        }
        else
        {
            //toast("Wi-Fi mode OFF");
            canConnect = true;
        }
        if (canConnect)
        {
            switch (v.getId())
            {
                case R.id.buttonPlay:
                    play();
                    break;
                case R.id.buttonPreviousStation:
                    if (MusicService.media != null)
                    {
                        stopRadio();
                    }
                    if (RadioMainPageActivity.radioStationPosition <= 0)
                    {
                        RadioMainPageActivity.radioStationPosition = RadioMainPageActivity.currentStationsList.size() - 1;
                    }
                    else
                    {
                        RadioMainPageActivity.radioStationPosition--;
                    }
                    if (MusicService.media != null)
                    {
                        startRadio();
                        saveStation(RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition));
                    }
                    Integer numbr = new Integer(RadioMainPageActivity.counter);
                    numbr = numbr+3;
                    //createNotificationControls(numbr--);
                    updateInfoBox();
                    RadioMainPageActivity.radioStation = RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition);
                    break;
                case R.id.buttonNextStation:
                    if (MusicService.media != null)
                    {
                        stopRadio();
                    }
                    if (RadioMainPageActivity.radioStationPosition < RadioMainPageActivity.currentStationsList.size() - 1)
                    {
                        RadioMainPageActivity.radioStationPosition++;
                    } else
                    {
                        RadioMainPageActivity.radioStationPosition = 0;
                    }

                    if (MusicService.media != null)
                    {
                        startRadio();
                        saveStation(RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition));
                    }
                    Integer numNext = new Integer(RadioMainPageActivity.counter);
                    //createNotificationControls(numNext+1);
                    updateInfoBox();
                    RadioMainPageActivity.radioStation = RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition);
                    break;
                default:
                    break;
            }
        }
    }

    //in RadioPlayerActivity it
    //updates 3 text views with information about radio station
    public static void updateInfoBox()
    {
        if (Debuger.DEBUG)
        {
            String current = "current:"
                    + RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName();
            String previous = " previous:"
                    + RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName();
            String rdStn = " radioStation:"
                    + RadioMainPageActivity.radioStation.getStationName();
            Log.e("updateInfoBox", current + previous + rdStn);
        }
        boolean stationList = RadioMainPageActivity.currentStationsList != null;
        boolean previousStationlist = RadioMainPageActivity.previousStationsList != null;
        /*STATION_NAME+*/
        boolean listSize = false;
        if (stationList && previousStationlist)
            listSize = RadioMainPageActivity.previousStationsList.size() <= RadioMainPageActivity.radioStationPosition;

        if (previousStationlist && listSize)
        {
            if (tvStation != null && stationList)
                tvStation.setText(RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName());
        /*STATION_COUNTRY+*/
            if (tvCountry != null && stationList)
                tvCountry.setText(RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition).getStationCountry());

        /*STATION_GENRE+*/
            if (tvGanre != null && stationList)
                tvGanre.setText(RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre());
        }
        else
        {
            if (tvStation != null && previousStationlist)
                tvStation.setText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName());
        /*STATION_COUNTRY+*/
            if (tvCountry != null && previousStationlist)
                tvCountry.setText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationCountry());
        /*STATION_GENRE+*/
            if (tvGanre != null && previousStationlist)
                tvGanre.setText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre());
        }
    }



    private void updateFromMemory()
    {
        RadioMainPageActivity.currentStationsList = new ArrayList<>(stationsBackUpStatic);
        //RadioMainPageActivity.radioStationPosition
    }

    private void startRadio()
    {
        if (RadioMainPageActivity.ivPlayerControls != null)
            RadioMainPageActivity.ivPlayerControls.setImageResource(R.drawable.icon_pause_notification);
        progressBar.setVisibility(View.VISIBLE);
        startService(startRadioIntent);
    }

    private void stopRadio()
    {
        if (RadioMainPageActivity.ivPlayerControls != null)
            RadioMainPageActivity.ivPlayerControls.setImageResource(R.drawable.icon_play_notification);
        stopService(startRadioIntent);
    }

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

    //notification switch between api 21 and lower versions
    public void createNotificationControls(int num)
    {
        if (Build.VERSION.SDK_INT > 20)
            createNotificationAPI21(num);
        else
            createNotificationAPI16(num);
    }

    @TargetApi(16)
    private void createNotificationAPI16(int num)
    {
        Intent nextReceive = new Intent();
        Intent previousReceive = new Intent();
        Intent stopPlayReceive = new Intent();

        nextReceive.setAction(NEXT_STATION);
        previousReceive.setAction(PREVIOUS_STATION);
        stopPlayReceive.setAction(STOP_PLAY);
        //icon for stop or play
        int icon;

        if (num %2 == 0)
            icon= android.R.drawable.ic_media_pause;
        else
        {
            notificationManager =  (NotificationManager) (getSystemService(Context.NOTIFICATION_SERVICE));
            //notificationManager.cancel(NOTIFICATION_ID);
            icon = android.R.drawable.ic_media_play;
        }

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon_small_coda);
        Intent intent = new Intent(getApplicationContext(), RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        PendingIntent pendingIntentStopPlay
                = PendingIntent.getBroadcast(getApplicationContext(), 0, stopPlayReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentPrevious
                = PendingIntent.getBroadcast(getApplicationContext(), 0, previousReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentNext
                = PendingIntent.getBroadcast(getApplicationContext(), 0, nextReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName())
                .setContentText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre())
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(mBitmap)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_media_rew, /*previous*/"", pendingIntentPrevious)
                .addAction(icon, /*play*/"", pendingIntentStopPlay)
                .addAction(android.R.drawable.ic_media_ff, /*next*/"", pendingIntentNext)
                .setOnlyAlertOnce(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(0)
                .setWhen(0)
                .build();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (num %2 == 0)
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        else
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(TAG, 01, notification);
    }

    @TargetApi(21)
    public void createNotificationAPI21(int num)
    {
        int icon;
        if (num %2 == 0)
            icon= android.R.drawable.ic_media_pause;
        else
        {
            notificationManager =  (NotificationManager) (getSystemService(Context.NOTIFICATION_SERVICE));
            //notificationManager.cancel(NOTIFICATION_ID);
            icon = android.R.drawable.ic_media_play;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_large_icon_test_two);
        Intent intent = new Intent(getApplicationContext(), RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 77, intent, 0);
        notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName())
                .setContentText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre())
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent).addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", PREVIOUS_STATION))
                .addAction(generateAction(icon, "Play", STOP_PLAY))
                .addAction(generateAction( android.R.drawable.ic_media_next, "Next", NEXT_STATION))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(0)
                .setWhen(0)
                //.setColor(getResources().getColor(R.color.blue_dark))
                .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(new int[] {0, 1, 2}))
                .build();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (num %2 == 0)
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        else
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


        notificationManager.notify(TAG, 01, notification);
    }

    //api 21 notification builder
    @TargetApi(21)
    private Notification.Action generateAction( int icon, String title, String intentAction )
    {
        Intent intent = new Intent();
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    private RadioStation getStation()
    {
        Gson gson = new Gson();
        String json = preferences.getString("RadioStation", "");
        RadioStation station = gson.fromJson(json, RadioStation.class);
        intPositionFromMemory = (int) preferences.getFloat("RadioStationPosition",0);
        if (DEBUG)
            Log.v("GSON", "Object retrived:" + station.getStationCountry() + "-" + station.getStationName() + "-" + station.getStationGanre());
        return station;
    }

    //method to return radio station position from memory if it was null
    private int getRadioPosition()
    {
        return intPositionFromMemory = (int) preferences.getFloat("RadioStationPosition",0);
    }

    private void setRadioStationPositionFromMemory()
    {
        mRadioStationPosition = new Integer(getRadioPosition());
    }

    private void saveStation(RadioStation station)
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
        if (Debuger.DEBUG)
            Log.v("saved_station", "" + json);
        if (Debuger.DEBUG)
            Log.v("GSON","Object stored");
    }

    //save station.
    private void backupStation(RadioStation station)
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
        String str = "RadioStation";
        prefEditor.putString(str, json).commit();
    }

    void showDialog()
    {
        DialogStations ds = new DialogStations(RadioPlayerActivity.this);
        ds.show();
    }

    public boolean connectionChecker()
    {
        boolean connect = false;
        WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (sharedPreferences.getBoolean(getString(R.string.preferences_wifi), false))
        {
            canConnect = false;
            //toast("Wi-Fi mode ON");
            if (wifi.isWifiEnabled())
            {
                if (Debuger.DEBUG)
                    Log.v("WIFI_CODA","wifi enabled");
                if (mWifi.isConnected())
                {
                    // Do whatever
                    canConnect = true;
                    if (Debuger.DEBUG)
                        Log.v("WIFI_CODA","wifi connected");
                }
                else
                {
                    if (Debuger.DEBUG)
                        Log.v("WIFI_CODA","wifi not connected");
                    bPlay.setChecked(false);
                    toast(getString(R.string.connection_checker_wifi_notcnctd));
                }
            }
            else
            {
                bPlay.setChecked(false);
                toast(getString(R.string.connection_checker_no_wifi));
            }
        }
        else
        {
            //toast("Wi-Fi mode OFF");
            canConnect = true;
            connect = true;
            return connect;
        }
        if (canConnect)
        {
            connect = true;
            return connect;
        }
        return connect;
    }

    //generates toast messages
    public void toast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // start/stop music service
    public void play()
    {
        boolean mserv = MusicService.media == null;
        if (Debuger.DEBUG)
        {
            Log.v("PlayerTracking", "MusicService null:" + mserv);
            Log.v("PlayerTracking", "URL:" + RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationUrl());
        }

        if (RadioMainPageActivity.counter % 2 == 0 && MusicService.media == null)
        {
            startRadio();
            if (RadioMainPageActivity.previousStationsList.size() > RadioMainPageActivity.radioStationPosition
                    && RadioMainPageActivity.currentStationsList.size() > RadioMainPageActivity.radioStationPosition )
            {
                if (!RadioMainPageActivity.listsAreDifferent)
                    saveStation(RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition));
                else
                    saveStation(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition));
            }
            ConnectivityCheckingReceiver checkingReceiver = new ConnectivityCheckingReceiver();
            DateTime now = DateTime.now();
            int min = now.getMinuteOfHour();
            bPlay.setChecked(true);
            //Log.v("Minute", ":"+min);
            checkingReceiver.setAlarm(getApplicationContext(), now.getHourOfDay(), min + 1);
        }
        else
        {
            stopRadio();
            progressBar.setVisibility(View.GONE);
        }
        if (changeList)
            RadioMainPageActivity.previousStationsList = new ArrayList<>(RadioMainPageActivity.currentStationsList);
        //createNotificationControls(RadioMainPageActivity.counter);
        RadioMainPageActivity.counter++;
        updateInfoBox();
    }

    public void stopAndPlayNew()
    {
        if (MusicService.media != null)
        {
            MusicService.media.stop();
            MusicService.media.reset();
            MusicService.media.release();
            MusicService.media = null;
            stopRadio();
            RadioMainPageActivity.counter++;
        }
        play();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (true)
        {
            NotificationManager mnotificationManager = (NotificationManager) (getSystemService(Context.NOTIFICATION_SERVICE));
            mnotificationManager.cancel(TAG, NOTIFICATION_ID);
        }
        if (Debuger.DEBUG)
            Log.v("onRestart", " onRestart()");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
        if (Debuger.DEBUG)
            Log.v("onResume", " onResume()");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (Debuger.DEBUG)
            Log.v("onPause", " onPause()");
    }

    @Override
    protected void onStop()
    {

        Integer numNext = new Integer(RadioMainPageActivity.counter);
        createNotificationControls(numNext - 1);
        if (Debuger.DEBUG)
            Log.v("onStop", " onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        MusicService.unSetSession();
        MusicService.myAudioManager.unregisterRemoteControlClient(MusicService.remoteControlClient);
        MusicService.myAudioManager.unregisterMediaButtonEventReceiver(MusicService.mRemoteControlResponder);
        RadioMainPageActivity.applyMargin = false;
        //MediaButtonHelper.unregisterMediaButtonEventReceiverCompat(MusicService.myAudioManager, MusicService.mRemoteControlResponder);
        //RemoteControlHelper.unregisterRemoteControlClient(MusicService.myAudioManager, MusicService.remoteControlClientCompat);

        mAdView.destroy();
        if (Debuger.DEBUG)
            Log.v("onDestroy", " onDestroy()");
        //might need to remove code below

        //stop music and cancel notification on exit
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
            stopRadio();
        }
        notificationManager.cancel(01);
        super.onDestroy();

    }


    private boolean isNotificationVisible(Context context, int id)
    {
        Intent notificationIntent = new Intent(context, RadioPlayerActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    public void onBackPressed()
    {
        int position = RadioNavigationDrawer.mPosition;
        if (RadioNavigationDrawer.mPosition != 0)
        {
            if ( position < 13 )
                prefEditor.putInt(STATION_PREFS, position).commit();
            //toast("position:" + position);
            RadioMainPageActivity.currentStationsList
                    = GenreListSelector.selectList(position, getApplicationContext());
        }
        else
        {
            int mPos = preferences.getInt(STATION_PREFS, 0);
            if (mPos != 0)
                prefEditor.putInt(STATION_PREFS, mPos).commit();
            //toast("position:" + mPos);
            RadioMainPageActivity.currentStationsList
                    = GenreListSelector.selectList(mPos, getApplicationContext());
        }
        Intent intent = new Intent(RadioPlayerActivity.this, RadioMainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_top);
    }
}
