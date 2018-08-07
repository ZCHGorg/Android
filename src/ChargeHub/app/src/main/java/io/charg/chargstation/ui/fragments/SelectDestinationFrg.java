package io.charg.chargstation.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.crypto.Credentials;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.dialogs.EditTextDialog;

public class SelectDestinationFrg extends BaseNavFragment {

    public static final String QR_MODE_SCAN_ETH = "QR_MODE_SCAN_ETH";

    private String mQrMode;

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
        return R.layout.frg_send_charg_select_destination;
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

    @OnClick(R.id.btn_load_from_qr)
    void onBtnLoadFromQrClicked() {
        mQrMode = QR_MODE_SCAN_ETH;
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        operateQrCodeResult(requestCode, resultCode, data);
    }

    private void operateQrCodeResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                if (mQrMode.equals(QR_MODE_SCAN_ETH)) {
                    String ethAddress = result.getContents();
                    if (!ethAddress.contains("0x")) {
                        Toast.makeText(getContext(), ethAddress + " - is not valid eth address", Toast.LENGTH_SHORT).show();
                    } else {
                        mEthAddress = ethAddress;
                    }
                }
            }
            refreshUI();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
