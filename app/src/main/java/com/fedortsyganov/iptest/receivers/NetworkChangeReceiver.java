package com.fedortsyganov.iptest.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fedortsyganov.iptest.MusicService;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.helpers.Debuger;
import com.fedortsyganov.iptest.helpers.NotificationControls;

/**
 * Created by fedortsyganov on 2/24/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver
{
    TelephonyManager telephony;
    boolean connected = false;
    ConnectivityManager cm;
    NetworkInfo networkInfo;
    Context con;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        con = context;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = cm.getActiveNetworkInfo();

        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        //int type = networkInfo.getType();

        //String typeName = networkInfo.getTypeName();
        if (networkInfo!= null)
            connected = networkInfo.isConnected();
        //Log.v("CODA", typeName);
        if (connected)
        {
            if (Debuger.DEBUG)
            {
                Log.v("CODA", "CONNECTED");
                Log.v("CODA", RadioMainPageActivity.connectivityCounter + "");
            }

            if ( RadioMainPageActivity.connectivityCounter < 2)
                RadioMainPageActivity.connectivityCounter++;
            else
            {
                RadioMainPageActivity.connectivityCounter = 0;
                if (MusicService.serviceON)
                {
                    Intent mIntent = new Intent(context, MusicService.class);
                    mIntent.putExtra("SERVICE", "RECONNECT");
                    context.stopService(mIntent);
                    context.startService(mIntent);
                }
            }
            //Intent mIntent = new Intent(context, MusicService.class);
            //mIntent.putExtra("SERVICE", "RECONNECT");
            //context.startService(mIntent);
        }
        else
        {
            if (Debuger.DEBUG)
                Log.v("CODA", "NOT CONNECTED");
        }

    }
    public void onDestroy()
    {
        telephony.listen(null, PhoneStateListener.LISTEN_NONE);
    }
    class MyPhoneStateListener extends PhoneStateListener
    {

        public Boolean phoneRinging = false;
        private boolean restartMusic = false;

        public void onCallStateChanged(int state, String incomingNumber)
        {
            Intent intent = new Intent(con, MusicService.class);
            intent.putExtra("SERVICE", "RECONNECT");
            switch (state)
            {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (Debuger.DEBUG)
                        Log.d("DEBUG", "IDLE");

                    if (phoneRinging && restartMusic)
                    {
                        if (MusicService.media == null)
                        {
                            con.startService(intent);
                            //i might need that -> please test it
                            //new NotificationControls(RadioMainPageActivity.radioStationPosition, con);
                        }
                        else
                        {
                            con.stopService(intent);
                            con.startService(intent);
                            //i might need that -> please test it
                            //new NotificationControls(RadioMainPageActivity.radioStationPosition, con);
                        }
                        restartMusic = false;
                        phoneRinging = false;
                    }
                    phoneRinging = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (Debuger.DEBUG)
                        Log.d("DEBUG", "OFFHOOK");
                    //after answering phonecall
                    phoneRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if (Debuger.DEBUG)
                        Log.d("DEBUG", "RINGING");
                    //phone is ringing

                    if (isMyServiceRunning(MusicService.class))
                    {
                        con.stopService(intent);
                        restartMusic = true;
                        phoneRinging = true;
                    }
                    break;
            }
        }
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

}
