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
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.fragments.FragmentPlaylistList;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.receivers.IDGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/23/15.
 */
public class DialogPlaylistSettingsAction extends Dialog
{
    private String playlistName;
    private ArrayList <Playlist> playlist;
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private Activity activity;
    private int position;
    public ArrayAdapter arrayAdapter;
    private int color;

    public DialogPlaylistSettingsAction(Activity a, String playlistName, ArrayList<Playlist> playlist, int position, ArrayAdapter adapter, int color)
    {
        super(a);
        activity = a;
        this.playlistName = playlistName;
        this.playlist = playlist;
        this.position = position;
        arrayAdapter = adapter;
        this.color = color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_playlist_settings_action);

        preferences = activity.getSharedPreferences(STATION_TO_SAVE, activity.MODE_PRIVATE);
        prefEditor = preferences.edit();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlSettingsBtns);
        String str = playlistName.toUpperCase();
        TextDrawable drawable = TextDrawable.builder().buildRound(str.charAt(0)+"", color);
        ImageView plName = (ImageView) findViewById(R.id.ivPlaylistName);
        //plName.setText(playlistName.toUpperCase());
        plName.setImageDrawable(drawable);
        plName.setRotation(-90);

        Button rename = (Button) relativeLayout.findViewById(R.id.btnRename);
        Button delete = (Button) relativeLayout.findViewById(R.id.btnDelete);
        Button cancel = (Button) relativeLayout.findViewById(R.id.btnCancel);
        relativeLayout.setScaleY(0f);
        relativeLayout.setScaleX(0f);
        relativeLayout.setTranslationY(-300f);
        relativeLayout.setTranslationX(-300f);
        rename.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showRenameDialog(activity, playlist.get(position), position);
                arrayAdapter.notifyDataSetChanged();
                dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playlist.remove(position);
                prefEditor.remove("Playlist" + Integer.toString(position)).commit();
                prefEditor.putInt(PLAYLIST_NUMBER, -1).commit();
                int playListSize = playlist.size();
                for (int size = 0; size < playListSize; size++)
                {
                    savePlaylist(playlist.get(size).getPlaylistName(), playlist.get(size).getStations(), playlist.get(size).getColor());
                }
                arrayAdapter.notifyDataSetChanged();
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

    private void showRenameDialog(Activity activity, Playlist playlist, int position)
    {
        DialogRename dialogRename = new DialogRename(activity, playlist, position);
        dialogRename.show();
    }


    private void savePlaylist(String name, ArrayList <RadioStation> stations, int color)
    {
        Playlist mPlaylist = new Playlist();
        mPlaylist.setPlaylistName(name);
        mPlaylist.setPlaylistID(IDGenerator.generateID());
        mPlaylist.setStations(stations);
        mPlaylist.setColor(color);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<Playlist>() {}.getType();
        String json = gson.toJson(mPlaylist, type);
        prefEditor.putBoolean(PLAYLIST_BOOLEAN, true).commit();
        int num = preferences.getInt(PLAYLIST_NUMBER, -1);
        num = num+1;
        prefEditor.putInt(PLAYLIST_NUMBER , num).commit();
        String str = "Playlist"+Integer.toString(num);
        prefEditor.putString(str, json).commit();
    }
}
