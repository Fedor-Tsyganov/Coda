package com.fedortsyganov.iptest.helpers;

import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.Comparator;

/**
 * Created by fedortsyganov on 4/6/15.
 */
public class RadioOBJComparator implements Comparator <RadioStation>
{
    @Override
    public int compare(RadioStation lhs, RadioStation rhs)
    {
        if (
                lhs.getStationCountry().equals(rhs.getStationCountry()) &&
                        lhs.getStationName().equals(rhs.getStationName()) &&
                        lhs.getStationUrl().equals(rhs.getStationUrl()) &&
                        lhs.getStationGanre().equals(rhs.getStationGanre())
                )
            return 0;
        else
            return -1;
    }
}
