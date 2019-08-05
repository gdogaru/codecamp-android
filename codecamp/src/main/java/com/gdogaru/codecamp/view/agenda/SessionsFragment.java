/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.gdogaru.codecamp.view.agenda;

import com.evernote.android.state.State;
import com.gdogaru.codecamp.repository.BookmarkRepository;
import com.gdogaru.codecamp.view.BaseFragment;

import javax.inject.Inject;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
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
