package io.charg.chargstation.ui.activities.sendCharg.fragments;

import android.widget.TextView;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SelectAmountFrg extends BaseFragment {

    @BindView(R.id.tv_amount)
    TextView mTvAmount;

    private float mAmount = 1f;

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_select_amount;
    }

    @Override
    protected void onExecute() {
        refreshUI();
    }

    private void refreshUI() {
        mTvAmount.setText(String.valueOf(mAmount));
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.amount);
    }

    public boolean isValid() {
        return true;
    }

    public float getAmount() {
        return mAmount;
    }
}
