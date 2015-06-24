package com.fedortsyganov.iptest.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.dialogs.DialogDeleteStation;
import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/8/15.
 */
public class FragmentPlaylist extends Fragment
{
    public static FragmentPlaylist fragmentPlaylist;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList <RadioStation> listStations;
    public static ListView listView;
    private RadioListAdapter adapter;
    public static RelativeLayout rlEmptyState;
    private int color;
    private ArrayList<Integer> colors;

    VideoView videoView;
    String uriPath = "android.resource://com.fedortsyganov.iptest/"+R.raw.scrnd;
    Uri uri = Uri.parse(uriPath);

    public FragmentPlaylist(){}

    public static FragmentPlaylist newInstance(int positionNumber)
    {
        FragmentPlaylist fragment = new FragmentPlaylist();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, positionNumber);
        fragment.setArguments(args);
        fragmentPlaylist = fragment;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RadioMainPageActivity.bSearch.setVisibility(View.GONE);
        RadioMainPageActivity.bAddPlaylist.setVisibility(View.GONE);

        colors = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        videoView = (VideoView) view.findViewById(R.id.videoViewTutorial);

        if (FragmentPlaylistList.mStations != null)
            listStations = new ArrayList<>(FragmentPlaylistList.mStations);
        else
            listStations = new ArrayList<>();
        rlEmptyState = (RelativeLayout) view.findViewById(R.id.rlPLContainer);
        listView = (ListView) view.findViewById(R.id.listViewCurrentPlaylist);
        if (listStations.size() > 0)
        {
            rlEmptyState.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
        }
        else
        {
            rlEmptyState.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mp.setLooping(true);
                }
            });
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
        }
        adapter = new RadioListAdapter();
        listView.setAdapter(adapter);
        //RadioMainPageActivity.currentStationsList = new ArrayList<>(listStations);
        //RadioMainPageActivity.previousStationsList = new ArrayList<>(RadioMainPageActivity.currentStationsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                RadioMainPageActivity.currentStationsList = new ArrayList<>(listStations);
                RadioMainPageActivity.previousStationsList = new ArrayList<>(RadioMainPageActivity.currentStationsList);
                RadioMainPageActivity.radioStationPosition = position;
                RadioMainPageActivity.radioStation = RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition);
                RadioPlayerActivity.changeStation = true;
                RadioPlayerActivity.changeList = true;
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(), RadioPlayerActivity.class));
            }
        });


        return view;
    }

    public class RadioListAdapter extends ArrayAdapter<RadioStation>
    {
        public RadioListAdapter()
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_playlist, listStations);
        }
        @Override
        public long getItemId(int position)
        {
            return super.getItemId(position);
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public int getItemViewType(int position)
        {
            return position;
            //return super.getItemViewType(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            int charTwo = 1;
            String station = listStations.get(position).getStationName();
            //checking if station name has multiple words
            if (containsWhiteSpace(station))
            {
                //if it does, get position of the first char in the second word
                charTwo = TextUtils.indexOf(station, ' ') + 1;
            }

            View view = getActivity().getLayoutInflater().inflate(R.layout.custom_radio_list, parent, false);

            TextView textViewStation = (TextView) view.findViewById(R.id.textViewStationName);
            TextView textViewCountry = (TextView) view.findViewById(R.id.textViewStationCountry);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutSettingBtn);
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showDeleteDialog(listStations, position, FragmentPlaylistList.playlistPosition, colors.get(position));
                }
            });
            textViewStation.setText(listStations.get(position).getStationName());
            textViewStation.setTextColor(Color.BLACK);
            textViewCountry.setText(listStations.get(position).getStationCountry());
            textViewCountry.setTextColor(Color.GRAY);

            Character c = new Character(listStations.get(position).getStationName().charAt(0));
            Character c2 = new Character(listStations.get(position).getStationName().charAt(charTwo));
            String string = c.toString().toUpperCase() + c2.toString().toLowerCase();
            int counter = 0;
            int size = RadioMainPageActivity.allStations.size();
            for (int i = 0; i < size; i++)
            {
                if (FragmentRadioList.sameAs(RadioMainPageActivity.allStations.get(i), listStations.get(position)))
                {
                    counter = i;
                    break;
                }
            }
            color = RadioMainPageActivity.allStations.get(counter).getColor();
            colors.add(color);
            TextDrawable drawable = TextDrawable.builder().buildRect(string, color);
            ImageView iv = (ImageView) view.findViewById(R.id.imageViewStationName); //imageViewSettings
            iv.setImageDrawable(drawable);

            return view;
        }
    }
    public static boolean containsWhiteSpace(final String testCode)
    {
        if (testCode != null)
        {
            int size = testCode.length();
            for (int i = 0; i < size; i++)
            {
                if (Character.isWhitespace(testCode.charAt(i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    void showDeleteDialog(ArrayList <RadioStation> stations, int position, int plPosition, int color)
    {
        DialogDeleteStation deleteStation
                = new DialogDeleteStation(
                getActivity(),
                position,
                adapter,
                color,
                stations,
                plPosition
        );
        deleteStation.show();
    }

    @Override
    public void onDestroyView()
    {
        videoView.pause();
        videoView.suspend();
        videoView.setBackgroundColor(getResources().getColor(R.color.white_darker));
        videoView.clearAnimation();
        videoView.clearFocus();
        videoView.destroyDrawingCache();
        super.onDestroyView();
        //videoView.destroyDrawingCache();
    }

    @Override
    public void onPause()
    {
        videoView.pause();
        videoView.suspend();
        videoView.setBackgroundColor(getResources().getColor(R.color.white_darker));
        videoView.clearAnimation();
        videoView.clearFocus();
        videoView.destroyDrawingCache();
        super.onPause();
    }

}
