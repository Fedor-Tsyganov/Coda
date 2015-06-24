package com.fedortsyganov.iptest;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedortsyganov.iptest.fragments.*;
import com.fedortsyganov.iptest.helpers.ActionBarAnimation;
import com.fedortsyganov.iptest.helpers.GenreListSelector;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 1/5/15.
 */
public class RadioNavigationDrawer extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener
{
    //shared preferences file string
    private static final String STATION_TO_SAVE = "info";
    private boolean openDrawerOnFirstLaunch = false;
    private ArrayList<String> drawerList = new ArrayList<>();
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    public static DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    public static View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private static final String SEARCH = "SEARCH_FRAGMENT";
    private static final String RADIO_LIST = "RADIO_FRAGMENT";
    private static final String PLAYLIST_LIST = "PLAYLIST_LIST_FRAGMENT";
    private static final String PLAYLIST = "PLAYLIST_FRAGMENT";
    private static final String SETTINGS = "SETTINGS_FRAGMENT";
    private static final String STATION_PREFS = "ListPosition";
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private Typeface typefaceThin, typefaceRegular;
    //private Typeface typefaceRegular, typefaceThin;
    private Button bSettings;
    public static int mPosition = 0;

    public static int [] background = new int []
            {
                    R.drawable.dance_back, R.drawable.all_stations_back, R.drawable.lounge_back_two,
                    R.drawable.rock_back, R.drawable.jazz_back, R.drawable.top_fourty_back,
                    R.drawable.classical_back, R.drawable.hiphop_back, R.drawable.alternative_back,
                    R.drawable.oldies_back, R.drawable.adult_back, R.drawable.country_back,
                    R.drawable.college_back
            };

    public RadioNavigationDrawer()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        typefaceThin = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Thin.ttf");
        typefaceRegular = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Roboto-Regular.ttf");
        preferences = getActivity().getSharedPreferences(STATION_TO_SAVE, getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();

        if (savedInstanceState != null)
        {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
        else
        {
            mCurrentSelectedPosition = preferences.getInt(STATION_PREFS, 0);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        populateDrawerList();
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        bSettings = (Button) view.findViewById(R.id.bSettings);
        bSettings.setOnClickListener(this);
        bSettings.setTypeface(LauncherActivity.typefaceRobotoRegular);

        mDrawerListView = (ListView) view.findViewById(R.id.navigation_drawer_list_view);
        mDrawerListView.setOnItemClickListener(this);
        mDrawerListView.setAdapter(new RadioDrawerAdapter());
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return view;
    }

    private void populateDrawerList()
    {
        drawerList.add(getResources().getString(R.string.drawer_all).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_dance).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_lounge).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_rock).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_jazz).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_top_forty).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_classic).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_hiphop).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_alternative).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_oldies).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_contemporary).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_country).toUpperCase());
        drawerList.add(getResources().getString(R.string.drawer_student).toUpperCase());
    }

    //bad coding
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
    {
        selectItem(position);
        mPosition = position;
        if ( position < 13 )
            prefEditor.putInt(STATION_PREFS, position).commit();
        Handler mDrawerHandler = new Handler();
        mDrawerHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                RadioMainPageActivity.currentStationsList
                        = GenreListSelector.selectList(position, getActivity().getBaseContext());
                new ThreadHelper().run();
            }
        }, 300);

    }

    //---------------------  used in ThreadHelper
    void fragLoadingLogic()
    {
        int pos = mPosition;
        if (shownFragment(SEARCH))
        {
            ActionBarAnimation.animationOut();
        }

        if (pos == 0)
            loadFragment(FragmentPlaylistList.newInstance(1), PLAYLIST_LIST);
        else
        {
            RadioMainPageActivity.bSearch.setVisibility(View.VISIBLE);
            RadioMainPageActivity.bAddPlaylist.setVisibility(View.GONE);
            loadFragment(FragmentRadioList.newInstance(4, background[pos]), RADIO_LIST);
        }
    }

    //--------------------- ThreadHelper used in onItemClick()
    class ThreadHelper implements Runnable
    {
        @Override
        public void run()
        {
            fragLoadingLogic();
        }
    }

    //---------------------
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bSettings:
                loadSettingsFragment();
                mDrawerLayout.closeDrawers();
                break;
            default:
                break;
        }
    }

    public static boolean isDrawerOpen()
    {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    private void selectItem(int position)
    {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null)
        {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null)
        {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout)
    {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setAnimationCacheEnabled(true);
        mDrawerLayout.setDrawingCacheEnabled(true);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.RIGHT);
        // set up the drawer's list view with items and click listener
        // between the navigation drawer and the action bar app icon.

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
    }

    //loading Settings Fragment
    private void loadSettingsFragment()
    {
        if (shownFragment(SETTINGS))
        {

        }
        else if (shownFragment(RADIO_LIST))
        {
            loadFragment(FragmentSettings.newInstance(2), SETTINGS);
        }
        else if (shownFragment(SEARCH))
        {
            ActionBarAnimation.animationOut();
            loadFragment(FragmentSettings.newInstance(2), SETTINGS);
        }
        else if (shownFragment(PLAYLIST_LIST))
        {
            ActionBarAnimation.animationPlaylistOut(getResources().getString(R.string.app_name));
            loadFragment(FragmentSettings.newInstance(2), SETTINGS);
        }
        else if (shownFragment(PLAYLIST))
        {
            ActionBarAnimation.animationPlaylistOut(getResources().getString(R.string.app_name));
            loadFragment(FragmentSettings.newInstance(2), SETTINGS);
        }
        else
        { /*do not load anything */ }
        RadioMainPageActivity.bSearch.setVisibility(View.VISIBLE);
        RadioMainPageActivity.bAddPlaylist.setVisibility(View.GONE);
    }

    //returns true if fragment is visible now
    private boolean shownFragment(String str)
    {
        if (getFragmentManager().findFragmentByTag(str) != null)
            return getFragmentManager().findFragmentByTag(str).isVisible();
        else
            return false;
    }

    //loader - calls replace() method
    private void loadFragment(Fragment fragToLoad, String fragTag)
    {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, fragToLoad, fragTag)
                .commit();
    }

    protected class RadioDrawerAdapter extends ArrayAdapter<String>
    {
        public RadioDrawerAdapter()
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_navigation_drawer, drawerList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View navigationItemView;
            if (position == 0)
            {
                navigationItemView = getActivity().getLayoutInflater().inflate(R.layout.custom_navdrawer_item, parent, false);
                TextView textViewDrawerMenuItem = (TextView) navigationItemView.findViewById(R.id.tvPlaylist);
                textViewDrawerMenuItem.setText(getResources().getString(R.string.drawer_playlist).toUpperCase());
                textViewDrawerMenuItem.setTypeface(typefaceThin, Typeface.BOLD);
            }
            else
            {
                navigationItemView = getActivity().getLayoutInflater().inflate(R.layout.navigation_drawer_item, parent, false);
                TextView textViewDrawerMenuItem = (TextView) navigationItemView.findViewById(R.id.textViewNavigDrawerItem);
                textViewDrawerMenuItem.setText(drawerList.get(position));
                textViewDrawerMenuItem.setTextColor(Color.WHITE);
                textViewDrawerMenuItem.setTypeface(typefaceRegular);
            }

            return navigationItemView;
        }
    }
}
