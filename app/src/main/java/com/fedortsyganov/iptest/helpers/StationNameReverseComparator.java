package com.fedortsyganov.iptest.helpers;

import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.Comparator;

/**
 * Created by fedortsyganov on 4/4/15.
 */
public class StationNameReverseComparator implements Comparator<RadioStation>
{
    @Override
    public int compare(RadioStation lhs, RadioStation rhs)
    {
        return rhs.getStationName().compareTo(lhs.getStationName());
    }
}
