package com.fedortsyganov.iptest.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.fedortsyganov.iptest.dialogs.DialogPlaylistAction;
import com.fedortsyganov.iptest.dialogs.DialogPlaylistSettingsAction;
import com.fedortsyganov.iptest.helpers.ActionBarAnimation;
import com.fedortsyganov.iptest.helpers.Debuger;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.dialogs.DialogCreatePlaylist;
import com.fedortsyganov.iptest.receivers.IDGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/8/15.
 */
public class FragmentPlaylistList extends Fragment
{
    public static FragmentPlaylistList fragmentPlaylistList;
    public FragmentPlaylistList(){}
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String PLAYLIST = "PLAYLIST_FRAGMENT";
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    public static ArrayList <RadioStation> mStations;
    public RelativeLayout rlContainer, rlPlaylist;
    public ListView listView;
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    private static final String STATION_BOOLEAN = "StationtBool";
    private static final String STATION_NUMBER = "StationNum";
    public ArrayList <Playlist> mPlaylists;
    public static PlaylistArrayAdapter adapter;
    public static int playlistPosition;
    private int mPosition;

    public static FragmentPlaylistList newInstance(int positionNumber)
    {
        FragmentPlaylistList fragment = new FragmentPlaylistList();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, positionNumber);
        fragment.setArguments(args);
        fragmentPlaylistList = fragment;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ArrayList <String> values = new ArrayList<>();
        View view;
        view = inflater.inflate(R.layout.fragment_playlists_list, container, false);
        rlContainer = (RelativeLayout) view.findViewById(R.id.rlPlaylistContainer);
        rlPlaylist = (RelativeLayout) view.findViewById(R.id.rlPlayList);
        listView = (ListView) view.findViewById(R.id.listViewPlayList);
        preferences = getActivity().getSharedPreferences(STATION_TO_SAVE, getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        mStations = getStation();

        int numPlaylist = -1;

        if (preferences.getBoolean(PLAYLIST_BOOLEAN, false))
        {
            numPlaylist = preferences.getInt(PLAYLIST_NUMBER, -1);
        }
        if (numPlaylist >= 0)
        {
            for (int c = 0; c < numPlaylist+1; c++)
            {
                String str = "Playlist"+Integer.toString(c);
                if (preferences.contains(str))
                {
                    String json = preferences.getString(str, "");
                    if (Debuger.DEBUG)
                    {
                        Log.v("saved_station", "" + " : " + c);
                        Log.v("saved_station", "" + " : " + json);
                    }
                    values.add(json);
                }
            }
            rlContainer.setVisibility(View.GONE);
            rlPlaylist.setVisibility(View.VISIBLE);
            mPlaylists = getPlaylist();
            adapter = new PlaylistArrayAdapter(mPlaylists);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    //do something here
                    if (Debuger.DEBUG)
                        Log.v("saved_station", "" + " position: " + position);
                    playlistPosition = position;
                    mStations = new ArrayList<>(mPlaylists.get(position).getStations());
                    ActionBarAnimation.animationPlaylistIn(mPlaylists.get(position).getPlaylistName());
                    getFragmentManager().beginTransaction().replace(R.id.container, FragmentPlaylist.newInstance(0), PLAYLIST).commit();
                }
            });

            //Long Click -> deletes playlist. Not sure that i need this functionality
            /*
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    mPlaylists.remove(position);
                    prefEditor.remove("Playlist" + Integer.toString(position)).commit();
                    prefEditor.putInt(PLAYLIST_NUMBER, -1).commit();
                    int playListSize = mPlaylists.size();
                    for (int size = 0; size < playListSize; size++)
                    {
                        savePlaylist(mPlaylists.get(size).getPlaylistName(), mPlaylists.get(size).getStations());
                    }
                    rlPlaylist.postInvalidate();
                    rlContainer.postInvalidate();
                    adapter.notifyDataSetChanged();
                    if (mPlaylists.size() == 0)
                    {
                        rlPlaylist.setVisibility(View.GONE);
                        rlContainer.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
            */

        }
        else
        {
            //rlPlaylist.setVisibility(View.VISIBLE);
           // ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, values);
            //listView.setAdapter(adapter);
        }
        if (Debuger.DEBUG)
        {
            for (int c = 0; c < mStations.size()-1; c++)
                Log.v("saved_station", "" +" : " + mStations.get(c).getStationCountry() + " : " + mStations.get(c).getStationName());
        }

        RadioMainPageActivity.bSearch.setVisibility(View.GONE);
        RadioMainPageActivity.bAddPlaylist.setVisibility(View.VISIBLE);
        RadioMainPageActivity.bAddPlaylist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();
            }
        });
        return view;
    }

    private ArrayList <Playlist> getPlaylist()
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
            if (Debuger.DEBUG)
                Log.v("saved_station", "else" + " : " + num);
        }
        return playlists;
    }


    private ArrayList<RadioStation> getStation()
    {
        ArrayList <RadioStation> stations = new ArrayList<>();
        int num = 0;
        Gson gson = new GsonBuilder().create();

        if (preferences.getBoolean(STATION_BOOLEAN, false))
        {
            num = preferences.getInt(STATION_NUMBER, 0);
        }
        if (num >= 0)
        {
            for (int i = 1; i < num; i++)
            {
                String str = "Station"+Integer.toString(i);
                String json = preferences.getString(str, "");
                Type type = new TypeToken<RadioStation>() {}.getType();
                RadioStation station = (RadioStation) gson.fromJson(json, type);
                stations.add(station);
                if (Debuger.DEBUG)
                {
                    Log.v("saved_station", "" + "counter : " + i);
                    Log.v("saved_station", "" + "json : " + json);
                    Log.v("saved_station", "" + "station country: " + station.getStationCountry() + "-station name: " + station.getStationName());
                }
            }
        }
        else
        {
            String json = preferences.getString("Station"+Integer.toString(0), "");
            RadioStation station = gson.fromJson(json, RadioStation.class);
            stations.add(station);
            if (Debuger.DEBUG)
            {
                Log.v("saved_station", "" + " : " + station.getStationCountry() + " : " + station.getStationName());
                Log.v("saved_station", "" + " : " + num);
            }
        }
        return stations;
    }

    void showDialog()
    {
        DialogCreatePlaylist dialogCreatePlaylist = new DialogCreatePlaylist(getActivity());
        dialogCreatePlaylist.show();
    }

    class PlaylistArrayAdapter extends ArrayAdapter <Playlist>
    {
        ArrayList<Playlist> mList;

        public PlaylistArrayAdapter(ArrayList<Playlist> list)
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_playlists_list, list);
            mList = list;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            Typeface typefaceTextView = LauncherActivity.typefaceRobotoRegular;
            View view = getActivity().getLayoutInflater().inflate(R.layout.custom_radio_list, parent, false);
            TextView textViewPlaylistItem = (TextView) view.findViewById(R.id.textViewStationName);
            textViewPlaylistItem.setText(mList.get(position).getPlaylistName());
            textViewPlaylistItem.setTypeface(typefaceTextView);
            int size = mList.get(position).getStations().size();
            mPosition = position;
            TextView textViewNumOfStation = (TextView) view.findViewById(R.id.textViewStationCountry);
            if (size > 1 || size == 0)
                textViewNumOfStation.setText(size+" "+getString(R.string.playlist_station_counter_plural));
            else
                textViewNumOfStation.setText(size+" "+getString(R.string.playlist_station_counter_singular));

            ImageView iv = (ImageView) view.findViewById(R.id.imageViewStationName);

            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutSettingBtn);
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showActionDialog(mList.get(mPosition).getPlaylistName(), mList, mPosition, adapter, mList.get(position).getColor());
                }
            });

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

    private void savePlaylist(String name, ArrayList <RadioStation> stations)
    {
        Playlist mPlaylist = new Playlist();
        mPlaylist.setPlaylistName(name);
        mPlaylist.setPlaylistID(IDGenerator.generateID());
        mPlaylist.setStations(stations);
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

    private void showActionDialog(String name, ArrayList <Playlist> playlist, int position, PlaylistArrayAdapter mAdapter, int color)
    {
        DialogPlaylistSettingsAction dialog =
                new DialogPlaylistSettingsAction(getActivity(), name, playlist, position, mAdapter, color );
        dialog.show();
    }

}
