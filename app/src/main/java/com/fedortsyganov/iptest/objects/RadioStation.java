package com.fedortsyganov.iptest.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class RadioStation implements Parcelable
{
    public RadioStation(){}
    public RadioStation(Parcel in)
    {
        name = in.readString();
        url = in.readString();
        genre = in.readString();
        country = in.readString();
        color = in.readInt();
    }
    public RadioStation(String name, String url, String genre, String country, Boolean header, int color)
    {
        this.name = name;
        this.url = url;
        this.genre = genre;
        this.country = country;
        this.header = header;
        this.color = color;
    }
    public RadioStation(boolean header, String country)
    {
        this.header = header;
        this.country = country;
    }

    @Expose
    @SerializedName("name")
    private String name = "";
    public String getStationName()
    {
        return name;
    }
    public void seStationName(String name)
    {
        this.name = name;
    }

    @Expose
    @SerializedName("url")
    private String url = "";
    public String getStationUrl()
    {
        return url;
    }
    public void setStationUrl(String url)
    {
        this.url = url;
    }

    @Expose
    @SerializedName("genre")
    private String genre = "";
    public String getStationGanre()
    {
        return genre;
    }
    public void setStationGenre(String genre)
    {
        this.genre = genre;
    }

    //for search in english if language is different from eng
    @Expose
    @SerializedName("countryDefLang")
    private String countryDefLang= "";
    public String getStationCountryDefLang()
    {
        return countryDefLang;
    }
    public void setStationCountryDefLang(String country)
    {
        this.countryDefLang = country;
    }

    @Expose
    @SerializedName("country")
    private String country= "";
    public String getStationCountry()
    {
        return country;
    }
    public void setStationCountry(String country)
    {
        this.country = country;
    }

    @Expose
    @SerializedName("header")
    private Boolean header;
    public Boolean isHeader()
    {
        return header;
    }
    public void setHeader(Boolean header)
    {
        this.header = header;
    }

    @Expose
    @SerializedName("color")
    private int color;
    public int getColor () { return color; }
    public void setColor(int color) { this.color = color; }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(color);
        dest.writeString(url);
        dest.writeString(genre);
        dest.writeString(country);
        dest.writeString(name);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {

        @Override
        public RadioStation createFromParcel(Parcel source)
        {
            return new RadioStation(source);
        }

        @Override
        public RadioStation [] newArray(int size)
        {
            return new RadioStation[size];
        }
    };
}
