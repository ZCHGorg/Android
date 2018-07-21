package io.charg.chargstation.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.helpers.DialogHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.dialogs.EditTextDialog;

public class SelectDestinationFrg extends BaseNavFragment {

    private static final String KEY_ADDRESS = "KEY_ADDRESS";
    private String mEthAddress;

    @BindView(R.id.tv_eth_address)
    TextView mTvEthAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mEthAddress = args.getString(KEY_ADDRESS);
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_selest_destination;
    }

    @Override
    protected void onShows() {
        refreshUI();
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(R.string.destination);
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

    public String getDestinationEth() {
        return mEthAddress;
    }

    @Override
    public boolean canBack() {
        return false;
    }

    @Override
    public boolean canNext() {
        return true;
    }

    @Override
    public boolean isValid() {
        boolean valid = !(mEthAddress == null || mEthAddress.isEmpty());
        if (!valid) {
            Snackbar.make(mTvEthAddress, R.string.eth_address_empty, Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }

    @Override
    public void invalidate() {
        refreshUI();
    }

    public static SelectDestinationFrg newInstance(String address) {
        SelectDestinationFrg frg = new SelectDestinationFrg();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS, address);
        frg.setArguments(bundle);
        return frg;
    }
}
