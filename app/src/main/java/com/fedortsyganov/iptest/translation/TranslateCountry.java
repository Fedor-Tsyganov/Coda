package com.fedortsyganov.iptest.translation;

import android.content.Context;
import android.content.res.Resources;

import com.fedortsyganov.iptest.R;

/**
 * Created by fedortsyganov on 6/2/15.
 */
public class TranslateCountry
{
    private static final String country[] =
            {
                    "Austria", "Belarus", "Belgium", "Bulgaria", "Croatia", "Czech Republic",
                    "Denmark", "Estonia", "Finland", "France", "Germany", "Greece", "Hungary",
                    "Ireland", "Italy", "Latvia", "Lithuania", "Luxembourg", "Macedonia",
                    "Netherlands", "Norway", "Poland", "Russia", "Slovakia", "Slovenia", "Spain",
                    "Sweden", "Switzerland", "Ukraine", "United Kingdom", "USA"
            };

    public static String translate(String defaultLang, Context context)
    {
        String countryName = "";
        Resources res = context.getResources();
        if (defaultLang.equals(country[0]))
        {
            countryName = res.getString(R.string.country_austria);
        }
        else if (defaultLang.equals(country[1]))
        {
            countryName = res.getString(R.string.country_belarus);
        }
        else if (defaultLang.equals(country[2]))
        {
            countryName = res.getString(R.string.country_belgium);
        }
        else if (defaultLang.equals(country[3]))
        {
            countryName = res.getString(R.string.country_bulgaria);
        }
        else if (defaultLang.equals(country[4]))
        {
            countryName = res.getString(R.string.country_croatia);
        }
        else if (defaultLang.equals(country[5]))
        {
            countryName = res.getString(R.string.country_czechrepublic);
        }
        else if (defaultLang.equals(country[6]))
        {
            countryName = res.getString(R.string.country_denmark);
        }
        else if (defaultLang.equals(country[7]))
        {
            countryName = res.getString(R.string.country_estonia);
        }
        else if (defaultLang.equals(country[8]))
        {
            countryName = res.getString(R.string.country_finland);
        }
        else if (defaultLang.equals(country[9]))
        {
            countryName = res.getString(R.string.country_france);
        }
        else if (defaultLang.equals(country[10]))
        {
            countryName = res.getString(R.string.country_germany);
        }
        else if (defaultLang.equals(country[11]))
        {
            countryName = res.getString(R.string.country_greece);
        }
        else if (defaultLang.equals(country[12]))
        {
            countryName = res.getString(R.string.country_hungary);
        }
        else if (defaultLang.equals(country[13]))
        {
            countryName = res.getString(R.string.country_ireland);
        }
        else if (defaultLang.equals(country[14]))
        {
            countryName = res.getString(R.string.country_italy);
        }
        else if (defaultLang.equals(country[15]))
        {
            countryName = res.getString(R.string.country_latvia);
        }
        else if (defaultLang.equals(country[16]))
        {
            countryName = res.getString(R.string.country_lithuania);
        }
        else if (defaultLang.equals(country[17]))
        {
            countryName = res.getString(R.string.country_luxembourg);
        }
        else if (defaultLang.equals(country[18]))
        {
            countryName = res.getString(R.string.country_macedonia);
        }
        else if (defaultLang.equals(country[19]))
        {
            countryName = res.getString(R.string.country_netherlands);
        }
        else if (defaultLang.equals(country[20]))
        {
            countryName = res.getString(R.string.country_norway);
        }
        else if (defaultLang.equals(country[21]))
        {
            countryName = res.getString(R.string.country_poland);
        }
        else if (defaultLang.equals(country[22]))
        {
            countryName = res.getString(R.string.country_russia);
        }
        else if (defaultLang.equals(country[23]))
        {
            countryName = res.getString(R.string.country_slovakia);
        }
        else if (defaultLang.equals(country[24]))
        {
            countryName = res.getString(R.string.country_slovenia);
        }
        else if (defaultLang.equals(country[25]))
        {
            countryName = res.getString(R.string.country_spain);
        }
        else if (defaultLang.equals(country[26]))
        {
            countryName = res.getString(R.string.country_sweden);
        }
        else if (defaultLang.equals(country[27]))
        {
            countryName = res.getString(R.string.country_switzerland);
        }
        else if (defaultLang.equals(country[28]))
        {
            countryName = res.getString(R.string.country_ukraine);
        }
        else if (defaultLang.equals(country[29]))
        {
            countryName = res.getString(R.string.country_unitedkingdom);
        }
        else if (defaultLang.equals(country[30]))
        {
            countryName = res.getString(R.string.country_usa);
        }
        else
        {
            countryName = "wrong country name";
        }

        return countryName;
    }
}
