package com.gdogaru.codecamp.view.agenda;

import com.gdogaru.codecamp.svc.BookmarkingService;
import com.gdogaru.codecamp.view.BaseFragment;

import javax.inject.Inject;

/**
 * Created by Gabriel on 11/7/2017.
 */

public abstract class SessionsFragment extends BaseFragment {
    private boolean favoritesOnly = false;

    @Inject
    protected BookmarkingService bookmarkingService;

    public void setFavoritesOnly(Boolean favoritesOnly) {
        this.favoritesOnly = favoritesOnly;
        updateDisplay();
    }

    public Boolean getFavoritesOnly() {
        return favoritesOnly;
    }

    public abstract void updateDisplay();
}
