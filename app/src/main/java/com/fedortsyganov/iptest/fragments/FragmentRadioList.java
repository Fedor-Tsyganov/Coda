package com.fedortsyganov.iptest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fedortsyganov.iptest.LauncherActivity;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioNavigationDrawer;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.dialogs.*;
import com.fedortsyganov.iptest.helpers.*;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.helpers.RadioOBJComparator;
import com.fedortsyganov.iptest.swipemenulist.SwipeMenu;
import com.fedortsyganov.iptest.swipemenulist.SwipeMenuCreator;
import com.fedortsyganov.iptest.swipemenulist.SwipeMenuItem;
import com.fedortsyganov.iptest.swipemenulist.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.Instant;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

/**
 * Created by fedortsyganov on 3/9/15.
 */
public class FragmentRadioList extends Fragment
{
    public static ImageView headerImage;
    //private ListView listView;
    private SwipeMenuListView listView;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String STATION_PREFS = "ListPosition";
    private ArrayList<String> listStations = new ArrayList<>();
    private ArrayList<String> listCountries = new ArrayList<>();
    private int arrayColors[];
    public static FragmentRadioList fragmentRadioList;
    private static int resourceId;
    public static RadioListAdapter adapter;
    //public ImageView anim;
    public TextView textViewStation, textViewCountry;
    private static final String STATION_TO_SAVE = "info";
    private static final String PLAYLIST_BOOLEAN = "PlaylistBool";
    private static final String PLAYLIST_NUMBER = "PlaylistNum";
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    protected int mPosition = 0;
    private SwipeMenuItem openItem;
    private SwipeMenuCreator creator;

    public FragmentRadioList()
    {
    }

