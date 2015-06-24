package com.fedortsyganov.iptest.dialogs;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.helpers.CircularSeekBar;
import com.fedortsyganov.iptest.helpers.Debuger;
import com.fedortsyganov.iptest.receivers.RadioTimerReceiver;

/**
 * Created by fedortsyganov on 4/5/15.
 */
public class DialogSetTimer extends DialogFragment
{
    private TextView textViewTime;
    private CircularSeekBar seekbar;
    private String text;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_timer, null);
        final String min = getString(R.string.dialog_timer_min);
        textViewTime = (TextView) dialogView.findViewById(R.id.textViewTime);
        seekbar = (CircularSeekBar) dialogView.findViewById(R.id.rotationBar);
        seekbar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser)
            {
                int progres = circularSeekBar.getProgress();
                text = min;
                textViewTime.setText(progres+" "+text);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar)
            {
                int progress = seekBar.getProgress();
                text = min;
                textViewTime.setText(progress+" "+text);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar)
            {

            }
        });
        builder.setView(dialogView)
                .setPositiveButton(getString(R.string.dialog_set_timer), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //String str = profileEditor.getText().toString();
                        //Properties.PROFILE_INFORMATION = str;
                        if (Debuger.DEBUG)
                            Log.v("SleepTimer", "setTimer Dialog, time:" + seekbar.getProgress());
                        RadioTimerReceiver timerReceiver = new RadioTimerReceiver();
                        timerReceiver.setTimer(getActivity().getApplicationContext(), seekbar.getProgress());
                        dismiss();
                    }
                })

                .setNegativeButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int style = android.support.v4.app.DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }
}