package com.fedortsyganov.iptest.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fedortsyganov.iptest.R;

public class SettingsToggle extends RelativeLayout implements View.OnClickListener
{

    public FrameLayout layout;
    public View toggleCircleDark, toggleCirclePink, background_oval_off, background_oval_on;
    public int dimen;

    private Boolean _crossfadeRunning = false;
    private SharedPreferences sharedPreferences;
    private ObjectAnimator _oaLeftDark, _oaRightDark, _oaLeftPink, _oaRightPink;
    private String _prefName;

    public SettingsToggle(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        String bgDrawableOff, bgDrawableOn;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_toggle, this, true);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WiFiSettingsToggle);
        _prefName = a.getString(R.styleable.WiFiSettingsToggle_prefName);
        a.recycle();
        //bgDrawableOff = a.getString(R.styleable.WiFiSettingsToggle_oval_background_off);
        //bgDrawableOn = a.getString(R.styleable.WiFiSettingsToggle_oval_background_on);

        background_oval_off = findViewById(R.id.background_oval_off);
        background_oval_on = findViewById(R.id.background_oval_on);
        toggleCircleDark = findViewById(R.id.toggleCircleDark);
        toggleCirclePink = findViewById(R.id.toggleCirclePink);

        layout = (FrameLayout)findViewById(R.id.layout);
        /*
        if (bgDrawableOff != null)
        {
            int id = getResources().getIdentifier(bgDrawableOff, "drawable", context.getPackageName());
            background_oval_off.setBackground(getResources().getDrawable(id));
        }
        if (bgDrawableOn != null)
        {
            int id = getResources().getIdentifier(bgDrawableOn, "drawable", context.getPackageName());
            background_oval_on.setBackground(getResources().getDrawable(id));
        }
        */
        layout.setOnClickListener(this);

        //get a pixel size for a particular dimension - will differ by device according to screen density
        dimen = getResources().getDimensionPixelSize(R.dimen.settings_toggle_width);
        _oaLeftDark = ObjectAnimator.ofFloat(toggleCircleDark, "x", dimen/2, 0).setDuration(250);
        _oaRightDark = ObjectAnimator.ofFloat(toggleCircleDark, "x", 0, dimen/2).setDuration(250);
        _oaLeftPink = ObjectAnimator.ofFloat(toggleCirclePink, "x",dimen/2,0).setDuration(250);
        _oaRightPink = ObjectAnimator.ofFloat(toggleCirclePink, "x", 0, dimen/2).setDuration(250);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_key), Context.MODE_PRIVATE);

        setState();
    }

    public SettingsToggle(Context context) {
        this(context, null);
    }

    public void setState()
    {
        if (isInEditMode()) return; //isInEditMode(): if being rendered in IDE preview, skip code that will break

        if (sharedPreferences.getBoolean(_prefName, false))
        {
            toggleCircleDark.setX(dimen/2);
            toggleCirclePink.setX(dimen/2);
            _crossfadeViews(background_oval_off, background_oval_on, 1);
            _crossfadeViews(toggleCircleDark, toggleCirclePink, 1);
        }
        else
        {
            toggleCircleDark.setX(0);
            toggleCirclePink.setX(0);
            _crossfadeViews(background_oval_on, background_oval_off, 1);
            _crossfadeViews(toggleCirclePink, toggleCircleDark, 1);
        }
    }

    private void _crossfadeViews(final View begin, View end, int duration)
    {
        _crossfadeRunning = true;

        end.setAlpha(0f);
        end.setVisibility(View.VISIBLE);
        end.animate().alpha(1f).setDuration(duration).setListener(null);
        begin.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                begin.setVisibility(View.GONE);
                _crossfadeRunning = false;
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        if (_oaLeftDark.isRunning() || _oaRightDark.isRunning() || _oaLeftPink.isRunning() || _oaRightPink.isRunning() || _crossfadeRunning) return;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean pref = sharedPreferences.getBoolean(_prefName, false);
        if (pref)
        {
            _oaLeftDark.start();
            _crossfadeViews(background_oval_on, background_oval_off, 110);
            _oaLeftPink.start();
            _crossfadeViews(toggleCirclePink, toggleCircleDark, 110);
        }
        else
        {
            _oaRightDark.start();
            _crossfadeViews(background_oval_off, background_oval_on, 400);
            _oaRightPink.start();
            _crossfadeViews(toggleCircleDark, toggleCirclePink, 400);
        }

        editor.putBoolean(_prefName, !pref);
        editor.apply();
    }
}