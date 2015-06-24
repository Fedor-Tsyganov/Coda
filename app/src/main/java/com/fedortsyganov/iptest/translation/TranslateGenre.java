package com.fedortsyganov.iptest.translation;

import android.content.Context;
import android.content.res.Resources;

import com.fedortsyganov.iptest.R;

/**
 * Created by fedortsyganov on 6/2/15.
 */
public class TranslateGenre
{
    private static final String genres [] =
            {
            "Top40", "Oldies", "Alternative", "Adult Contemporary", "Hip-Hop", "Dance",
            "Classical", "Lounge", "Rock", "College", "Jazz", "Country"
            };
    public static String translate(String defaultLang, Context context)
    {
        Resources res = context.getResources();
        String genre = "";
        if (defaultLang.equals(genres[0]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_top_forty));
        }
        else if (defaultLang.equals(genres[1]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_oldies));
        }
        else if (defaultLang.equals(genres[2]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_alternative));
        }
        else if (defaultLang.equals(genres[3]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_contemporary));
        }
        else if (defaultLang.equals(genres[4]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_hiphop));
        }
        else if (defaultLang.equals(genres[5]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_dance));
        }
        else if (defaultLang.equals(genres[6]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_classic));
        }
        else if (defaultLang.equals(genres[7]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_lounge));
        }
        else if (defaultLang.equals(genres[8]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_rock));
        }
        else if (defaultLang.equals(genres[9]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_student));
        }
        else if (defaultLang.equals(genres[10]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_jazz));
        }
        else if (defaultLang.equals(genres[11]))
        {
            genre = toUpperCaseFirstChar(res.getString(R.string.drawer_country));
        }
        else
        {
            genre = toUpperCaseFirstChar("wrong genre name");
        }

        return genre;
    }

    private static String toUpperCaseFirstChar(String genre)
    {
        StringBuffer res = new StringBuffer();

        String[] strArr = genre.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

       return res.toString().trim();
    }
}