    public static FragmentRadioList newInstance(int sectionNumber, int resId)
    {
        FragmentRadioList fragment = new FragmentRadioList();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragmentRadioList = fragment;
        resourceId = resId;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_radio_list, container, false);
        preferences = getActivity().getSharedPreferences(STATION_TO_SAVE, getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        arrayColors = new int[RadioMainPageActivity.currentStationsList.size()];
        if (listStations.size() < 2)
            populateListView();
        headerImage = (ImageView) view.findViewById(R.id.header_image);
        headerImage.setImageResource(resourceId);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (!RadioMainPageActivity.tvActionBarInfo.getText().equals(getResources().getString(R.string.app_name)))
        {
            ActionBarAnimation.animationPlaylistOut(getResources().getString(R.string.app_name));
        }
        listView = (SwipeMenuListView) view.findViewById(R.id.listRadioStations);
        creator = new SwipeMenuCreator()
        {
            @Override
            public void create(SwipeMenu menu)
            {
                openItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.parseColor("#FF4081")));
                // set item width
                openItem.setWidth(dp2px(90));
                openItem.setPosition(mPosition);

                openItem.setIcon(R.drawable.icon_playlist_small);
                // set item title
                // openItem.setTitle("Add to Playlist");
                openItem.setTitle(getString(R.string.playlist));
                // set item title fontsize
                openItem.setTitleSize(10);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
            {
                //Toast.makeText(getActivity().getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
                showPlaylistSelectorDialog(position);
                //getActivity().startActivity(new Intent(getActivity().getApplicationContext(), RadioPlayerActivity.class));
                //RadioMainPage.genreTV.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (RadioNavigationDrawer.mPosition != 0)
                    RadioMainPageActivity.previousStationsList
                        = GenreListSelector.selectList(RadioNavigationDrawer.mPosition, getActivity().getApplicationContext());
                else
                    RadioMainPageActivity.previousStationsList
                            = GenreListSelector.selectList(preferences.getInt(STATION_PREFS, 0), getActivity().getApplicationContext());

                RadioMainPageActivity.radioStationPosition = position - 1;
                RadioMainPageActivity.radioStation = RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition);
                backupStation(RadioMainPageActivity.radioStation, RadioMainPageActivity.radioStationPosition);
                RadioPlayerActivity.changeStation = true;
                Activity activity = getActivity();
                Intent intent = new Intent(getActivity().getApplicationContext(), RadioPlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                RadioMainPageActivity.applyMargin = true;
                RadioPlayerActivity.changeList = true;
                activity.startActivity(intent);
            }
        });
        if (RadioMainPageActivity.applyMargin)
        {
            int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
            listView.setPadding(0,0,0, margin);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        adapter = new RadioListAdapter();

        StikkyHeaderBuilder
                .stickTo(listView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_height_header)
                .animator(new ParallaxStikkyAnimator())
                .build();

        listView.setAdapter(adapter);

    }

    private void populateListView()
    {
        int size = RadioMainPageActivity.currentStationsList.size();
        for (int i = 0; i < size; i++)
        {
            listStations.add(RadioMainPageActivity.currentStationsList.get(i).getStationName());
            listCountries.add(RadioMainPageActivity.currentStationsList.get(i).getStationCountry());
            arrayColors[i] = RadioMainPageActivity.currentStationsList.get(i).getColor();
            // arrayColors[i] = Color.parseColor(colors[randomWithRange(0, colors.length - 1)]);
            if (Debuger.DEBUG)
                Log.i("Station", RadioMainPageActivity.currentStationsList.get(i).getStationName());
        }
    }

    public class RadioListAdapter extends ArrayAdapter<String>
    {
        public RadioListAdapter()
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_radio_list, listStations);
        }
        public RadioListAdapter getAdapter()
        {
            return adapter;
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            int charTwo = 1;
            String station = listStations.get(position);
            //checking if station name has multiple words
            if (containsWhiteSpace(station))
            {
                //if it does, get position of the first char in the second word
                charTwo = TextUtils.indexOf(station, ' ') + 1;
            }

            View view = getActivity().getLayoutInflater().inflate(R.layout.custom_radio_list, parent, false);

            textViewStation = (TextView) view.findViewById(R.id.textViewStationName);
            textViewCountry = (TextView) view.findViewById(R.id.textViewStationCountry);
            textViewStation.setText(listStations.get(position));
            textViewStation.setTextColor(Color.BLACK);
            textViewCountry.setText(listCountries.get(position));
            textViewCountry.setTextColor(Color.GRAY);

            /* google music player - animation in listview
            anim = (ImageView) view.findViewById(R.id.ivAnimation);
            anim.setBackgroundResource(R.drawable.player_animation);
            final AnimationDrawable frameAnimation = (AnimationDrawable) anim.getBackground();

            view.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    anim.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (frameAnimation.isRunning())
                            {
                                frameAnimation.stop();
                                frameAnimation.selectDrawable(0);
                            } else
                            {
                                frameAnimation.start();
                            }
                        }
                    });
                    return false;
                }
            });
            */
            Character c = new Character(listStations.get(position).charAt(0));
            Character c2 = new Character(listStations.get(position).charAt(charTwo));
            String str = c.toString().toUpperCase() + c2.toString().toLowerCase();
            TextDrawable drawable = TextDrawable.builder().buildRound(str, arrayColors[position]);
            ImageView iv = (ImageView) view.findViewById(R.id.imageViewStationName);
            ImageView ivPlayPause = (ImageView) view.findViewById(R.id.imageViewPlayPause);
            iv.setImageDrawable(drawable);

            //if music is playing/paused changing Letters in Circular view to Play/Pause icon.

            //if (RadioMainPageActivity.isPlaying &&
            // sameAs(RadioMainPageActivity.radioStation, RadioMainPageActivity.currentStationsList.get(position)))
            //compares objects!
            if (RadioMainPageActivity.isPlaying &&
                    RadioMainPageActivity.radioStation.getStationName().equalsIgnoreCase(listStations.get(position)))
            //compares names, not objects!
            {
                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.icon_pause_notification));
                ivPlayPause.setVisibility(View.VISIBLE);
                drawable = TextDrawable.builder().buildRound(" ", arrayColors[position]);
                iv.setImageDrawable(drawable);
                textViewStation.setTextColor(getResources().getColor(R.color.pink_dark));
                textViewStation.setTypeface(null, Typeface.BOLD);
                textViewCountry.setTextColor(getResources().getColor(R.color.pink_light));
                textViewCountry.setTypeface(null, Typeface.BOLD);
                adapter.notifyDataSetChanged();
                if (Debuger.DEBUG)
                {
                    String listStn = "listStn:" + listStations.get(position);
                    String current = "current:" + RadioMainPageActivity.currentStationsList.get(position).getStationName();
                    String previous = " previous:" + RadioMainPageActivity.previousStationsList.get(position).getStationName();
                    String rdStn = " radioStation:" + RadioMainPageActivity.radioStation.getStationName();
                    Log.e("isPlaying", listStn + current + previous + rdStn);
                }
            }
            //else if(RadioMainPageActivity.isPaused &&
             //       sameAs(RadioMainPageActivity.radioStation, RadioMainPageActivity.currentStationsList.get(position)))
            //compares objects!
            else if(RadioMainPageActivity.isPaused &&
                    RadioMainPageActivity.radioStation.getStationName().equalsIgnoreCase(listStations.get(position)))
            //compares names, not objects!
            {
                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.icon_play_notification));
                ivPlayPause.setVisibility(View.VISIBLE);
                drawable = TextDrawable.builder().buildRound(" ", arrayColors[position]);
                iv.setImageDrawable(drawable);
                textViewStation.setTextColor(getResources().getColor(R.color.pink_dark));
                textViewStation.setTypeface(null, Typeface.BOLD);
                textViewCountry.setTextColor(getResources().getColor(R.color.pink_light));
                textViewCountry.setTypeface(null, Typeface.BOLD);
                adapter.notifyDataSetChanged();
                if (Debuger.DEBUG)
                {
                    String listStn = "listStn:" + listStations.get(position);
                    String current = " current:" + RadioMainPageActivity.currentStationsList.get(position).getStationName();
                    String previous = " previous:" + RadioMainPageActivity.previousStationsList.get(position).getStationName();
                    String rdStn = " radioStation:" + RadioMainPageActivity.radioStation.getStationName();
                    Log.e("isPaused", listStn + current + previous + rdStn);
                }
            }
            else
            {
                ivPlayPause.setVisibility(View.GONE);
            }

            return view;
        }
    }

    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator
    {

        @Override
        public AnimatorBuilder getAnimatorBuilder()
        {
            View mHeader_image = getHeader().findViewById(R.id.header_image);

            return AnimatorBuilder.create().applyVerticalParallax(mHeader_image);
        }
    }

    private int dp2px(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public static boolean containsWhiteSpace(final String testCode)
    {
        if (testCode != null)
        {
            int mSize = testCode.length();
            for (int i = 0; i < mSize; i++)
            {
                if (Character.isWhitespace(testCode.charAt(i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void backupStation(RadioStation station, int position)
    {
        RadioStation myStation = new RadioStation();
        myStation.seStationName(station.getStationName());
        myStation.setStationCountry(station.getStationCountry());
        myStation.setStationGenre(station.getStationGanre());
        myStation.setStationUrl(station.getStationUrl());
        myStation.setHeader(false);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<RadioStation>() {}.getType();
        String json = gson.toJson(myStation, type);
        String str = "RadioStation";
        String intNum = "RadioStationPosition";
        prefEditor.putString(str, json).commit();
        prefEditor.putFloat(intNum, (float)position).commit();
    }


    //compares two Radio Stations.
    public static boolean sameAs (RadioStation lhs, RadioStation rhs)
    {
        RadioOBJComparator comparator = new RadioOBJComparator();
        if (comparator.compare(lhs,rhs) == 0)
            return true;
        else
            return false;
    }

    //shows dialog to select playlist --> and used can add station to playlist if it exists.
    void showPlaylistSelectorDialog(int position)
    {
        int num = -1;
        if (preferences.getBoolean(PLAYLIST_BOOLEAN, false))
        {
            num = preferences.getInt(PLAYLIST_NUMBER, -1);
        }

        if (num >= 0)
        {
            DialogPlaylistAction dialogPlaylistAction
                    = new DialogPlaylistAction(getActivity(), position, RadioMainPageActivity.currentStationsList.get(position));
            dialogPlaylistAction.getWindow().getAttributes().gravity = Gravity.TOP;
            dialogPlaylistAction.show();
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.playlist_create), Toast.LENGTH_SHORT).show();
        }
    }

}
