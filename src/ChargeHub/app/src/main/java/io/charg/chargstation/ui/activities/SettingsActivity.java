package io.charg.chargstation.ui.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.DialogHelper;
import io.charg.chargstation.services.SettingsProvider;
import io.charg.chargstation.services.StringHelper;
import io.charg.chargstation.ui.dialogs.EditTextDialog;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_smart_contract_address)
    TextView mTvContractAddress;

    @BindView(R.id.rbtn_network_main)
    RadioButton mRbtnNetworkMain;

    @BindView(R.id.rbtn_network_test)
    RadioButton mRbtnNetworkTest;

    @BindView(R.id.tv_gas_limit)
    TextView mTvGasLimit;

    @BindView(R.id.tv_gas_price)
    TextView mTvGasPrice;

    @BindView(R.id.tv_max_fee)
    TextView mTvMaxFee;

    private SettingsProvider mSettingsProvider;

    @Override
    public int getResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
    }

    private void initServices() {
        mSettingsProvider = new SettingsProvider(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        mTvContractAddress.setText(StringHelper.getShortEthAddress(mSettingsProvider.getContractAddress()));
        mRbtnNetworkMain.setChecked(mSettingsProvider.getNetwork());
        mRbtnNetworkTest.setChecked(!mSettingsProvider.getNetwork());
        mTvGasLimit.setText(String.valueOf(mSettingsProvider.getGasLimit()));
        mTvGasPrice.setText(String.format(Locale.getDefault(), "%s GWEI (%s ETH)",
                Convert.fromWei(String.valueOf(mSettingsProvider.getGasPrice()), Convert.Unit.GWEI),
                Convert.fromWei(String.valueOf(mSettingsProvider.getGasPrice()), Convert.Unit.ETHER).toPlainString()));
        mTvMaxFee.setText(String.format(Locale.getDefault(), "%s ETH",
                Convert.fromWei(BigDecimal.valueOf(mSettingsProvider.getGasLimit().longValue() * mSettingsProvider.getGasPrice().longValue()), Convert.Unit.ETHER)));
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @OnClick(R.id.rbtn_network_main)
    void onBtnNetworkMainClicked() {
        mSettingsProvider.setNetwork(true);
        refreshUI();
    }

    @OnClick(R.id.rbtn_network_test)
    void onBtnNetworkTestClicked() {
        mSettingsProvider.setNetwork(false);
        refreshUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_edit_contract_address)
    void onBtnEditClicked() {
        EditTextDialog dlg = new EditTextDialog(this, getString(R.string.smart_contract_address), mSettingsProvider.getContractAddress());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                mSettingsProvider.setContractAddress(result);
                onSettingsChanged();

            }
        });
        dlg.show();
    }

    @OnClick(R.id.btn_load_camera)
    void onBtnLoadCameraClicked() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        operateQrCodeResult(requestCode, resultCode, data);
    }

    private void operateQrCodeResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            mSettingsProvider.setContractAddress(result.getContents());
            onSettingsChanged();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onSettingsChanged() {
        refreshUI();
        Snackbar.make(mTvContractAddress, "Settings updated", Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_load_default_contract_address)
    void onBtnLoadDefaultClicked() {
        DialogHelper.showQuestion(this, "Do you want load defaults?", new Runnable() {
            @Override
            public void run() {
                mSettingsProvider.setContractAddress(CommonData.SMART_CONTRACT_ADDRESS);
                onSettingsChanged();
            }
        });
    }

    @OnClick(R.id.btn_edit_gas_limit)
    void onBtnEditGasLimitClicked() {
        EditTextDialog dlg = new EditTextDialog(this, getString(R.string.gas_limit), mSettingsProvider.getGasLimit().toString());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                mSettingsProvider.setGasLimit(Long.valueOf(result));
                onSettingsChanged();
            }
        });
        dlg.setNumberRange(100000, 30000);
        dlg.show();
    }

    @OnClick(R.id.btn_load_default_gas_limit)
    void onBtnLoadDefaultGasLimitClicked() {
        DialogHelper.showQuestion(this, "Do you want load defaults?", new Runnable() {
            @Override
            public void run() {
                mSettingsProvider.setGasLimit(SettingsProvider.DEFAULT_GAS_LIMIT);
                onSettingsChanged();
            }
        });
    }

    @OnClick(R.id.btn_edit_gas_price)
    void onBtnEditGasPriceClicked() {
        EditTextDialog dialog = new EditTextDialog(this, getString(R.string.gas_price),
                String.valueOf(Convert.fromWei(mSettingsProvider.getGasPrice().toString(), Convert.Unit.GWEI)));

        dialog.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                mSettingsProvider.setGasPrice(Convert.toWei(result, Convert.Unit.GWEI).longValue());
                onSettingsChanged();
            }
        });

        dialog.setNumberRange(99, 1);
        dialog.show();
    }

    @OnClick(R.id.btn_load_default_gas_price)
    void onBtnLoadDefaultGasPriceClicked() {
        DialogHelper.showQuestion(this, "Do you want load defaults?", new Runnable() {
            @Override
            public void run() {
                mSettingsProvider.setGasPrice(SettingsProvider.DEFAULT_GAS_PRICE);
                onSettingsChanged();
            }
        });
    }
}
