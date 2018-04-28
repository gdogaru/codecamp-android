package com.gdogaru.codecamp.view.agenda;

import com.gdogaru.codecamp.svc.BookmarkingService;
import com.gdogaru.codecamp.view.BaseFragment;

import javax.inject.Inject;

import icepick.State;

/**
 * Created by Gabriel on 11/7/2017.
 */

public abstract class SessionsFragment extends BaseFragment {
    @Inject
    protected BookmarkingService bookmarkingService;
    @State
    boolean favoritesOnly = false;

    public Boolean getFavoritesOnly() {
        return favoritesOnly;
    }

    public void setFavoritesOnly(Boolean favoritesOnly) {
        this.favoritesOnly = favoritesOnly;
        if (getView() != null) updateDisplay();
    }

    public abstract void updateDisplay();


}
