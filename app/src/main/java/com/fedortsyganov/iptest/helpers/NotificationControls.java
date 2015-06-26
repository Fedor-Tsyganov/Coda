package com.fedortsyganov.iptest.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioPlayerActivity;

/**
 * Created by fedortsyganov on 5/25/15.
 */
public class NotificationControls
{
    private Notification notification;
    public SharedPreferences preferences, sharedPreferences;
    public NotificationManager notificationManager;
    private static final String PREVIOUS_STATION = "com.fedortsyganov.iptest.PREVIOUS_STATION";
    private static final String NEXT_STATION = "com.fedortsyganov.iptest.NEXT_STATION";
    private static final String STOP_PLAY = "com.fedortsyganov.iptest.STOP_PLAY";
    private static final String TAG = "MediaNotification";
    private static final int NOTIFICATION_ID = 101;
    //shared preferences file string
    private Context con;

    public NotificationControls(int num, Context context)
    {
        con = context;
        createNotificationControls(num, con);
    }
    public void createNotificationControls(int num, Context context)
    {
        if (Build.VERSION.SDK_INT > 20)
            createNotificationAPI21(num, context);
        else
            createNotificationAPI16(num, context);
    }

    @TargetApi(16)
    private void createNotificationAPI16(int num, Context context)
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
            notificationManager =  (NotificationManager) (context.getSystemService(Context.NOTIFICATION_SERVICE));
            //notificationManager.cancel(NOTIFICATION_ID);
            icon = android.R.drawable.ic_media_play;
        }

        Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon_small_coda);
        Intent intent = new Intent(context, RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        PendingIntent pendingIntentStopPlay
                = PendingIntent.getBroadcast(context, 0, stopPlayReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentPrevious
                = PendingIntent.getBroadcast(context, 0, previousReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentNext
                = PendingIntent.getBroadcast(context, 0, nextReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(context)
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
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (num %2 == 0)
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        else
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        //notification.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    @TargetApi(21)
    public void createNotificationAPI21(int num, Context context)
    {
        int icon;
        if (num %2 == 0)
            icon= android.R.drawable.ic_media_pause;
        else
        {
            notificationManager =  (NotificationManager) (context.getSystemService(Context.NOTIFICATION_SERVICE));
            //notificationManager.cancel(NOTIFICATION_ID);
            icon = android.R.drawable.ic_media_play;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_icon_test_two);
        Intent intent = new Intent(context, RadioPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 77, intent, 0);
        notification = new Notification.Builder(context)
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
                .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                .build();

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //was in reverse!
        if (num %2 == 0)
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        else
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;


        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    //api 21 notification builder
    @TargetApi(21)
    private Notification.Action generateAction( int icon, String title, String intentAction )
    {
        Intent intent = new Intent();
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }
}
