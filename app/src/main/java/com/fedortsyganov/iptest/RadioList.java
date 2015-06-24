package com.fedortsyganov.iptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class RadioList extends ArrayAdapter <String>
{
    public RadioList(Context context, ArrayList<String> users)
    {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String radiostation = getItem(position);
        convertView = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.custom_list_view, parent, false);
        TextView tv = (TextView) convertView.findViewById(R.id.textView777);
        tv.setText(radiostation);
        return convertView;
    }
}
