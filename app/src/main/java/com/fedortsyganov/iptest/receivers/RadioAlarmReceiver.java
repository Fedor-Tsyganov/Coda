package com.fedortsyganov.iptest.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.fedortsyganov.iptest.MusicService;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioPlayerActivity;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by fedortsyganov on 2/19/15.
 */
public class RadioAlarmReceiver extends WakefulBroadcastReceiver
{
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Intent service;
    private Context mContext;
    private String text, title, bigText;
    private static final String SERVICE = "SERVICE";
    private static final String ALARM = "STOP_ALARM";
    private static final String ALARM_TAG = "AlarmNotification";
    private static final int ALARM_NOTIFICATION_ID = 1;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        service = new Intent(context, MusicService.class);
        service.putExtra(SERVICE, ALARM);
        mContext = context;
        title = mContext.getString(R.string.alarm_notification_title);
        bigText = mContext.getString(R.string.alarm_notification_big_text);
        text = mContext.getString(R.string.alarm_notification_text);
        sendAlarmNotification();
        startWakefulService(context, service);
    }
    public void setAlarm (Context context, int hour, int minute)
    {
        GregorianCalendar cal = new GregorianCalendar();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RadioAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        DateTime now = DateTime.now();
        int dayOfYear = now.getDayOfYear();
        long time = now.getSecondOfMinute() * 1000;
        if (hour >= now.getHourOfDay())
        {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }
        else
        {
            if (cal.isLeapYear(now.getYear()))
            {
                if (dayOfYear < 365)
                {
                    //ok
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear+1);
                }
                else
                {
                    //
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.YEAR, now.getYear()+1);
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                }
            }
            else
            {
                if (dayOfYear < 364)
                {
                    //ok
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear+1);
                }
                else
                {
                    //
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.YEAR, now.getYear()+1);
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                }
            }
        }
        //Log.v("ALARM", "SECONDS: " + time);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - time, AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - time, pendingIntent);
    }

    private void sendAlarmNotification()
    {
        Bitmap mBitmap;

        //checking for a version to show a correct icon.
        if (Build.VERSION.SDK_INT > 20)
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_notification_alarm_big);
        else
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_notification_alarm_small);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent serviceMusic = new Intent(mContext, RadioPlayerActivity.class);
        //serviceMusic.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        //mContext.sendBroadcast(serviceMusic);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, serviceMusic, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.icon_notification_alarm_small)
                .setLargeIcon(mBitmap)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentText(text);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(ALARM_TAG, ALARM_NOTIFICATION_ID, builder.build());
    }

}
