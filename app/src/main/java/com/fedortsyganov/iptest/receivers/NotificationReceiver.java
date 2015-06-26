package com.fedortsyganov.iptest.receivers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.fedortsyganov.iptest.MusicService;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.fragments.FragmentRadioList;

/**
 * Created by fedortsyganov on 3/6/15.
 */
public class NotificationReceiver extends BroadcastReceiver
{
    private static final String PREVIOUS_STATION = "com.fedortsyganov.iptest.PREVIOUS_STATION";
    private static final String NEXT_STATION = "com.fedortsyganov.iptest.NEXT_STATION";
    private static final String STOP_PLAY = "com.fedortsyganov.iptest.STOP_PLAY";
    private static final String STATION_TO_SAVE = "info";
    private static final String NOTIFICATION_CONTROLS = "NotificationCont";
    private static final String TAG = "MediaNotification";
    private static final int NOTIFICATION_ID = 101;
    private Context con;
    private Intent startRadioIntent;
    private NotificationManager notificationManager;
    private Notification notification;
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        preferences = context.getSharedPreferences(STATION_TO_SAVE, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        con = context;
        startRadioIntent = new Intent(con, MusicService.class);
        startRadioIntent.putExtra("SERVICE","START");

        String action = intent.getAction();
        if (PREVIOUS_STATION.equalsIgnoreCase(action))
        {
            if (MusicService.media != null)
            {
                stopRadio();
            }
            if (RadioMainPageActivity.radioStationPosition <= 0)
            {
                RadioMainPageActivity.radioStationPosition = RadioMainPageActivity.previousStationsList.size() - 1;
            }
            else
            {
                RadioMainPageActivity.radioStationPosition--;
            }
            if (MusicService.media != null)
            {
                startRadio();
            }
        }
        if (STOP_PLAY.equalsIgnoreCase(action))
        {
            if (RadioMainPageActivity.counter % 2 == 0 && MusicService.media == null)
            {
                startRadio();
            }
            else
            {
                stopRadio();
            }
            RadioMainPageActivity.counter++;
        }
        if (NEXT_STATION.equalsIgnoreCase(action))
        {
            if (MusicService.media != null)
            {
                stopRadio();
            }
            if (RadioMainPageActivity.radioStationPosition < RadioMainPageActivity.previousStationsList.size() - 1)
            {
                RadioMainPageActivity.radioStationPosition++;
            }
            else
            {
                RadioMainPageActivity.radioStationPosition = 0;
            }

            if (MusicService.media != null)
            {
                startRadio();
            }
        }
        if (RadioMainPageActivity.previousStationsList != null)
            RadioMainPageActivity.radioStation = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition);
        if (FragmentRadioList.adapter != null)
            FragmentRadioList.adapter.notifyDataSetChanged();
        createNotificationControls(RadioMainPageActivity.counter);
    }

    private void startRadio()
    {
        con.startService(startRadioIntent);
        if (RadioMainPageActivity.ivPlayerControls != null)
        {
            String country = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationCountry();
            String name = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName();
            RadioMainPageActivity.ivPlayerControls.setImageResource(R.drawable.icon_pause_notification);
            RadioMainPageActivity.tvStationCountry.setText(country);
            RadioMainPageActivity.tvStationName.setText(name);
        }
        RadioMainPageActivity.isPlaying = true;
        RadioMainPageActivity.isPaused = false;
        if (FragmentRadioList.adapter != null)
            FragmentRadioList.adapter.notifyDataSetChanged();
    }

    private void stopRadio()
    {
        con.stopService(startRadioIntent);
        if (RadioMainPageActivity.ivPlayerControls != null)
        {
            RadioMainPageActivity.ivPlayerControls.setImageResource(R.drawable.icon_play_notification);
        }
        RadioMainPageActivity.isPlaying = false;
        RadioMainPageActivity.isPaused = true;
        if (FragmentRadioList.adapter != null)
            FragmentRadioList.adapter.notifyDataSetChanged();
    }

    @TargetApi(14)
    public void createNotificationAPI14()
    {
        Intent nextReceive = new Intent();
        Intent previousReceive = new Intent();
        Intent stopPlayReceive = new Intent();

        nextReceive.setAction(NEXT_STATION);
        previousReceive.setAction(PREVIOUS_STATION);
        stopPlayReceive.setAction(STOP_PLAY);

        Bitmap mBitmap = BitmapFactory.decodeResource(con.getResources(), R.drawable.notification_icon_small_coda);
        Intent intent = new Intent(con.getApplicationContext(), RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(con.getApplicationContext(), 0, intent, 0);
        notification = new Notification.Builder(con.getApplicationContext())
                .setContentTitle(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName())
                .setContentText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre())
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(mBitmap)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setDefaults(0)
                .setWhen(0)
                .getNotification();
        notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    @TargetApi(16)
    private void createNotificationAPI16(int num)
    {
        Intent nextReceive = new Intent();
        Intent previousReceive = new Intent();
        Intent stopPlayReceive = new Intent();

        prefEditor.putBoolean(NOTIFICATION_CONTROLS, true).commit();
        nextReceive.setAction(NEXT_STATION);
        previousReceive.setAction(PREVIOUS_STATION);
        stopPlayReceive.setAction(STOP_PLAY);
        //icon for stop or play
        int icon;

        if (num %2 == 0)
        {
            notificationManager =  (NotificationManager) (con.getSystemService(Context.NOTIFICATION_SERVICE));
            //notificationManager.cancel(01);
            icon = android.R.drawable.ic_media_play;
            if (RadioPlayerActivity.bPlay != null)
                RadioPlayerActivity.bPlay.setChecked(false);
            //FragmentMainPage.bPlay.setChecked(false);
        }
        else
        {
            icon= android.R.drawable.ic_media_pause;
            if (RadioPlayerActivity.bPlay != null)
                RadioPlayerActivity.bPlay.setChecked(true);
            //FragmentMainPage.bPlay.setChecked(true);
        }
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(con);

        Bitmap mBitmap = BitmapFactory.decodeResource(con.getResources(), R.drawable.notification_icon_small_coda);
        Intent intent = new Intent(con.getApplicationContext(), RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(con.getApplicationContext(), 0, intent, 0);

        PendingIntent pendingIntentStopPlay
                = PendingIntent.getBroadcast(con.getApplicationContext(), 0, stopPlayReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentPrevious
                = PendingIntent.getBroadcast(con.getApplicationContext(), 0, previousReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentNext
                = PendingIntent.getBroadcast(con.getApplicationContext(), 0, nextReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(con.getApplicationContext())
                .setContentTitle(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName())
                .setContentText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre())
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(mBitmap)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_media_rew, /*previous*/"", pendingIntentPrevious)
                .addAction(icon, /*play*/"", pendingIntentStopPlay)
                .addAction(android.R.drawable.ic_media_ff, /*next*/"", pendingIntentNext)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(0)
                .setWhen(0)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);

        if (num %2 == 0)
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        else
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        //notification.flags |= Notification.FLAG_NO_CLEAR;
        //FragmentMainPage.updateInfoBox();
        RadioPlayerActivity.updateInfoBox();
        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    @TargetApi(21)
    private void createNotificationAPI21(int num)
    {
        int icon;

        if (num %2 == 0)
        {
            notificationManager =  (NotificationManager) (con.getSystemService(Context.NOTIFICATION_SERVICE));
            //notificationManager.cancel(01);
            icon = android.R.drawable.ic_media_play;
            //FragmentMainPage.bPlay.setChecked(false);
            if (RadioPlayerActivity.bPlay != null)
                RadioPlayerActivity.bPlay.setChecked(false);
        }
        else
        {
            icon = android.R.drawable.ic_media_pause;
            //FragmentMainPage.bPlay.setChecked(true);
            if (RadioPlayerActivity.bPlay != null)
                RadioPlayerActivity.bPlay.setChecked(true);
        }
        prefEditor.putBoolean(NOTIFICATION_CONTROLS, true).commit();
        Bitmap bitmap = BitmapFactory.decodeResource(con.getResources(), R.drawable.notification_large_icon_test_two);
        Intent intent = new Intent(con.getApplicationContext(), RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(con.getApplicationContext(), 77, intent, 0);
        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
        notification = new Notification.Builder(con.getApplicationContext())
                .setContentTitle(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName())
                .setContentText(RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre())
                .setSmallIcon(R.drawable.icon_notification).setLargeIcon(bitmap)
                .setContentIntent(pendingIntent).addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", PREVIOUS_STATION))
                .addAction(generateAction(icon, "Play", STOP_PLAY))
                .addAction(generateAction(android.R.drawable.ic_media_next, "Next", NEXT_STATION))
                .setWhen(0)
                .setDefaults(0)
                //.setColor(getResources().getColor(R.color.blue_dark))
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);

        if (num %2 == 0)
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        else
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        //FragmentMainPage.updateInfoBox();
        RadioPlayerActivity.updateInfoBox();
        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    @TargetApi(21)
    private Notification.Action generateAction( int icon, String title, String intentAction )
    {
        Intent intent = new Intent();
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();

    }

    private void createNotificationControls(int num)
    {
        if (Build.VERSION.SDK_INT > 20)
            createNotificationAPI21(num);
        else if (Build.VERSION.SDK_INT > 15 && Build.VERSION.SDK_INT <= 20)
            createNotificationAPI16(num);
        else
            createNotificationAPI14();
    }
}
