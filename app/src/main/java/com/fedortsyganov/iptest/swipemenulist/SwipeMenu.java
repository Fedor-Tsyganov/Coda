package com.fedortsyganov.iptest.swipemenulist;

/**
 * Created by fedortsyganov on 3/12/15.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * @author baoyz
 * @date 2014-8-23
 */
public class SwipeMenu
{

    private Context mContext;
    private List<SwipeMenuItem> mItems;
    private int mViewType;
    private int position;
    public SwipeMenu(Context context)
    {
        mContext = context;
        mItems = new ArrayList<SwipeMenuItem>();
    }

    public Context getContext()
    {
        return mContext;
    }

    public void addMenuItem(SwipeMenuItem item)
    {
        mItems.add(item);
    }

    public void removeMenuItem(SwipeMenuItem item)
    {
        mItems.remove(item);
    }

    public List<SwipeMenuItem> getMenuItems()
    {
        return mItems;
    }

    public SwipeMenuItem getMenuItem(int index)
    {
        return mItems.get(index);
    }

    public int getViewType()
    {
        return mViewType;
    }

    public void setViewType(int viewType)
    {
        this.mViewType = viewType;
    }

    public void setPosition(int position) {this.position = position;}
    public int getPosition() {return position; }

}