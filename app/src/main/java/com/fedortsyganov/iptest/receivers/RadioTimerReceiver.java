package com.fedortsyganov.iptest.receivers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fedortsyganov.iptest.MusicService;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.helpers.Debuger;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by fedortsyganov on 4/5/15.
 */
public class RadioTimerReceiver extends BroadcastReceiver
{
    private static final String CANCEL_TIMER = "com.fedortsyganov.iptest.CANCEL_TIMER";
    private Context con;
    private AlarmManager timerManager;
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    private Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        con = context;
        String action = intent.getAction();
        if (CANCEL_TIMER.equals(action))
        {
            timerManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent cancelIntent = new Intent(context, RadioTimerReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 52, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT );
            timerManager.cancel(pendingIntent);
        }
        else
        {
            if (Debuger.DEBUG)
                Log.v("SleepTimer", "onReceive");
            if (isMyServiceRunning(MusicService.class))
            {
                if (Debuger.DEBUG)
                    Log.v("SleepTimer", "stopService");
                if (MusicService.media != null)
                {
                    context.sendBroadcast(new Intent("com.fedortsyganov.iptest.STOP_PLAY"));
                    //MusicService.serviceON = false;
                    MusicService.media.stop();
                    MusicService.media.reset();
                    MusicService.media.release();
                    MusicService.media = null;
                }
                RadioPlayerActivity.bPlay.setChecked(false);
                context.stopService(intent);
                notificationManager = (NotificationManager) (con.getSystemService(Context.NOTIFICATION_SERVICE));
                notificationManager.cancel(01);
                notificationManager.cancel(52);
            }
            mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            con.startActivity(mIntent);
        }
    }

    public void setTimer(Context context, int min)
    {
        timerManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RadioTimerReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 52, intent, 0);
        Calendar calendar = Calendar.getInstance();
        DateTime now = DateTime.now();
        int minNow = now.getMinuteOfHour();
        //calendar.set(Calendar.MINUTE, minute);
        if (now.getHourOfDay() < 23)
        {
            if ((minNow+min) < 60)
            {
                calendar.set(Calendar.MINUTE, minNow+min);
            }
            else
            {
                calendar.set(Calendar.HOUR_OF_DAY, now.getHourOfDay()+1);
                calendar.set(Calendar.MINUTE, minNow-60+min);
            }
        }
        else
        {
            if ((minNow+min) < 60)
            {
                calendar.set(Calendar.MINUTE, minNow+min);
            }
            else
            {
                calendar.set(Calendar.DAY_OF_YEAR, now.getDayOfYear()+1);
                calendar.set(Calendar.HOUR_OF_DAY, now.getHourOfDay()+1);
                calendar.set(Calendar.MINUTE, minNow-60+min);
            }
        }
        timerManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        createNotification(context);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
    private void createNotification(Context context)
    {
        Intent cancelTimerIntent = new Intent();
        cancelTimerIntent.setAction(CANCEL_TIMER);
        //PendingIntent pIntent = PendingIntent.getActivity(context, 33, new Intent(), 0);

        //used to be 32
        PendingIntent pendingIntentCancelTimer
                = PendingIntent.getBroadcast(context.getApplicationContext(), 52, cancelTimerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context.getApplicationContext())
                .setContentTitle(context.getString(R.string.sleep_timer_title))
                .setContentText(context.getString(R.string.sleep_timer_text))
                .setSmallIcon(R.drawable.icon_notification_alarm)
                .setContentIntent(pendingIntentCancelTimer)
                .addAction(android.R.color.transparent, context.getString(R.string.dialog_cancel), pendingIntentCancelTimer)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        //33
        notificationManager.notify(52, noti);

    }
}
