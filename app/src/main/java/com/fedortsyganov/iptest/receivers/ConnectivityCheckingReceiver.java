package com.fedortsyganov.iptest.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.fedortsyganov.iptest.MusicService;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by fedortsyganov on 3/17/15.
 */
public class ConnectivityCheckingReceiver extends WakefulBroadcastReceiver
{
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (MusicService.media != null)
        {
            if (!MusicService.media.isPlaying())
            {
                Log.v("Music", "Music is NOT playing !!!");
            } else
            {
                Log.v("Music", "Music is playing !!!");
            }
        }
        else
        {
            Log.v("Music", "User stopped player !!!");
        }
    }
    public void setAlarm (Context context, int hour, int minute)
    {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ConnectivityCheckingReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        //calendar.set(Calendar.SECOND, second);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
