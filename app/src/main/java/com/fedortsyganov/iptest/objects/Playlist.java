package com.fedortsyganov.iptest.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/10/15.
 */
public class Playlist
{
    public Playlist(){}
    public Playlist (String name, int numberOfObjects, int color)
    {
        this.name = name;
        this.numberOfObjects = numberOfObjects;
        this.color = color;
    }

    @Expose
    @SerializedName("name")
    private String name;
    public String getPlaylistName()
    {
        return name;
    }
    public void setPlaylistName(String name)
    {
        this.name = name;
    }

    @Expose
    @SerializedName("numberOfObjects")
    private int numberOfObjects;
    public int getNumberOfObjects() { return numberOfObjects; }
    public void setNumberOfObjects(int numberOfObjects) {this.numberOfObjects = numberOfObjects; }

    @Expose
    @SerializedName("id")
    private String id;
    public String getPlaylistID()
    {
        return id;
    }
    public void setPlaylistID(String id)
    {
        this.id = id;
    }

    @Expose
    @SerializedName("stations")
    private ArrayList<RadioStation> stations;
    public  ArrayList<RadioStation> getStations()
    {
        return stations;
    }
    public void setStations( ArrayList<RadioStation> stations)
    {
        this.stations = stations;
    }

    @Expose
    @SerializedName("color")
    private int color;
    public int getColor () { return color; }
    public void setColor(int color) { this.color = color; }
}
