package com.fedortsyganov.iptest.helpers;

import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.Comparator;

/**
 * Created by fedortsyganov on 3/22/15.
 */
public class GenreComparator implements Comparator <RadioStation>
{
    @Override
    public int compare(RadioStation lhs, RadioStation rhs)
    {
        return lhs.getStationGanre().compareTo(rhs.getStationGanre());
    }
}
