package com.lchpartners.fragments;

/**
 * Created by Gwangrae Kim on 2014-08-31.
 */
public interface ActionBarUpdater {
    /**
     * This method updates the current activity's actionbar according to the purpose of current fragment.
     * This method must not be called before this fragment instance is attached to activity.
     */
    public void updateActionBar();
    public void setUpdateActionBarOnCreateView();
}
