package com.fedortsyganov.iptest.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fedortsyganov.iptest.RadioMainPageActivity;
import com.fedortsyganov.iptest.RadioPlayerActivity;
import com.fedortsyganov.iptest.MusicService;
import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.RadioList;

import java.util.ArrayList;

public class DialogStations extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener
{

    ArrayList <String> listRadios = new ArrayList<String>();
    /*
    static int img [] ={R.drawable.recordbkg_two, R.drawable.hitfmbkg,
            R.drawable.dfmbkg, R.drawable.europabkg,
            R.drawable.defbkg};
            */
    private Activity mActivity;
    private ImageView background;
    private ListView listView;
    private Button bCancel;
    private RelativeLayout relativeLayout;

    public DialogStations(Activity a)
    {
        super(a);
        mActivity = a;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //int style = android.support.v4.app.DialogFragment.STYLE_NO_TITLE, theme = 0;
        //setStyle(style, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_station);
        relativeLayout = (RelativeLayout) findViewById(R.id.rlDialogStations);
        /*
        if (listRadios.size() <= 0 )
        {
            for (int i =0; i < Information.stationList.length; i++)
                listRadios.add(Information.stationList[i]);
        }
        */
        int val = RadioMainPageActivity.currentStationsList.size();
        for (int i = 0; i < val; i++)
        {
            listRadios.add(RadioMainPageActivity.currentStationsList.get(i).getStationName());
        }
        background = (ImageView) relativeLayout.findViewById(R.id.ivBackground);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        listView = (ListView) relativeLayout.findViewById(R.id.listViewStations);
        listView.setAdapter(new RadioList(getContext(), listRadios));
        listView.setOnItemClickListener(this);
        bCancel = (Button) relativeLayout.findViewById(R.id.bCANCEL);
        bCancel.setOnClickListener(this);
        relativeLayout.setTranslationY(-500.0f);
        relativeLayout.setScaleY(0.6f);
        listView.setAlpha(0.0f);
        //listView.setTranslationY(-150.0f);
        relativeLayout.animate().translationY(0).scaleY(1.0f).setDuration(400);
        listView.animate().alpha(1.0f).setDuration(300);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bCANCEL:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //RadioMainPage.position = position;
        RadioMainPageActivity.radioStationPosition = position;
        //FragmentMainPage.updateInfoBox();
        RadioPlayerActivity.updateInfoBox();
        RadioMainPageActivity.radioStation = RadioMainPageActivity.currentStationsList.get(RadioMainPageActivity.radioStationPosition);
        if (MusicService.media != null && (MusicService.media.isPlaying() || !MusicService.media.isPlaying()))
        {
            //RadioMainPage.play.setChecked(false);
            //FragmentMainPage.bPlay.setChecked(false);
            RadioPlayerActivity.bPlay.setChecked(false);
            RadioMainPageActivity.counter++;
            getContext().stopService(new Intent(getContext().getApplicationContext(), MusicService.class));
        }
        dismiss();
    }
}
