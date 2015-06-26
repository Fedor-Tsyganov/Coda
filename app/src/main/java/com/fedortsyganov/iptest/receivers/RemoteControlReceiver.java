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
import android.util.Log;
import android.view.KeyEvent;

import com.fedortsyganov.iptest.MusicService;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.fragments.FragmentRadioList;

/**
 * Created by fedortsyganov on 5/14/15.
 */
public class RemoteControlReceiver extends BroadcastReceiver
{
    private Context con;
    private Intent startRadioIntent;
    private static final String PREVIOUS_STATION = "com.fedortsyganov.iptest.PREVIOUS_STATION";
    private static final String NEXT_STATION = "com.fedortsyganov.iptest.NEXT_STATION";
    private static final String STOP_PLAY = "com.fedortsyganov.iptest.STOP_PLAY";
    private static final String STATION_TO_SAVE = "info";
    private static final String NOTIFICATION_CONTROLS = "NotificationCont";
    private static final String TAG = "MediaNotification";
    private static final int NOTIFICATION_ID = 101;
    private NotificationManager notificationManager;
    private Notification notification;
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        con = context;
        startRadioIntent = new Intent(con, MusicService.class);
        startRadioIntent.putExtra("SERVICE","START");
        preferences = context.getSharedPreferences(STATION_TO_SAVE, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
        {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode())
            {
                // Handle key press.
                if ( RadioMainPageActivity.remoteControlCounter < 2)
                    RadioMainPageActivity.remoteControlCounter++;
                else
                {
                    RadioMainPageActivity.remoteControlCounter = 0;
                    if (RadioMainPageActivity.counter % 2 == 0 && MusicService.media == null)
                    {
                        startRadio();
                    } else
                    {
                        stopRadio();
                    }
                    RadioMainPageActivity.counter++;
                    Log.v("myreceiver", "from bluetooth");
                }
            }
            else if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode())
            {
                if ( RadioMainPageActivity.remoteControlCounter < 2)
                    RadioMainPageActivity.remoteControlCounter++;
                else
                {
                    RadioMainPageActivity.remoteControlCounter = 0;
                    if (MusicService.media != null)
                    {
                        stopRadio();
                    }
                    if (RadioMainPageActivity.radioStationPosition <= 0)
                    {
                        RadioMainPageActivity.radioStationPosition = RadioMainPageActivity.previousStationsList.size() - 1;
                    } else
                    {
                        RadioMainPageActivity.radioStationPosition--;
                    }
                    if (MusicService.media != null)
                    {
                        startRadio();
                    }
                }
            }
            else if (KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode())
            {
                if ( RadioMainPageActivity.remoteControlCounter < 2)
                    RadioMainPageActivity.remoteControlCounter++;
                else
                {
                    RadioMainPageActivity.remoteControlCounter = 0;
                    if (RadioMainPageActivity.counter % 2 == 0 && MusicService.media == null)
                    {
                        startRadio();
                    } else
                    {
                        stopRadio();
                    }
                    RadioMainPageActivity.counter++;
                    Log.v("myreceiver", "from bluetooth KEYCODE_MEDIA_PAUSE");
                }
            }
            else if (KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode())
            {
                if ( RadioMainPageActivity.remoteControlCounter < 2)
                    RadioMainPageActivity.remoteControlCounter++;
                else
                {
                    RadioMainPageActivity.remoteControlCounter = 0;
                    if (RadioMainPageActivity.counter % 2 == 0 && MusicService.media == null)
                    {
                        startRadio();
                    } else
                    {
                        stopRadio();
                    }
                    RadioMainPageActivity.counter++;
                    Log.v("myreceiver", "from bluetooth KEYCODE_MEDIA_STOP");
                }
            }
            else if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode())
            {
                if ( RadioMainPageActivity.remoteControlCounter < 2)
                    RadioMainPageActivity.remoteControlCounter++;
                else
                {
                    RadioMainPageActivity.remoteControlCounter = 0;
                    if (RadioMainPageActivity.counter % 2 == 0 && MusicService.media == null)
                    {
                        startRadio();
                    } else
                    {
                        stopRadio();
                    }
                    RadioMainPageActivity.counter++;
                    Log.v("myreceiver", "from bluetooth KEYCODE_MEDIA_PLAY");
                }
            }
            else if (KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK == event.getKeyCode())
            {
                Log.v("myreceiver", "from bluetooth KEYCODE_MEDIA_AUDIO_TRACK");
            }
            else if (KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode())
            {
                if ( RadioMainPageActivity.remoteControlCounter < 2)
                    RadioMainPageActivity.remoteControlCounter++;
                else
                {
                    RadioMainPageActivity.remoteControlCounter = 0;
                    if (MusicService.media != null)
                    {
                        stopRadio();
                    }
                    if (RadioMainPageActivity.radioStationPosition < RadioMainPageActivity.previousStationsList.size() - 1)
                    {
                        RadioMainPageActivity.radioStationPosition++;
                    } else
                    {
                        RadioMainPageActivity.radioStationPosition = 0;
                    }

                    if (MusicService.media != null)
                    {
                        startRadio();
                    }
                }
            }
            else
            {
                Log.v("myreceiver", "from bluetooth else");
            }
            RadioMainPageActivity.radioStation = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition);
            if (FragmentRadioList.adapter != null)
                FragmentRadioList.adapter.notifyDataSetChanged();
            createNotificationControls(RadioMainPageActivity.counter);
        }
    }
    private void startRadio()
    {
        con.startService(startRadioIntent);
        RadioMainPageActivity.isPlaying = true;
        RadioMainPageActivity.isPaused = false;
        if (FragmentRadioList.adapter != null)
            FragmentRadioList.adapter.notifyDataSetChanged();
    }

    private void stopRadio()
    {
        con.stopService(startRadioIntent);
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
            notificationManager.cancel(TAG, NOTIFICATION_ID);
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
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        else
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.flags |= Notification.FLAG_NO_CLEAR;
        //FragmentMainPage.updateInfoBox();
        RadioPlayerActivity.updateInfoBox();
        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
        abortBroadcast();
    }



    @TargetApi(21)
    public void createNotificationAPI21(int num)
    {
        int icon;

        if (num %2 == 0)
        {
            notificationManager =  (NotificationManager) (con.getSystemService(Context.NOTIFICATION_SERVICE));
            notificationManager.cancel(TAG, NOTIFICATION_ID);
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
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

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
        return new Notification.Action.Builder(icon, title, pendingIntent ).build();

    }

    private void createNotificationControls(int number)
    {
        if (Build.VERSION.SDK_INT > 20)
            createNotificationAPI21(number);
        else if (Build.VERSION.SDK_INT > 15 && Build.VERSION.SDK_INT <= 20)
            createNotificationAPI16(number);
        else
            createNotificationAPI14();
    }
}