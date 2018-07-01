package io.charg.chargstation.ui.activities.sendCharg.fragments;

import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.StringHelper;
import io.charg.chargstation.ui.dialogs.EditTextDialog;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SelectDestinationFrg extends BaseFragment {

    private String mEthAddress;

    @BindView(R.id.tv_eth_address)
    TextView mTvEthAddress;

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_selest_destination;
    }

    @Override
    protected void onActivate() {
        refreshUI();
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.destination);
    }

    @OnClick(R.id.btn_edit)
    void onBtnEditClicked() {
        EditTextDialog dialog = new EditTextDialog(getContext(), getString(R.string.destination), mEthAddress);
        dialog.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                mEthAddress = result;
                refreshUI();
            }
        });
        dialog.show();
    }

    private void refreshUI() {
        mTvEthAddress.setText(StringHelper.getShortEthAddress(mEthAddress));
    }
}
