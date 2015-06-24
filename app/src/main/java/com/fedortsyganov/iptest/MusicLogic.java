package com.fedortsyganov.iptest;

import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 6/9/15.
 */
public class MusicLogic
{

    private ArrayList <RadioStation> playListToPlay;
    private ArrayList <RadioStation> playListToShow;

    public MusicLogic(ArrayList <RadioStation> pl2play, ArrayList <RadioStation> pl2show)
    {
        playListToPlay = new ArrayList<>(pl2play);
        playListToShow = new ArrayList<>(pl2show);
    }

    public void setPlayListToPlay(ArrayList <RadioStation> pl2play)
    {
        playListToPlay = new ArrayList<>(pl2play);
    }

    public void setPlayListToShow(ArrayList <RadioStation> pl2show)
    {
        playListToShow = new ArrayList<>(pl2show);
    }

    public ArrayList <RadioStation> getPlayListToPlay()
    {
        return playListToPlay;
    }

    public ArrayList <RadioStation> getPlayListToShow()
    {
        return playListToShow;
    }


}
