package com.fedortsyganov.iptest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.dialogs.DialogAbout;
import com.fedortsyganov.iptest.dialogs.DialogDefault;
import com.fedortsyganov.iptest.dialogs.DialogSetTimer;
import com.fedortsyganov.iptest.dialogs.DialogSort;
import com.fedortsyganov.iptest.dialogs.DialogWiFi;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 2/16/15.
 */
public class FragmentSettings extends Fragment implements AdapterView.OnItemClickListener
{
    public FragmentSettings(){}
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<String> settingsList = new ArrayList<String>();
    private boolean POPULATED  = false;
    private View settingsView;
    private SettingsAdapter settingsAdapter;
    private ListView listOfSettings;
    public static FragmentSettings fragmentSettings;
    public static final String LINK_TO_APP ="http://bit.ly/1K1eUgO";

    public static FragmentSettings newInstance(int positionNumber)
    {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, positionNumber);
        fragment.setArguments(args);
        fragmentSettings = fragment;
        return fragment;
    }

    private void populateList()
    {
        settingsList.add(getResources().getString(R.string.settings_wifi));
        //settingsList.add(getResources().getString(R.string.settings_playlist));
        settingsList.add(getResources().getString(R.string.settings_sort));
        settingsList.add(getResources().getString(R.string.settings_share));
        settingsList.add(getResources().getString(R.string.settings_timer));
        //settingsList.add(getResources().getString(R.string.settings_alarm_days));
        settingsList.add(getResources().getString(R.string.settings_about));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (!POPULATED)
            populateList();
        POPULATED = true;
        settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsAdapter = new SettingsAdapter();
        listOfSettings = (ListView) settingsView.findViewById(R.id.listSettings);
        listOfSettings.setAdapter(settingsAdapter);
        listOfSettings.setOnItemClickListener(this);

        return settingsView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                showWiFiDialog();
                break;
            //case 1:
              //  showDefaultDialog();
                //break;
            case 1:
                showSortDialog();
                break;
            case 2:
                String message = getString(R.string.coda_share_text)+LINK_TO_APP;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getString(R.string.coda_share_via)));
                break;
            case 3:
                showTimerDialog();
                break;
            //case 5:
                //showDefaultDialog();
                //break;
            case 4:
                showAboutDialog();
                break;
            default:
                showDefaultDialog();
                break;
        }
    }
    void showDefaultDialog()
    {
        DialogDefault dialogDefault = new DialogDefault();
        dialogDefault.show(getFragmentManager(), "default_dialog");
    }
    void showAboutDialog()
    {
        DialogAbout dialogAbout = new DialogAbout();
        dialogAbout.show(getFragmentManager(), "about_dialog");
    }
    void showWiFiDialog()
    {
        DialogWiFi dialogWiFi = new DialogWiFi();
        dialogWiFi.show(getFragmentManager(), "wifi_dialog");
    }
    void showSortDialog()
    {
        DialogSort dialogSort = new DialogSort();
        dialogSort.show(getFragmentManager(), "sort_dialog");
    }
    void showTimerDialog()
    {
        DialogSetTimer dialogSetTimer = new DialogSetTimer();
        dialogSetTimer.show(getFragmentManager(), "timer_dialog");
    }
    private class SettingsAdapter extends ArrayAdapter
    {
        View view;
        LayoutInflater inflater;
        TextView settingsName;

        public SettingsAdapter()
        {
            super(getActivity().getApplicationContext(), R.layout.fragment_settings, settingsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.list_item_settings, null);
            settingsName = (TextView) view.findViewById(R.id.tv_settings_name);
            settingsName.setText(settingsList.get(position));
            return view;
        }
    }
}
