package io.charg.chargstation.ui.activities.sendCharg.fragments;

import io.charg.chargstation.R;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class ConfirmFrg extends BaseFragment {

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_confirm;
    }

    @Override
    protected void onExecute() {

    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.confirm);
    }
}
