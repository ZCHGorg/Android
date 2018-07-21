package io.charg.chargstation.ui.activities.parkingActivity;

import io.charg.chargstation.R;
import io.charg.chargstation.ui.fragments.BaseNavFragment;

public class ParkingFrg extends BaseNavFragment {

    @Override
    protected int getResourceId() {
        return R.layout.frg_parking;
    }

    @Override
    protected void onShows() {

    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public boolean canBack() {
        return false;
    }

    @Override
    public boolean canNext() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void invalidate() {

    }
}
