package io.charg.chargstation.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by oleg on 04.11.2017.
 */

public abstract class BaseFragment extends Fragment {

    protected abstract int getResourceId();

    protected abstract void onShows();

    public abstract CharSequence getTitle();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getResourceId(), container, false);
        ButterKnife.bind(this, view);
        onShows();
        return view;
    }

}
