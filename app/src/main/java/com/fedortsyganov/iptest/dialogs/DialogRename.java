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
import android.widget.TextView;

import com.fedortsyganov.iptest.LauncherActivity;
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
 * Created by fedortsyganov on 4/24/15.
 */
public class DialogRename extends Dialog
{
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private EditText editText;
    private Button btnCancel, btnRename;
    private TextView textView;
    private Activity activity;
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private static final String PLAYLIST_LIST = "PLAYLIST_FRAGMENT";
    public int number;
    private Playlist mPlaylist;

    public DialogRename(Activity a, Playlist playlist, int position)
    {
        super(a);
        activity = a;
        mPlaylist = playlist;
        this.number = position;
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
        textView = (TextView) findViewById(R.id.textViewCreatePlaylistDialog);
        textView.setText(activity.getString(R.string.dialog_rename_text));
        editText = (EditText) findViewById(R.id.editTextPlaylist);
        editText.setTypeface(typeface);
        editText.setText(mPlaylist.getPlaylistName());
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
        btnRename = (Button) findViewById(R.id.btnCreatePLRename);
        btnRename.setTypeface(typeface);
        btnRename.setText(activity.getString(R.string.dialog_rename));
        btnRename.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String playlist = editText.getText().toString().trim().replaceAll("\\s{2}", " ");
                if (playlist.length() == 0)
                {
                } else
                {
                    savePlaylist(playlist, mPlaylist, number);
                    FragmentTransaction tr = activity.getFragmentManager().beginTransaction();
                    tr.replace(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST);
                    tr.commit();
                    dismiss();
                }
            }
        });
    }

    private void savePlaylist(String name, Playlist playlist, int number)
    {
        Playlist mPlaylist = playlist;
        mPlaylist.setPlaylistName(name);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<Playlist>() {}.getType();
        String json = gson.toJson(mPlaylist, type);
        String str = "Playlist"+Integer.toString(number);
        prefEditor.putString(str, json).commit();
    }
}

