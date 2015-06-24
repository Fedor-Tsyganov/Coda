package com.fedortsyganov.iptest.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.fragments.FragmentPlaylist;
import com.fedortsyganov.iptest.helpers.PlaylistHelper;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.receivers.IDGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/29/15.
 */
public class DialogDeleteStation extends Dialog
{
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private Activity activity;
    private int position;
    public ArrayAdapter arrayAdapter;
    private int color;
    private Playlist playlist;
    private int playlistNumber;
    private ArrayList <RadioStation> stations;

    public DialogDeleteStation (Activity activity, int position, ArrayAdapter adapter, int color, ArrayList <RadioStation> stations, int playlistNum)
    {
        super(activity);
        this.activity = activity;
        this.position = position;
        arrayAdapter = adapter;
        this.color = color;
        this.stations = stations;
        playlistNumber = playlistNum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_station_action);

        preferences = activity.getSharedPreferences(STATION_TO_SAVE, activity.MODE_PRIVATE);
        prefEditor = preferences.edit();
        playlist = PlaylistHelper.getPlaylist(activity.getApplicationContext(), preferences, playlistNumber);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlSettingsButtons);
        String str = stations.get(position).getStationName().toUpperCase();
        TextDrawable drawable = TextDrawable.builder().buildRound(str.charAt(0)+"", color);
        ImageView plName = (ImageView) findViewById(R.id.ivStationName);
        //plName.setText(playlistName.toUpperCase());
        plName.setImageDrawable(drawable);
        plName.setRotation(-90);

        Button delete = (Button) relativeLayout.findViewById(R.id.btnDeleteStation);
        Button cancel = (Button) relativeLayout.findViewById(R.id.btnCancelStation);
        relativeLayout.setScaleY(0f);
        relativeLayout.setScaleX(0f);
        relativeLayout.setTranslationY(-300f);
        relativeLayout.setTranslationX(-300f);

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //playlist.remove(position);
                stations.remove((int) (position));
                deleteStation(playlistNumber, stations, playlist);
                if (stations.size() > 0)
                {
                    FragmentPlaylist.rlEmptyState.setVisibility(View.INVISIBLE);
                    FragmentPlaylist.listView.setVisibility(View.VISIBLE);
                }
                else
                {
                    FragmentPlaylist.rlEmptyState.setVisibility(View.VISIBLE);
                    FragmentPlaylist.listView.setVisibility(View.INVISIBLE);
                }
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        plName.animate().rotation(1).setDuration(400).start();
        relativeLayout.animate().scaleX(1f).translationX(0).setDuration(500).start();
        relativeLayout.animate().scaleY(1f).translationY(0).setDuration(500).start();
    }
    private void deleteStation(int number, ArrayList <RadioStation> stations, Playlist playlist)
    {
        Playlist mPlaylist = playlist;
        mPlaylist.setStations(stations);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<Playlist>() {}.getType();
        String json = gson.toJson(mPlaylist, type);
        prefEditor.putBoolean(PLAYLIST_BOOLEAN, true).commit();
        String str = "Playlist"+Integer.toString(number);
        prefEditor.putString(str, json).commit();
        arrayAdapter.notifyDataSetChanged();
    }


}
