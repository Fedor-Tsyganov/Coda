package com.fedortsyganov.iptest.fragments;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedortsyganov.iptest.LauncherActivity;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioNavigationDrawer;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.objects.RSCountry;
import com.fedortsyganov.iptest.objects.RadioStation;
import com.fedortsyganov.iptest.helpers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Created by fedortsyganov on 2/16/15.
 */
public class FragmentSearch extends Fragment
{
    public FragmentSearch()
    {
    }

    private static final String STATION_TO_SAVE = "info";
    private static final String RADIO_LIST = "RADIO_FRAGMENT";
    private static final String STATION_PREFS = "ListPosition";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String SEARCH = "SEARCH_FRAGMENT";
    private static final String PLAYLIST_LIST = "PLAYLIST_LIST_FRAGMENT";
    private View searchView;
    public static FragmentSearch fragmentSearch;
    public static Button bCloseSearch;
    private ListView listSearch;
    private static ArrayList<RadioStation> stationArrayList;
    private SearchArrayAdapter searchArrayAdapter;
    public static ArrayList<Integer> headers;
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    public TextView stations, countries;
    public ImageView line1, line2;
    public boolean lookStations = true;
    public static final String noResults = "No Results";
    private InputMethodManager imm;
    public static Context context;

    public static FragmentSearch newInstance(int positionNumber)
    {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, positionNumber);
        fragment.setArguments(args);
        fragmentSearch = fragment;

        RadioMainPageActivity.testAllStations = new ArrayList<>( RadioMainPageActivity.allStations);
        Collections.sort( RadioMainPageActivity.testAllStations, new CountryComparator());
        stationArrayList = setHeaders(RadioMainPageActivity.testAllStations);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = getActivity().getBaseContext();
        preferences = getActivity().getSharedPreferences(STATION_TO_SAVE, getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();

        imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);

        searchView = inflater.inflate(R.layout.fragment_search, container, false);
        stations = (TextView) searchView.findViewById(R.id.tabStations);
        countries = (TextView) searchView.findViewById(R.id.tabCountries);
        line1 = (ImageView) searchView.findViewById(R.id.tabline1);
        line2 = (ImageView) searchView.findViewById(R.id.tabline2);

