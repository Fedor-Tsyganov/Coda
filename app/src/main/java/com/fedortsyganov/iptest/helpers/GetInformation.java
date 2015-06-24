package com.fedortsyganov.iptest.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.google.android.gms.common.api.Api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by fedortsyganov on 6/3/15.
 */
public class GetInformation
{
    private static ArrayList<String> removeDuplicates(ArrayList<String> list)
    {

        // Store unique items in result.
        ArrayList<String> result = new ArrayList<>();

        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<>();

        // Loop over argument list.
        for (String item : list)
        {

            // If String is not in set, add it to the list and the set.
            if (!set.contains(item))
            {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }

    public static ArrayList<String> getAllCountries()
    {
        ArrayList <String> countries = new ArrayList<>();
        for (int i = 0; i < RadioMainPageActivity.testAllStations.size(); i++)
        {
            countries.add(RadioMainPageActivity.testAllStations.get(i).getStationCountry());
        }
        return removeDuplicates(countries);
    }

    public static ArrayList<String> getAllGenres()
    {
        ArrayList <String> genres = new ArrayList<>();
        for (int i = 0; i < RadioMainPageActivity.testAllStations.size(); i++)
        {
            genres.add(RadioMainPageActivity.testAllStations.get(i).getStationGanre());
        }
        return removeDuplicates(genres);
    }
    public static ArrayList<String> getAllURLs()
    {
        ArrayList <String> urls = new ArrayList<>();
        for (int i = 0; i < RadioMainPageActivity.testAllStations.size(); i++)
        {
            urls.add(RadioMainPageActivity.testAllStations.get(i).getStationUrl());
        }
        return removeDuplicates(urls);
    }
    public static ArrayList<String> getAllStationNames()
    {
        ArrayList <String> names = new ArrayList<>();
        for (int i = 0; i < RadioMainPageActivity.testAllStations.size(); i++)
        {
            names.add(RadioMainPageActivity.testAllStations.get(i).getStationName());
        }
        return removeDuplicates(names);
    }

    public static String reader(String country)
    {
        String everything = "";
        StringBuilder main = new StringBuilder();
        main.append("<string name=\"country_"+country.toLowerCase().replace(" ","")+"\">" + country+ "</string>");
        everything = main.toString();
        return everything;
    }

    @SuppressLint("NewApi")
    public static void writeToFile(Context context) throws IOException
    {
        String path = context.getExternalFilesDir(null).getAbsolutePath();
        File file = new File(path + "/countries.txt");
        ArrayList <String> cnt = getAllCountries();
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < cnt.size(); c++)
        {
            sb.append(reader(cnt.get(c)) + System.lineSeparator());
        }
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(sb.toString().getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }


}
