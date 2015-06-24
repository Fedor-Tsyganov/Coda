package com.fedortsyganov.iptest.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 5/29/15.
 */
public class GenreListSelector
{
    private static ArrayList <RadioStation> stationList;
    public static ArrayList<RadioStation> selectList (int position, Context context)
    {
        switch (position)
        {
            case 0:
                break;
            case 1:
                stationList = new ArrayList<>(RadioMainPageActivity.danceStations);
                break;
            case 2:
                stationList = new ArrayList<>(RadioMainPageActivity.loungeStations);
                break;
            case 3:
                stationList = new ArrayList<>(RadioMainPageActivity.rockStations);
                break;
            case 4:
                stationList = new ArrayList<>(RadioMainPageActivity.jazzStations);
                break;
            case 5:
                stationList = new ArrayList<>(RadioMainPageActivity.topFourtyStations);
                break;
            case 6:
                stationList = new ArrayList<>(RadioMainPageActivity.classicalStations);
                break;
            case 7:
                stationList = new ArrayList<>(RadioMainPageActivity.hiphopStations);
                break;
            case 8:
                stationList = new ArrayList<>(RadioMainPageActivity.alternativeStations);
                break;
            case 9:
                stationList = new ArrayList<>(RadioMainPageActivity.oldiesStations);
                break;
            case 10:
                stationList = new ArrayList<>(RadioMainPageActivity.adultContemporaryStations);
                break;
            case 11:
                stationList = new ArrayList<>(RadioMainPageActivity.countryStations);
                break;
            case 12:
                stationList = new ArrayList<>(RadioMainPageActivity.studentStations);
                break;
            default:
                Toast.makeText(context, "You pushed a button", Toast.LENGTH_SHORT).show();
                break;
        }
        return stationList;
    }

    public static ArrayList<RadioStation> selectListByGenere (String genre, Context context)
    {
        Resources res = context.getResources();

        if (genre.equals(res.getString(R.string.drawer_dance)))
            stationList = new ArrayList<>(RadioMainPageActivity.danceStations);
        if (genre.equals(res.getString(R.string.drawer_lounge)))
            stationList = new ArrayList<>(RadioMainPageActivity.loungeStations);
        if (genre.equals(res.getString(R.string.drawer_rock)))
            stationList = new ArrayList<>(RadioMainPageActivity.rockStations);
        if (genre.equals(res.getString(R.string.drawer_jazz)))
            stationList = new ArrayList<>(RadioMainPageActivity.jazzStations);
        if (genre.equals(res.getString(R.string.drawer_top_forty)))
            stationList = new ArrayList<>(RadioMainPageActivity.topFourtyStations);
        if (genre.equals(res.getString(R.string.drawer_classic)))
            stationList = new ArrayList<>(RadioMainPageActivity.classicalStations);
        if (genre.equals(res.getString(R.string.drawer_hiphop)))
            stationList = new ArrayList<>(RadioMainPageActivity.hiphopStations);
        if (genre.equals(res.getString(R.string.drawer_alternative)))
            stationList = new ArrayList<>(RadioMainPageActivity.alternativeStations);
        if (genre.equals(res.getString(R.string.drawer_oldies)))
            stationList = new ArrayList<>(RadioMainPageActivity.oldiesStations);
        if (genre.equals(res.getString(R.string.drawer_contemporary)))
            stationList = new ArrayList<>(RadioMainPageActivity.adultContemporaryStations);
        if (genre.equals(res.getString(R.string.drawer_country)))
            stationList = new ArrayList<>(RadioMainPageActivity.countryStations);
        if (genre.equals(res.getString(R.string.drawer_student)))
            stationList = new ArrayList<>(RadioMainPageActivity.studentStations);

        return stationList;
    }

    public static int listPosition(String genre, Context context)
    {
        Resources res = context.getResources();
        int position;

        if (genre.equals(res.getString(R.string.drawer_dance)))
            position = 1;
        else if (genre.equals(res.getString(R.string.drawer_lounge)))
            position = 2;
        else if (genre.equals(res.getString(R.string.drawer_rock)))
            position = 3;
        else if (genre.equals(res.getString(R.string.drawer_jazz)))
            position = 4;
        else if (genre.equals(res.getString(R.string.drawer_top_forty)))
            position = 5;
        else if (genre.equals(res.getString(R.string.drawer_classic)))
            position = 6;
        else if (genre.equals(res.getString(R.string.drawer_hiphop)))
            position = 7;
        else if (genre.equals(res.getString(R.string.drawer_alternative)))
            position = 8;
        else if (genre.equals(res.getString(R.string.drawer_oldies)))
            position = 9;
        else if (genre.equals(res.getString(R.string.drawer_contemporary)))
            position = 10;
        else if (genre.equals(res.getString(R.string.drawer_country)))
            position = 11;
        else if (genre.equals(res.getString(R.string.drawer_student)))
            position = 12;
        else
            position = 0;

        return position;
    }
}
