package com.fedortsyganov.iptest.helpers;

import android.graphics.Color;

/**
 * Created by fedortsyganov on 4/29/15.
 */
public class ColorGenerator
{
    private static String[] colors = new String[]
            {
                    "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
                    "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
                    /*"#FFEB3B",*/ "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E",
                    "#607D8B",    "#F44132", "#E1205A", "#922DAA", "#7132C1", "#4455BA",
                    "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A",
                    "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B","#FF9800"
            };

    public static int generateColor()
    {
        return Color.parseColor(colors[randomWithRange(0, colors.length - 1)]);
    }

    private static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }
}
