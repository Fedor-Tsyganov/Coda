package com.fedortsyganov.iptest.objects;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.fedortsyganov.iptest.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by fedortsyganov on 6/16/15.
 */
public class RSCountry extends RadioStation
{

    public static ArrayList <String> setSynonyms(Context context)
    {
        ArrayList <String> countriesWithSynonyms = new ArrayList<>();
        Resources resources = context.getResources();
        countriesWithSynonyms.add(resources.getString(R.string.country_usa));
        countriesWithSynonyms.add(resources.getString(R.string.country_unitedkingdom));
        return countriesWithSynonyms;
    }

    public static boolean hasSynonyms(String countryName, Context context)
    {
        ArrayList <String> toCheck = setSynonyms(context);
        for (int i = 0; i< toCheck.size(); i++)
            if (countryName.equals(toCheck.get(i)))
                return true;
        return false;
    }

    //get country from synonyms
    public static String getSynonyms(String countryName, Context context)
    {
        Resources res = context.getResources();
        String [] arrayUSA = res.getStringArray(R.array.usa_synonyms);
        String [] arrayUK = res.getStringArray(R.array.unitedkingdom_synonyms);
        int size1 = arrayUSA.length;
        int size2 = arrayUK.length;

        for (int c = 0; c < size1; c++)
        {
            //Log.e("search1","arrayUSA[c]:"+arrayUSA[c]);
            if (Pattern.compile(countryName.trim().replaceAll("\\s{2}", " "), Pattern.CASE_INSENSITIVE).matcher(arrayUSA[c]).find())
                return res.getString(R.string.country_usa);
        }
        for (int j = 0; j < size2; j++)
        {
            //Log.e("search2","arrayUK[j]:"+arrayUK[j]);
            if (Pattern.compile(countryName.trim().replaceAll("\\s{2}", " "), Pattern.CASE_INSENSITIVE).matcher(arrayUK[j]).find())
                return res.getString(R.string.country_unitedkingdom);
        }
        return "";

    }
}
