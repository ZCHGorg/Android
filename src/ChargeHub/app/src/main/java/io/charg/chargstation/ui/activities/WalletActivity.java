package io.charg.chargstation.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.AccountService;
import io.charg.chargstation.services.ChargCoinContract;
import io.charg.chargstation.ui.dialogs.SendChargDialog;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * Created by worker on 13.11.2017.
 */

public class WalletActivity extends BaseActivity {

    private AccountService mAccountService;

    @BindView(R.id.tv_eth_address)
    TextView tvEthAddress;

    @BindView(R.id.tv_balance_eth)
    TextView tvBalanceEth;

    @BindView(R.id.tv_balance_chg)
    TextView tvBalanceChg;

    @BindView(R.id.btn_change_wallet)
    Button btnChangeWallet;

    @BindView(R.id.progress_balance)
    ProgressBar prBalance;

    @BindView(R.id.layout_balance)
    ViewGroup ltBalance;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public int getResourceId() {
        return R.layout.activity_wallet;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void initServices() {
        mAccountService = new AccountService(this);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadBalanceAsync() {
        new LoadBalanceAsyncTask().execute();
    }

    private void refreshUI() {
        String ethAddress = mAccountService.getEthAddress();
        if (ethAddress != null) {
            tvEthAddress.setText(ethAddress);
            loadBalanceAsync();
        } else {
            tvEthAddress.setText(R.string.not_defined);
            tvBalanceChg.setText(R.string.not_defined);
            tvBalanceEth.setText(R.string.not_defined);
        }
    }

    @OnClick(R.id.btn_copy_clipboard)
    void onBtnCopyClipboardClicked() {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("address", mAccountService.getEthAddress());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.eth_address_copied, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_change_wallet)
    void onBtnChangeWalletClicked() {
        startActivity(new Intent(WalletActivity.this, ChangeWalletActivity.class));
    }

    @OnClick(R.id.btn_buy_charg)
    void onBtnBuyChargClicked() {
        Snackbar.make(btnChangeWallet, R.string.not_implemented_yet, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_buy_eth)
    void onBtnBuyEthClicked() {
        Snackbar.make(btnChangeWallet, R.string.not_implemented_yet, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_send_charg)
    void onBtnSendChargClicked() {
        SendChargDialog dialog = new SendChargDialog(this);
        dialog.sendCharg(getString(R.string.eth_test_address), 0.75);
    }

    @OnClick(R.id.btn_send_eth)
    void onBtnSendEthClicked() {
        Snackbar.make(btnChangeWallet, R.string.not_implemented_yet, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class LoadBalanceAsyncTask extends AsyncTask<Object, Object, LoadBalanceAsyncTask.ViewModel> {

        final Web3j web3 = Web3jFactory.build(new HttpService(CommonData.ETH_URL));

        class ViewModel {
            BigInteger chgBalance;
            BigInteger ethBalance;

            public ViewModel(BigInteger chgBalance, BigInteger ethBalance) {
                this.chgBalance = chgBalance;
                this.ethBalance = ethBalance;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ltBalance.setVisibility(View.INVISIBLE);
            prBalance.setVisibility(View.VISIBLE);
        }

        @Override
        protected ViewModel doInBackground(Object... objects) {
            try {
                ChargCoinContract contract = ChargCoinContract.load(CommonData.SMART_CONTRACT_ADDRESS, web3, Credentials.create(mAccountService.getPrivateKey()), GAS_PRICE, CommonData.GAS_LIMIT_BIG);
                BigInteger chgBalance = contract.balanceOf(mAccountService.getEthAddress()).sendAsync().get();
                BigInteger ethBalance = web3.ethGetBalance(mAccountService.getEthAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();

                Log.v(CommonData.TAG, String.valueOf(chgBalance));
                Log.v(CommonData.TAG, String.valueOf(contract.name().sendAsync().get()));
                Log.v(CommonData.TAG, String.valueOf(ethBalance));

                return new ViewModel(chgBalance, ethBalance);
            } catch (Exception ex) {
                Log.v(CommonData.TAG, ex.getMessage());
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ViewModel vm) {
            super.onPostExecute(vm);

            ltBalance.setVisibility(View.VISIBLE);
            prBalance.setVisibility(View.INVISIBLE);

            if (vm == null) {
                Snackbar.make(btnChangeWallet, R.string.err_loading_balance, Snackbar.LENGTH_SHORT).show();
                return;
            }

            String chgBalance = String.format("%.3f", vm.chgBalance.floatValue() / 1E18);
            tvBalanceChg.setText(chgBalance);

            String ethBalance = String.format("%.3f", Convert.fromWei(BigDecimal.valueOf(vm.ethBalance.doubleValue()), Convert.Unit.ETHER));
            tvBalanceEth.setText(ethBalance);
        }
    }
}
