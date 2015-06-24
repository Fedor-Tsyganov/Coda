package com.fedortsyganov.iptest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by fedortsyganov on 5/25/15.
 */
public class KillNotificationsService extends Service
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i("onCreate", "KillNotifi - onCreate");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Log.i("onTaskRemoved", "App is no longer working");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        stopForeground(false);
        Log.i("onUnbind", "KillNotifi - onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent)
    {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("onStartCom", "KillNotifi - onStartCom");
        return super.onStartCommand(intent, flags, startId);
    }
}