        stations.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lookStations = true;
                line1.setBackgroundColor(getResources().getColor(R.color.pink_search));
                line2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                stations.setTextColor(getResources().getColor(R.color.pink_search));
                countries.setTextColor(getResources().getColor(R.color.white));
                stationArrayList = new ArrayList<>(setHeaders(searchStation(RadioMainPageActivity.searchET.getText().toString())));
                searchArrayAdapter.clear();
                searchArrayAdapter.addAll(stationArrayList);
                searchArrayAdapter.notifyDataSetChanged();
            }
        });
        countries.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lookStations = false;
                line1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                line2.setBackgroundColor(getResources().getColor(R.color.pink_search));
                stations.setTextColor(getResources().getColor(R.color.white));
                countries.setTextColor(getResources().getColor(R.color.pink_search));
                stationArrayList = new ArrayList<>(setHeaders(searchCountry(RadioMainPageActivity.searchET.getText().toString())));
                searchArrayAdapter.clear();
                searchArrayAdapter.addAll(stationArrayList);
                searchArrayAdapter.notifyDataSetChanged();
            }
        });

        bCloseSearch = (Button) searchView.findViewById(R.id.buttonCloseSearch);
        bCloseSearch.setVisibility(View.VISIBLE);
        bCloseSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!RadioMainPageActivity.searchET.getText().toString().equals(""))
                {
                    RadioMainPageActivity.searchET.getText().clear();
                }
                else
                {

                    //hiding keyboard
                    bCloseSearch.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            imm.hideSoftInputFromWindow(RadioMainPageActivity.searchET.getWindowToken(), 0);
                        }
                    }, 10);
                    ActionBarAnimation.animationOut();
                    int image = preferences.getInt(STATION_PREFS, 0);
                    if (image == 0)
                    {
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .addToBackStack(null)
                                .replace(R.id.container, FragmentPlaylistList.newInstance(1), PLAYLIST_LIST)
                                .commit();

                    }
                    else
                    {
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .addToBackStack(null)
                                .replace(R.id.container, FragmentRadioList.newInstance(4, RadioNavigationDrawer.background[image]), RADIO_LIST)
                                .commit();
                    }
                }
            }
        });
        listSearch = (ListView) searchView.findViewById(R.id.listSearch);
        searchArrayAdapter = new SearchArrayAdapter(stationArrayList);
        listSearch.setAdapter(searchArrayAdapter);
        RadioMainPageActivity.searchET.addTextChangedListener(inputTextWatcher);

        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                if (RadioMainPageActivity.previousStationsList.contains(stationArrayList.get(position)))
                {
                    int sizeCurrent = RadioMainPageActivity.previousStationsList.size();
                    for (int i = 0; i < sizeCurrent; i++)
                    {
                        if (RadioMainPageActivity.previousStationsList.get(i).equals(stationArrayList.get(position)))
                        {
                            RadioMainPageActivity.radioStationPosition = i;
                        }
                    }
                }
                else
                {
                    /*
                    int sizeAll = RadioMainPageActivity.allStations.size();
                    for (int i = 0; i < sizeAll; i++)
                    {
                        if (RadioMainPageActivity.allStations.get(i).equals(stationArrayList.get(position)))
                        {
                            RadioMainPageActivity.radioStationPosition = i;
                        }
                    }
                    RadioMainPageActivity.previousStationsList = new ArrayList<>(RadioMainPageActivity.allStations);
                    */
                    //test

                    String genre = stationArrayList.get(position).getStationGanre();
                    int sizeGenre = GenreListSelector.selectListByGenere(genre, getActivity().getBaseContext()).size();
                    ArrayList<RadioStation> tempList = GenreListSelector.selectListByGenere(genre, getActivity().getBaseContext());

                    for (int c = 0; c < sizeGenre; c++)
                    {
                        if (tempList.get(c).equals(stationArrayList.get(position)))
                        {
                            RadioMainPageActivity.radioStationPosition = c;
                        }
                    }
                    RadioPlayerActivity.changeList = false;
                    //if (RadioNavigationDrawer.mPosition == 0)
                    //{
                    //  RadioNavigationDrawer.mPosition = GenreListSelector.listPosition(genre);
                    //}
                    //prefEditor.putInt("ListPosition", 0).commit();
                    RadioMainPageActivity.previousStationsList = tempList;
                    RadioMainPageActivity.currentStationsList = new ArrayList<>(tempList);
                }
                if (Debuger.DEBUG)
                {
                    String current = "current:"
                            + RadioMainPageActivity.currentStationsList.get(position).getStationName();
                    String previous = " previous:"
                            + RadioMainPageActivity.previousStationsList.get(position).getStationName();
                    String rdStn = " radioStation:"
                            + RadioMainPageActivity.radioStation.getStationName();
                    Log.e("Search", current + previous + rdStn);
                }
                RadioMainPageActivity.applyMargin = true;
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(), RadioPlayerActivity.class));
            }
        });
        //adds margin, so last item in the list can be above information panel

        return searchView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (RadioMainPageActivity.applyMargin)
        {
            int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
            listSearch.setPadding(0,0,0, margin);
        }
    }

    private ArrayList <RadioStation> searchStation(final String value)
    {
        final ArrayList<RadioStation> searchResult = new ArrayList<>();
        // RadioMainPage.currentStationsList
        int size = RadioMainPageActivity.testAllStations.size();
        int counter = 0;
        if (Debuger.DEBUG)
        {
            Log.v("SEARCH_TEST", "------------------START----------------------");
        }
        for (int i = 0; i < size - 1; i++)
        {
            String stationName = RadioMainPageActivity.testAllStations.get(i).getStationName();
            //value.trim().replaceAll("\\s{2}", " ") to replace extra spaces that are inserted
            if (Pattern.compile(value.trim().replaceAll("\\s{2}", " "), Pattern.CASE_INSENSITIVE).matcher(stationName).find())
            {
                if (Debuger.DEBUG)
                    Log.v("SEARCH_TEST", RadioMainPageActivity.currentStationsList.get(i).getStationName());
                searchResult.add(RadioMainPageActivity.testAllStations.get(i));
                counter++;
            }
        }
        //stationArrayList = searchResult;
        if (Debuger.DEBUG)
            Log.v("SEARCH_TEST", "-------------------END--------------------- COUNTER : " + counter);

        return searchResult;
    }

    private static ArrayList<Integer> getHeaders(ArrayList<RadioStation> list)
    {
        headers = new ArrayList<Integer>();
        headers.add(0);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            if (!list.get(i-1).getStationCountry().equals(list.get(i).getStationCountry()))
            {
                headers.add(i);
            }
        }
        return headers;
    }

    private ArrayList <RadioStation> searchCountry(final String value)
    {
        final ArrayList<RadioStation> searchResult = new ArrayList<>();
        // RadioMainPage.currentStationsList
        int size = RadioMainPageActivity.testAllStations.size();
        int counter = 0;
        if (Debuger.DEBUG)
            Log.v("SEARCH_TEST", "------------------START----------------------");
        for (int i = 0; i < size - 1; i++)
        {
            String countryName = RadioMainPageActivity.testAllStations.get(i).getStationCountry();
            boolean foundCountry =
                    Pattern
                    .compile(value.trim().replaceAll("\\s{2}", " "), Pattern.CASE_INSENSITIVE)
                    .matcher(countryName)
                    .find();
            //value.trim().replaceAll("\\s{2}", " ") to replace extra spaces that are inserted
            if (foundCountry)
            {
                if (Debuger.DEBUG)
                    Log.v("SEARCH_TEST", RadioMainPageActivity.previousStationsList.get(i).getStationName());
                searchResult.add(RadioMainPageActivity.testAllStations.get(i));
                counter++;
            }

        }
        //stationArrayList = searchResult;
        if (Debuger.DEBUG)
            Log.v("SEARCH_TEST", "-------------------END--------------------- COUNTER : " + counter);

        return searchResult;
    }
    private ArrayList <RadioStation> searchCountrySynonym(String value)
    {
        final ArrayList<RadioStation> searchResult = new ArrayList<>();
        int size = RadioMainPageActivity.testAllStations.size();
        for (int i = 0; i < size - 1; i++)
        {

            String countrySynonym = RSCountry.getSynonyms(value, getActivity().getBaseContext());
            String countryName = RadioMainPageActivity.testAllStations.get(i).getStationCountry();
            if (Debuger.DEBUG)
                Log.v("searchCountrySynonym()", "Country Name: "+countryName+" - Country Synonym: "+ countrySynonym);
            if (countrySynonym.equalsIgnoreCase(countryName))
            {
                searchResult.add(RadioMainPageActivity.testAllStations.get(i));
            } else {}
        }
        return searchResult;
    }

    private ArrayList<String> searchGenre(String value)
    {
        return new ArrayList<>();
    }

    private TextWatcher inputTextWatcher = new TextWatcher()
    {
        public void afterTextChanged(Editable s)
        {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            //used to be ArrayList <RadioStation> temp = new ...
            if (lookStations)
            {
                stationArrayList = new ArrayList<>(setHeaders(searchStation(RadioMainPageActivity.searchET.getText().toString())));
            }
            else
            {
                stationArrayList = new ArrayList<>(setHeaders(searchCountry(RadioMainPageActivity.searchET.getText().toString())));
                if (stationArrayList.size() <= 1)
                {
                    stationArrayList = new ArrayList<>(setHeaders(searchCountrySynonym(RadioMainPageActivity.searchET.getText().toString())));
                }
            }
            searchArrayAdapter.clear();
            searchArrayAdapter.addAll(stationArrayList);
            searchArrayAdapter.notifyDataSetChanged();
            if (Debuger.DEBUG)
                Log.v("SEARCH_TEST", "NEW SIZE: "+RadioMainPageActivity.currentStationsList.size()+"");
        }
    };

    public static ArrayList<RadioStation> setHeaders(ArrayList <RadioStation> list)
    {
        if (list.size() > 0)
        {
            ArrayList<Integer> mHeader = getHeaders(list);
            ArrayList<RadioStation> mList = new ArrayList<>(list);
            int counter = 0;
            int size = mHeader.size();
            for (int pos = 0; pos < size; pos++)
            {
                if (Debuger.DEBUG)
                    Log.i("headers",""+mHeader.get(pos)/*RadioMainPage.testAllStations.get(header.get(j)).getStationCountry()*/);
                mList.add(mHeader.get(pos) + counter, new RadioStation(true, list.get(mHeader.get(pos)).getStationCountry()));
                counter++;
            }
            return mList;
        }
        else
        {
            Resources resources = context.getResources();
            String noResults = resources.getString(R.string.fragment_search_noresults);
            ArrayList<RadioStation> eList = new ArrayList<>();
            eList.add(new RadioStation(true, noResults));
            return eList;
        }
    }

    class SearchArrayAdapter extends ArrayAdapter<RadioStation>
    {
        ArrayList<RadioStation> mList;
        public SearchArrayAdapter(ArrayList<RadioStation> list)
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_search, list);
            mList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Typeface typefaceTextView = LauncherActivity.typefaceRobotoRegular;
            View searchViewList = getActivity().getLayoutInflater().inflate(R.layout.search_item, parent, false);
            if (mList != null)
            {
                if ( mList.get(position).isHeader() )
                {
                    searchViewList = getActivity().getLayoutInflater().inflate(R.layout.search_header, parent, false);
                    //searchHeaderTV
                    TextView textViewSearchListItem = (TextView) searchViewList.findViewById(R.id.searchHeaderTV);
                    textViewSearchListItem.setText(mList.get(position).getStationCountry().toUpperCase());
                    textViewSearchListItem.setTextColor(getResources().getColor(R.color.grey_light));
                    textViewSearchListItem.setTypeface(typefaceTextView);
                }
                else
                {
                    TextView textViewSearchListItem = (TextView) searchViewList.findViewById(R.id.textViewSearchItem);
                    textViewSearchListItem.setText(mList.get(position).getStationName());
                    textViewSearchListItem.setTextColor(Color.BLACK);
                    textViewSearchListItem.setTypeface(typefaceTextView);
                }

            }
            return searchViewList;
        }
    }

}
