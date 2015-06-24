package com.fedortsyganov.iptest.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.fedortsyganov.iptest.fragments.FragmentRadioList;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/11/15.
 */
public class PlaylistHelper
{
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private static final String STATION_BOOLEAN = "StationtBool";
    private static final String STATION_NUMBER = "StationNum";
    private static final String STATION_TO_SAVE = "info";


    public static ArrayList<Playlist> getPlaylists(Context context, SharedPreferences prefs,  SharedPreferences.Editor prefEditor)
    {
        prefs = context.getSharedPreferences(STATION_TO_SAVE, context.MODE_PRIVATE);
        ArrayList <Playlist> playlists = new ArrayList<>();
        int num = -1;
        Gson gson = new GsonBuilder().create();

        if (prefs.getBoolean(PLAYLIST_BOOLEAN, false))
        {
            num = prefs.getInt(PLAYLIST_NUMBER, -1);
        }
        if (num >= 0)
        {
            for (int i = 0; i < num+1; i++)
            {
                String str = "Playlist"+Integer.toString(i);
                String json = prefs.getString(str, "");
                Type type = new TypeToken<Playlist>() {}.getType();
                Playlist playlist = (Playlist) gson.fromJson(json, type);
                playlists.add(playlist);
            }
        }
        else
        {
            String json = prefs.getString("Playlist"+Integer.toString(0), "");
            Playlist playlist = (Playlist) gson.fromJson(json, Playlist.class);
            playlists.add(playlist);
        }
        return playlists;
    }

    public static Playlist getPlaylist (Context context, SharedPreferences prefs, int playlistPosition)
    {
        prefs = context.getSharedPreferences(STATION_TO_SAVE, context.MODE_PRIVATE);
        Playlist playlist;
        Gson gson = new GsonBuilder().create();
        String str = "Playlist"+Integer.toString(playlistPosition);
        String json = prefs.getString(str, "");
        Type type = new TypeToken<Playlist>() {}.getType();
        playlist = (Playlist) gson.fromJson(json, type);
        return playlist;
    }
}
