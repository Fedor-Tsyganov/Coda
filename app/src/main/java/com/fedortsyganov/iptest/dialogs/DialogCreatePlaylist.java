package com.fedortsyganov.iptest.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.fedortsyganov.iptest.LauncherActivity;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.fragments.FragmentPlaylistList;
import com.fedortsyganov.iptest.helpers.ColorGenerator;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.receivers.IDGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/9/15.
 */
public class DialogCreatePlaylist extends Dialog
{
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private EditText editText;
    private Activity activity;
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private static final String PLAYLIST_LIST = "PLAYLIST_FRAGMENT";
    private Button btnCancel, btnCreate;

    public DialogCreatePlaylist(Activity a)
    {
        super(a);
        activity = a;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_create_playlist);
        Typeface typeface = LauncherActivity.typefaceRobotoRegular;
        preferences = activity.getSharedPreferences(STATION_TO_SAVE, activity.MODE_PRIVATE);
        prefEditor = preferences.edit();
        editText = (EditText) findViewById(R.id.editTextPlaylist);
        editText.setTypeface(typeface);
        btnCancel = (Button) findViewById(R.id.btnCreatePLCancel);
        btnCancel.setTypeface(typeface);
        btnCancel.setText(activity.getString(R.string.dialog_cancel));
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        btnCreate = (Button) findViewById(R.id.btnCreatePLRename);
        btnCreate.setTypeface(typeface);
        btnCreate.setText(activity.getString(R.string.dialog_create_playlist_create));
        btnCreate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String playlist = editText.getText().toString().trim().replaceAll("\\s{2}", " ");
                if (playlist.length() == 0)
                {
                }
                else
                {
                    savePlaylist(playlist);
                    FragmentTransaction tr = activity.getFragmentManager().beginTransaction();
                    tr.replace(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST);
                    tr.commit();
                    dismiss();
                }
            }
        });
    }


    private void savePlaylist(String name)
    {
        Playlist mPlaylist = new Playlist();
        mPlaylist.setPlaylistName(name);
        mPlaylist.setPlaylistID(IDGenerator.generateID());
        mPlaylist.setStations(new ArrayList<RadioStation>());
        mPlaylist.setColor(ColorGenerator.generateColor());
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

