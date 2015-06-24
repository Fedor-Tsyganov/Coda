package com.fedortsyganov.iptest.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fedortsyganov.iptest.R;
import com.fedortsyganov.iptest.fragments.FragmentRadioList;
import com.fedortsyganov.iptest.helpers.PlaylistHelper;
import com.fedortsyganov.iptest.objects.Playlist;
import com.fedortsyganov.iptest.objects.RadioStation;

import java.util.ArrayList;

/**
 * Created by fedortsyganov on 4/13/15.
 */
public class DialogPlaylistAction extends Dialog
{
    private Button bCancel, bAdd, bRemove;
    private Activity activity;
    private RelativeLayout relativeLayoutMain, relativeLayoutButtons;
    private int mPosition;
    private Animation ani, ani2; //animation one and two
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;
    private static final String STATION_TO_SAVE = "info";
    private RadioStation station;
    private ArrayList <Integer> positionPL;
    private ArrayList <Integer> positionInPL;
    private ArrayList <Boolean> statuses;

    public DialogPlaylistAction(Activity a, int position, RadioStation station)
    {
        super(a);
        activity = a;
        mPosition = position;
        this.station = station;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_action_playlist);

        positionPL = new ArrayList<>();
        positionInPL = new ArrayList<>();
        relativeLayoutMain = (RelativeLayout) findViewById(R.id.rlDialogPlaylistAction);
        relativeLayoutButtons = (RelativeLayout) findViewById(R.id.rlDialogPlaylistActionButtons);
        //relativeLayoutMain.setLayoutParams();
        ani = new ShowAnim(relativeLayoutMain, dp2px(240)/* target layout height */, dp2px(0));
        ani2 = new ShowAnim(relativeLayoutMain, dp2px(240)/* target layout height */, dp2px(240));
        ani2.setDuration(670/* animation time */);
        ani2.setFillAfter(true);
        ani2.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                relativeLayoutButtons.setVisibility(View.VISIBLE);
                relativeLayoutButtons.animate().scaleY(1.0f).alpha(1.0f).translationY(0).setDuration(400).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {}

        });
        ani.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                relativeLayoutButtons.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                relativeLayoutMain.animate().translationY(0).setDuration(650).start();
                relativeLayoutButtons.setTranslationY(-500.0f);
                relativeLayoutButtons.setScaleY(0.2f);
                relativeLayoutButtons.setAlpha(0f);
                relativeLayoutButtons.startAnimation(ani2);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {}
        });
        ani.setDuration(0/* animation time */);
        ani.setFillAfter(true);

        relativeLayoutMain.setTranslationY(1500.0f);
        relativeLayoutMain.startAnimation(ani);

        bAdd = (Button) relativeLayoutButtons.findViewById(R.id.bPlaylistActionAdd);
        /*ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                RadialGradient lg = new RadialGradient(0, 0, bAdd.getWidth(),new int[] {
                        0xFF1e5799,
                        0xFF207cca,
                        0xFF2989d8,
                        0xFF207cca }, //substitute the correct colors for these
                        new float[] {
                                0f, 0.40f, 0.60f, 1f },
                        Shader.TileMode.CLAMP);
                return lg;
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        bAdd.setBackground((Drawable)p);*/
        bRemove = (Button) relativeLayoutButtons.findViewById(R.id.bPlaylistActionRemove);
        bRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (inPlaylist(station))
                {
                    dismiss();
                    showDialogRemove();
                    //Toast.makeText(getContext(), "In playlists: " + name.get(0) + " and " + name.get(1), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getContext(), ""+activity.getString(R.string.dialog_remove_not_in_pl), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                showDialog();
            }
        });
        bCancel = (Button) relativeLayoutButtons.findViewById(R.id.bPlaylistActionCancel);
        bCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }

    void showDialog()
    {
        DialogPlaylistSelector dialogPlaylistSelector = new DialogPlaylistSelector();
        dialogPlaylistSelector.setPostion(mPosition);
        dialogPlaylistSelector.setAction(false);
        dialogPlaylistSelector.show(activity.getFragmentManager(), "dialog_playlist_selector");
    }
    void showDialogRemove()
    {
        DialogPlaylistSelector dialogPlaylistSelector = new DialogPlaylistSelector();
        dialogPlaylistSelector.setPostion(mPosition);
        dialogPlaylistSelector.setAction(true);
        dialogPlaylistSelector.setRemoveLists(positionPL, positionInPL);
        dialogPlaylistSelector.show(activity.getFragmentManager(), "dialog_playlist_selector");
    }

    private int dp2px(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public class ShowAnim extends Animation
    {
        int targetHeight;
        int initialHeight = 0;
        View view;

        public ShowAnim(View view, int targetHeight, int initialHeight)
        {
            this.view = view;
            this.targetHeight = targetHeight;
            this.initialHeight = initialHeight;
        }
        public ShowAnim(View view, int targetHeight)
        {
            this.view = view;
            this.targetHeight = targetHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            view.getLayoutParams().height = (int) ((targetHeight-initialHeight) * interpolatedTime) + initialHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight)
        {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds()
        {
            return true;
        }
    }

    public boolean inPlaylist(RadioStation station)
    {
        statuses = new ArrayList<>();
        Context context = activity.getApplicationContext();
        preferences = context.getSharedPreferences(STATION_TO_SAVE, context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        ArrayList <Playlist> playlists = PlaylistHelper.getPlaylists(context, preferences, prefEditor);
        if (playlists != null)
        {
            int playListSize = playlists.size();
            for (int i = 0; i < playListSize; i++)
            {
                if (playlists.get(i) != null)
                {
                    ArrayList<RadioStation> stations = playlists.get(i).getStations();

                    int stationsSize = stations.size();
                    for (int j = 0; j < stationsSize; j++)
                    {
                        if (FragmentRadioList.sameAs(station, stations.get(j)))
                        {
                            positionPL.add(i);
                            positionInPL.add(j);
                            statuses.add(true);
                        }
                    }
                }
            }
        }

        if (statuses.size() > 0)
            return true;
        else
            return false;
    }

}
