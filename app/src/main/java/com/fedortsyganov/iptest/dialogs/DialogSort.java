package com.fedortsyganov.iptest.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.helpers.CountryComparator;
import com.fedortsyganov.iptest.helpers.CountryReverseComparator;
import com.fedortsyganov.iptest.helpers.StationNameComparator;
import com.fedortsyganov.iptest.helpers.StationNameReverseComparator;

import java.util.Collections;

/**
 * Created by fedortsyganov on 4/4/15.
 */
public class DialogSort extends DialogFragment implements RadioGroup.OnCheckedChangeListener
{
    private RadioGroup radioSortGroup;
    private RadioButton radioButtonStationAZ, radioButtonStationZA, radioButtonCountryAZ, radioButtonCountryZA;
    public SharedPreferences preferences;
    public SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private static final String SORT_STATION_AZ = "SortStationAZ";
    private static final String SORT_STATION_ZA = "SortStationZA";
    private static final String SORT_COUNTRY_AZ = "SortCountryAZ";
    private static final String SORT_COUNTRY_ZA = "SortCountryZA";
    private boolean changed = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int style = android.support.v4.app.DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        preferences = getActivity().getSharedPreferences(STATION_TO_SAVE, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        changed = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sort, null);
        radioSortGroup = (RadioGroup) dialogView.findViewById(R.id.radioGroupSort);
        radioButtonStationAZ = (RadioButton) dialogView.findViewById(R.id.radioButtonStationAZ);
        radioButtonStationZA = (RadioButton) dialogView.findViewById(R.id.radioButtonStationZA);
        radioButtonCountryAZ = (RadioButton) dialogView.findViewById(R.id.radioButtonCountryAZ);
        radioButtonCountryZA = (RadioButton) dialogView.findViewById(R.id.radioButtonCountryZA);

        if (preferences.getBoolean(SORT_STATION_AZ, false))
            radioButtonStationAZ.setChecked(true);
        if (preferences.getBoolean(SORT_STATION_ZA, false))
            radioButtonStationZA.setChecked(true);
        if (preferences.getBoolean(SORT_COUNTRY_AZ, false))
            radioButtonCountryAZ.setChecked(true);
        if (preferences.getBoolean(SORT_COUNTRY_ZA, false))
            radioButtonCountryZA.setChecked(true);

        radioSortGroup.setOnCheckedChangeListener(this);


        builder.setView(dialogView)
                .setPositiveButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //String str = profileEditor.getText().toString();
                        //Properties.PROFILE_INFORMATION = str;
                        if (changed)
                        {
                            sortLists();
                        }
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        changed = true;
        switch (checkedId)
        {
            case R.id.radioButtonStationAZ:
                prefEditor.putBoolean(SORT_STATION_AZ, true).commit();
                prefEditor.putBoolean(SORT_STATION_ZA, false).commit();
                prefEditor.putBoolean(SORT_COUNTRY_AZ, false).commit();
                prefEditor.putBoolean(SORT_COUNTRY_ZA, false).commit();
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.settings_dialog_sort_choice_stations_az),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButtonStationZA:
                prefEditor.putBoolean(SORT_STATION_AZ, false).commit();
                prefEditor.putBoolean(SORT_STATION_ZA, true).commit();
                prefEditor.putBoolean(SORT_COUNTRY_AZ, false).commit();
                prefEditor.putBoolean(SORT_COUNTRY_ZA, false).commit();
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.settings_dialog_sort_choice_stations_za)
                        , Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButtonCountryAZ:
                prefEditor.putBoolean(SORT_STATION_AZ, false).commit();
                prefEditor.putBoolean(SORT_STATION_ZA, false).commit();
                prefEditor.putBoolean(SORT_COUNTRY_AZ, true).commit();
                prefEditor.putBoolean(SORT_COUNTRY_ZA, false).commit();
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.settings_dialog_sort_choice_countries_az)
                        , Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButtonCountryZA:
                prefEditor.putBoolean(SORT_STATION_AZ, false).commit();
                prefEditor.putBoolean(SORT_STATION_ZA, false).commit();
                prefEditor.putBoolean(SORT_COUNTRY_AZ, false).commit();
                prefEditor.putBoolean(SORT_COUNTRY_ZA, true).commit();
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.settings_dialog_sort_choice_countries_za)
                        , Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
    }
    public void sortLists()
    {
        if (preferences.getBoolean(SORT_STATION_AZ, false))
        {
            for (int i = 0; i < RadioMainPageActivity.bigArray.size(); i++)
            {
                Collections.sort(RadioMainPageActivity.bigArray.get(i), new StationNameComparator());
            }
        }
        if (preferences.getBoolean(SORT_STATION_ZA, false))
        {
            for (int i = 0; i < RadioMainPageActivity.bigArray.size(); i++)
            {
                Collections.sort(RadioMainPageActivity.bigArray.get(i), new StationNameReverseComparator());
            }
        }
        if (preferences.getBoolean(SORT_COUNTRY_AZ, false))
        {
            for (int i = 0; i < RadioMainPageActivity.bigArray.size(); i++)
            {
                Collections.sort(RadioMainPageActivity.bigArray.get(i), new CountryComparator());
            }
        }
        if (preferences.getBoolean(SORT_COUNTRY_ZA, false))
        {
            for (int i = 0; i < RadioMainPageActivity.bigArray.size(); i++)
            {
                Collections.sort(RadioMainPageActivity.bigArray.get(i), new CountryReverseComparator());
            }
        }
    }
}
