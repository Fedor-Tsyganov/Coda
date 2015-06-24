package com.fedortsyganov.iptest.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.fedortsyganov.iptest.R;

/**
 * Created by fedortsyganov on 4/1/15.
 */
public class DialogDefault extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_default, null);
        builder.setView(dialogView)
                .setPositiveButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //String str = profileEditor.getText().toString();
                        //Properties.PROFILE_INFORMATION = str;
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
