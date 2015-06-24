package com.fedortsyganov.iptest.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.fragments.FragmentSearch;

/**
 * Created by fedortsyganov on 3/26/15.
 */
public class ActionBarAnimation
{
    private static final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    private static final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    //commented out numbers that moved playlist to right/left
    public static void animationIn()
    {
        fadeOut.setDuration(10); //200
        fadeIn.setDuration(80);  //200
        RadioMainPageActivity.tvActionBarInfo.startAnimation(fadeOut);
        RadioMainPageActivity.tvActionBarInfo.setVisibility(View.INVISIBLE);
        RadioMainPageActivity.bSearch.setVisibility(View.INVISIBLE);
        RadioMainPageActivity.searchET.startAnimation(fadeIn);
        RadioMainPageActivity.searchET.setVisibility(View.VISIBLE);
    }

    public static void animationOut()
    {
        fadeOut.setDuration(10);  //400
        fadeIn.setDuration(80);   //300
        RadioMainPageActivity.searchET.startAnimation(fadeOut);
        RadioMainPageActivity.searchET.setVisibility(View.INVISIBLE);
        RadioMainPageActivity.tvActionBarInfo.startAnimation(fadeIn);
        RadioMainPageActivity.tvActionBarInfo.setVisibility(View.VISIBLE);
        FragmentSearch.bCloseSearch.setVisibility(View.INVISIBLE);
        RadioMainPageActivity.bSearch.setVisibility(View.VISIBLE);
    }

    public static void animationPlaylistIn(String playlistName)
    {
        fadeOut.setDuration(10); //200
        fadeIn.setDuration(80);  //200
        RadioMainPageActivity.tvActionBarInfo.startAnimation(fadeOut);
        RadioMainPageActivity.tvActionBarInfo.setVisibility(View.INVISIBLE);
        RadioMainPageActivity.tvActionBarInfo.setText(cap1stChar(playlistName));
        RadioMainPageActivity.tvActionBarInfo.startAnimation(fadeIn);
        RadioMainPageActivity.tvActionBarInfo.setVisibility(View.VISIBLE);
    }
    public static void animationPlaylistOut(String appName)
    {
        fadeOut.setDuration(10); //200
        fadeIn.setDuration(80);  //200
        RadioMainPageActivity.tvActionBarInfo.startAnimation(fadeOut);
        RadioMainPageActivity.tvActionBarInfo.setVisibility(View.INVISIBLE);
        RadioMainPageActivity.tvActionBarInfo.setText(appName);
        RadioMainPageActivity.tvActionBarInfo.startAnimation(fadeIn);
        RadioMainPageActivity.tvActionBarInfo.setVisibility(View.VISIBLE);
    }

    private static String cap1stChar(String userIdea)
    {
        char[] stringArray = userIdea.toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        return new String(stringArray);
    }
}
