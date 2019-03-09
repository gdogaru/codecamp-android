package com.gdogaru.codecamp.view;

import android.os.Bundle;

import com.gdogaru.codecamp.di.Injectable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.Unbinder;
import icepick.Icepick;

/**
 *
 */
public abstract class BaseFragment extends Fragment implements Injectable {

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) unbinder.unbind();
    }

    protected void manage(Unbinder unbinder) {
        this.unbinder = unbinder;
    }

}
