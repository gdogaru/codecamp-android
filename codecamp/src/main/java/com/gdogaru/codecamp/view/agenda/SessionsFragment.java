package com.gdogaru.codecamp.view.agenda;

import com.evernote.android.state.State;
import com.gdogaru.codecamp.repository.BookmarkRepository;
import com.gdogaru.codecamp.view.BaseFragment;

import javax.inject.Inject;

/**
 * Created by Gabriel on 11/7/2017.
 */

public abstract class SessionsFragment extends BaseFragment {
    @Inject
    protected BookmarkRepository bookmarkingService;
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
