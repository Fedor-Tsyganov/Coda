package com.fedortsyganov.iptest.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fedortsyganov.iptest.LauncherActivity;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.receivers.IDGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/11/15.
 */
public class DialogPlaylistSelector extends DialogFragment
{
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private int mPosition = 0;
    private ArrayList <Playlist> playlists;
    private ArrayList <Playlist> tempPlaylists;
    private ArrayList <Integer> playlistNums;
    private ArrayList <Integer> positionInPl;
    public boolean actionRemove = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        preferences = getActivity().getSharedPreferences(STATION_TO_SAVE, getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();

        playlists = new ArrayList<>(getPlaylist());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_playlist_selector, null);
        ListView listview = (ListView) dialogView.findViewById(R.id.listViewPlayListSelector);
        if (actionRemove)
        {
            tempPlaylists = new ArrayList<>();
            int size = playlistNums.size();
            for (int c = 0; c < size; c++)
            {
                tempPlaylists.add(playlists.get(playlistNums.get(c)));
            }
            listview.setAdapter(new DialogArrayAdapter(tempPlaylists));
        }
        else
        {
            listview.setAdapter(new DialogArrayAdapter(playlists));
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (actionRemove)
                {
                    ArrayList <RadioStation> mArray1 = new ArrayList <> (tempPlaylists.get(position).getStations());
                    mArray1.remove((int)(positionInPl.get(position)));
                    saveToPlaylist(tempPlaylists.get(position).getPlaylistName(), playlistNums.get(position), mArray1, tempPlaylists.get(position).getColor());
                    dismiss();
                }
                else
                {
                    ArrayList<RadioStation> mArray = new ArrayList<>(playlists.get(position).getStations());
                    mArray.add(RadioMainPageActivity.currentStationsList.get(getPosition()));
                    saveToPlaylist(playlists.get(position).getPlaylistName(), position, mArray, playlists.get(position).getColor());
                    dismiss();
                }
            }
        });
        builder.setView(dialogView)
        .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dismiss();
            }
        });

        final Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int style = android.support.v4.app.DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }
    public void setAction(boolean remove)
    {
        actionRemove = remove;
    }
    public void setRemoveLists(ArrayList <Integer> playlistNums, ArrayList <Integer> positionInPl)
    {
        this.playlistNums = new ArrayList<>(playlistNums);
        this.positionInPl = new ArrayList<>(positionInPl);
    }
    public int setPostion(int position)
    {
        return mPosition = position;
    }
    private int getPosition()
    {
        return mPosition;
    }

    private ArrayList<Playlist> getPlaylist()
    {
        ArrayList <Playlist> playlists = new ArrayList<>();
        int num = -1;
        Gson gson = new GsonBuilder().create();

        if (preferences.getBoolean(PLAYLIST_BOOLEAN, false))
        {
            num = preferences.getInt(PLAYLIST_NUMBER, -1);
        }
        if (num >= 0)
        {
            for (int i = 0; i < num+1; i++)
            {
                String str = "Playlist"+Integer.toString(i);
                String json = preferences.getString(str, "");
                Type type = new TypeToken<Playlist>() {}.getType();
                Playlist playlist = (Playlist) gson.fromJson(json, type);
                playlists.add(playlist);
            }
        }
        else
        {
            String json = preferences.getString("Playlist"+Integer.toString(0), "");
            Playlist playlist = (Playlist) gson.fromJson(json, Playlist.class);
            playlists.add(playlist);
        }
        return playlists;
    }

    class DialogArrayAdapter extends ArrayAdapter <Playlist>
    {
        ArrayList<Playlist> mList;

        public DialogArrayAdapter(ArrayList<Playlist> list)
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_playlists_list, list);
            mList = list;
        }
        int randomWithRange(int min, int max)
        {
            int range = (max - min) + 1;
            return (int) (Math.random() * range) + min;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Typeface typefaceTextView = LauncherActivity.typefaceRobotoRegular;
            View view = getActivity().getLayoutInflater().inflate(R.layout.custom_playlist_item, parent, false);
            TextView textViewPlaylistItem = (TextView) view.findViewById(R.id.textViewPlaylistName);
            textViewPlaylistItem.setText(mList.get(position).getPlaylistName());
            textViewPlaylistItem.setTypeface(typefaceTextView);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutSettingsBtn);
            relativeLayout.setVisibility(View.INVISIBLE);

            ImageView iv = (ImageView) view.findViewById(R.id.imageViewPlaylistName);

            Character c = new Character(mList.get(position).getPlaylistName().charAt(0));
            Character c2 = new Character(' ');
            if (mList.get(position).getPlaylistName().length() > 1)
                c2 = new Character(mList.get(position).getPlaylistName().charAt(1));
            String str = c.toString().toUpperCase() + c2.toString().toLowerCase();
            TextDrawable drawable = TextDrawable.builder().buildRect(str, mList.get(position).getColor());
            iv.setImageDrawable(drawable);
            return view;
        }
    }

    private void saveToPlaylist(String name, int position, ArrayList <RadioStation> array, int color)
    {
        Playlist mPlaylist = new Playlist();
        mPlaylist.setPlaylistName(name);
        mPlaylist.setPlaylistID(IDGenerator.generateID());
        mPlaylist.setStations(array);
        mPlaylist.setColor(color);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<Playlist>() {}.getType();
        String json = gson.toJson(mPlaylist, type);
        prefEditor.putBoolean(PLAYLIST_BOOLEAN, true).commit();
        String str = "Playlist"+Integer.toString(position);
        prefEditor.putString(str, json).commit();
    }
}
