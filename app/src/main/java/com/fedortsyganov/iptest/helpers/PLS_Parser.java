package com.fedortsyganov.iptest.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fedortsyganov on 3/11/15.
 */
public class PLS_Parser
{
    private final BufferedReader reader;

    public PLS_Parser(String url) throws IOException
    {
        URLConnection urlConnection = new URL(url).openConnection();
        this.reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    }

    public List<String> getUrls()
    {
        LinkedList<String> urls = new LinkedList<String>();
        while (true)
        {
            try
            {
                String line = reader.readLine();
                if (line == null)
                {
                    break;
                }
                String url = parseLine(line);
                if (url != null && !url.equals(""))
                {
                    urls.add(url);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private String parseLine(String line)
    {
        if (line == null)
        {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.indexOf("http") >= 0)
        {
            return trimmed.substring(trimmed.indexOf("http"));
        }
        return "";
    }
}
