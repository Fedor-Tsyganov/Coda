package com.fedortsyganov.iptest;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.*;
import android.util.Log;
import android.view.View;

import com.fedortsyganov.iptest.amr.AudiostreamMetadataManager;
import com.fedortsyganov.iptest.amr.OnNewMetadataListener;
import com.fedortsyganov.iptest.amr.UserAgent;
import com.fedortsyganov.iptest.fragments.FragmentRadioList;
import com.fedortsyganov.iptest.helpers.Debuger;
import com.fedortsyganov.iptest.receivers.RadioAlarmReceiver;
import com.fedortsyganov.iptest.receivers.RemoteControlReceiver;
import com.fedortsyganov.iptest.remotehelpers.*;

import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener
{
    private static final String SERVICE = "SERVICE";
    private static final String SERVICE_START = "START";
    private static final String SERVICE_STOP = "STOP_ALARM";
    private static final long PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
    public static MediaPlayer media;
    public static boolean ALARM_PLAYED = false;
    public static Intent wakefulIntent;
    //private static MusicService instance = null;
    public static boolean serviceON = false;
    public static ComponentName mRemoteControlResponder;
    public static AudioManager myAudioManager;
    public static RemoteControlClient remoteControlClient;
    public String track, album, artist;
    public long duration = Long.valueOf(600), trackNumber;
    public int position, listSize, sessionId;
    private String URL;

    public static RemoteControlClientCompat remoteControlClientCompat;
    //private MusicMetadataSet src_set;
    //private MediaSession mediaSession;
    public static MediaSessionCompat sessionCompat = null;
    //private MediaSessionManager sessionManager;
    private MediaControllerCompat controllerCompat;
    //public static boolean isInstanceCreated() {return  instance != null;}

    @Override
    public void onCreate()
    {
        super.onCreate();
        //sessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        //src_set = new MyID3();
        //instance = this;
        try
        {
            URL = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationUrl();
            media = new MediaPlayer();
            media.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            media.setAudioStreamType(AudioManager.STREAM_MUSIC);
           // media.setDataSource(Information.stationUrl[RadioMainPage.position]);
            media.setDataSource(URL);
            media.setOnPreparedListener(this);

            //media.
            media.prepareAsync();
            RadioMainPageActivity.isPlaying = true;
            RadioMainPageActivity.isPaused = false;
            position = RadioMainPageActivity.radioStationPosition;
            listSize = RadioMainPageActivity.previousStationsList.size();
            album = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationCountry();
            artist = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationGanre();
            track = RadioMainPageActivity.previousStationsList.get(RadioMainPageActivity.radioStationPosition).getStationName();
            sessionId = media.getAudioSessionId();
            trackNumber = RadioMainPageActivity.radioStationPosition;
            //listen(URL);

        } catch (Exception e)
        {
            //Getting Exception
        }
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (sessionCompat != null && sessionCompat.isActive())
        {
            //do something
        }
        else
        {
            mRemoteControlResponder = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
            registerRemoteClient();
            //updateMetadata();

            setSession(mRemoteControlResponder, getApplication().getBaseContext());

            //sendAndroidMetadata(sessionId, track, artist, album, position, duration, listSize);
            //sessionMetadata();
        }
        //registerRemoteClient();
        //MediaButtonHelper.registerMediaButtonEventReceiverCompat(myAudioManager, mRemoteControlResponder);
        //registerRemoteClientCompat();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //sessionMetadata();
        if (Debuger.DEBUG)
            Log.v("CODA - SERVICE", "STARTED");
        serviceON = true;
        // updated if statement with
        //      intent != null
        if (intent != null && intent.getStringExtra(SERVICE) != null)
        {
            if (Debuger.DEBUG)
                Log.v("ALARM", intent.getStringExtra(SERVICE));
            if (intent.getStringExtra(SERVICE).equalsIgnoreCase(SERVICE_STOP))
            {
                ALARM_PLAYED = true;
                wakefulIntent = intent;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        stopForeground(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent)
    {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy()
    {
        myAudioManager.abandonAudioFocus(this);
        serviceON = false;
        //instance = null;
        if (Debuger.DEBUG)
            Log.v("CODA - SERVICE", " onDestroy() - INSIDE SERVICE");
        if (ALARM_PLAYED)
        {
            if (Debuger.DEBUG)
                Log.v("ALARM", " onDestroy() - ALARM DESTROYED");
            //stopping service if it's already played.
            stopService(wakefulIntent);
            RadioAlarmReceiver.completeWakefulIntent(wakefulIntent);
        }
        if (MusicService.media != null)
        {
            MusicService.media.stop();
            MusicService.media.reset();
            MusicService.media.release();
            MusicService.media = null;
            RadioMainPageActivity.isPlaying = false;
            RadioMainPageActivity.isPaused = true;
        }
        sendAndroidMetadata("com.android.music.playstatechanged", false, sessionId, track, artist, album, position, duration, listSize);
        //stopListening();
        //stopService(new Intent(getBaseContext(), MusicService.class));
        clearMetadata();
        stopForeground(false);
        super.onDestroy();
    }
    public int setTransportControlFlags()
    {
        int flags =  RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
        return flags;
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        //sendAndroidMetadata();
        //myAudioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        //remoteControlClientCompat.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        //remoteControlClientCompat.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
          //      RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
            //    RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
              //  RemoteControlClient.FLAG_KEY_MEDIA_STOP);

        //updateMetadaCompat();

        myAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
        //listen(URL);
        mp.start();
        if (RadioPlayerActivity.progressBar != null)
            RadioPlayerActivity.progressBar.setVisibility(View.GONE);
        RadioMainPageActivity.isPlaying = true;
        RadioMainPageActivity.isPaused = false;
        if (FragmentRadioList.fragmentRadioList.isVisible())
            FragmentRadioList.adapter.notifyDataSetChanged();
        updateMetadata();
        sessionMetadata();
        sendAndroidMetadata("com.android.music.metachanged", true, sessionId, track, artist, album, position, duration, listSize);
    }

    private void setSession(ComponentName componentName, Context context)
    {
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(Intent.ACTION_MEDIA_BUTTON), 0);
        if (context != null && mediaPendingIntent != null & componentName != null)
        {
            if (media != null)
            {
                sessionCompat = new MediaSessionCompat(context, "session"+media.getAudioSessionId(), componentName, mediaPendingIntent);
            }
            else
            {
                sessionCompat = new MediaSessionCompat(context, "session001", componentName, mediaPendingIntent);
            }
        }
        if (sessionCompat != null)
            controllerCompat = new MediaControllerCompat(context, sessionCompat);

        if (sessionCompat != null)
        {
            PlaybackStateCompat.Builder bob = new PlaybackStateCompat.Builder();
            bob.setActions(PLAYBACK_ACTIONS);
            PlaybackStateCompat pbState = bob.build();
            sessionCompat.setPlaybackState(pbState);
            sessionCompat.setActive(true);
        }
    }

    public static void unSetSession()
    {
        if (sessionCompat != null)
        {
            sessionCompat.setActive(false);
            sessionCompat.release();
            sessionCompat = null;
        }
    }

    private void sessionMetadata()
    {
        MediaMetadataCompat.Builder bob = new MediaMetadataCompat.Builder();
        //bob.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track);
        //bob.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, album);
        //bob.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, artist);
        //bob.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "session" + sessionId);
        bob.putString(MediaMetadata.METADATA_KEY_TITLE, track)
        .putString(MediaMetadata.METADATA_KEY_GENRE, artist).putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
        .putLong(MediaMetadata.METADATA_KEY_DURATION, duration)
        .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, trackNumber).putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, listSize);

        /*
        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
        builder.setTitle(track);
        builder.setDescription(album);
        builder.setSubtitle(artist);
        builder.build();
        */
        MediaMetadataCompat compat = bob.build();
        //Parcel parcel = Parcel.obtain();
        myAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        //compat.writeToParcel(parcel, 0);
        sessionCompat.setMetadata(compat);
    }

    @SuppressLint("NewApi")
    public void registerRemoteClientCompat()
    {
        //mRemoteControlResponder = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        MediaButtonHelper.registerMediaButtonEventReceiverCompat(myAudioManager, mRemoteControlResponder);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(Intent.ACTION_MEDIA_BUTTON), 0);
        remoteControlClientCompat = new RemoteControlClientCompat(mediaPendingIntent);
        RemoteControlHelper.registerRemoteControlClient(myAudioManager, remoteControlClientCompat);
        remoteControlClientCompat.setTransportControlFlags(setTransportControlFlags());

    }
    @SuppressLint("NewApi")
    public void registerRemoteClient()
    {
        //mRemoteControlResponder = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        try
        {
            //MediaButtonHelper.registerMediaButtonEventReceiverCompat(myAudioManager, mRemoteControlResponder);
            //myAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
            PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(Intent.ACTION_MEDIA_BUTTON), 0);
            remoteControlClient = new RemoteControlClient(mediaPendingIntent);
            myAudioManager.registerRemoteControlClient(remoteControlClient);
            remoteControlClient.setTransportControlFlags(setTransportControlFlags());
        }
        catch (Exception exception) {}
    }

    @SuppressLint("NewApi")
    private void updateMetadaCompat()
    {
        remoteControlClientCompat.editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, track)
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, duration)
                .apply();
        myAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
    @SuppressLint("NewApi")
    private void updateMetadata()
    {
        if (remoteControlClient == null)
            return;
        RemoteControlClient.MetadataEditor editor = remoteControlClient.editMetadata(true);
        editor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, track)
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, duration)
                .apply();
        myAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
    private void clearMetadata()
    {
        RemoteControlClient.MetadataEditor editor = remoteControlClient.editMetadata(true);
        editor.clear();
    }

    private void sendAndroidMetadata(String action, boolean status, int id, String track, String artist, String album, int position, long duration, int listSize)
    {
        Intent avrcp = new Intent(action);
        avrcp.putExtra("id", id);
        avrcp.putExtra("track", track);
        avrcp.putExtra("artist", artist);
        avrcp.putExtra("album", album);
        avrcp.putExtra("playing", status);
        avrcp.putExtra("ListSize", listSize);
        avrcp.putExtra("duration", duration);
        avrcp.putExtra("position", position);
        sendStickyBroadcast(avrcp);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {}

    private void listen(String url)
    {
        Uri uri = Uri.parse(url);

    //implement callbacks
        OnNewMetadataListener listener = new OnNewMetadataListener()
        {
            @Override
            public void onNewHeaders(String stringUri, List<String> name, List<String> desc,
                                     List<String> br, List<String> genre, List<String> info)
            {
                StringBuilder sName = new StringBuilder();
                for (String item: info)
                {
                    sName.append(item).append(" ");
                }
                Log.v("musicListener","sName:"+sName.toString());

            }

            @Override
            public void onNewStreamTitle(String stringUri, String streamTitle)
            {
                Log.v("musicListener",String.format("Uri: %1$s # streamTitle: %2$s", stringUri, streamTitle));
            }
        };

    //Start parsing
        AudiostreamMetadataManager.getInstance()
                .setUri(uri)
                .setOnNewMetadataListener(listener)
                .setUserAgent(UserAgent.WINDOWS_MEDIA_PLAYER)
                .start();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Log.i("onTaskRemoved", "App is no longer working");
        super.onTaskRemoved(rootIntent);
    }

    private void stopListening()
    {
        AudiostreamMetadataManager.getInstance().stop();
    }
}
