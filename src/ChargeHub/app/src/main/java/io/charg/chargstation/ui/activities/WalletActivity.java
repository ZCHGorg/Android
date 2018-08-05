package io.charg.chargstation.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.local.SettingsProvider;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceEthTask;
import io.charg.chargstation.ui.activities.sendChargActivity.SendChargActivity;

/**
 * Created by worker on 13.11.2017.
 */

public class WalletActivity extends BaseAuthActivity {

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

    private GetBalanceChgTask mGetBalanceChgTask;
    private GetBalanceEthTask mGetBalanceEthTask;

    @Override
    public int getResourceId() {
        return R.layout.activity_wallet;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
    }

    private void initTasks() {
        mGetBalanceChgTask = new GetBalanceChgTask(this, mAccountService.getEthAddress());
        mGetBalanceChgTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
            @Override
            public void onComplete(BigInteger result) {
                tvBalanceChg.setText(String.format(Locale.getDefault(), "%.3f", ContractHelper.getChgFromWei(result)));
            }
        });
        mGetBalanceChgTask.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                Toast.makeText(WalletActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        mGetBalanceChgTask.setPrepareListener(new ICallbackOnPrepare() {
            @Override
            public void onPrepare() {
                ltBalance.setVisibility(View.INVISIBLE);
                prBalance.setVisibility(View.VISIBLE);
            }
        });
        mGetBalanceChgTask.setFinishListener(new ICallbackOnFinish() {
            @Override
            public void onFinish() {
                ltBalance.setVisibility(View.VISIBLE);
                prBalance.setVisibility(View.INVISIBLE);
            }
        });

        mGetBalanceEthTask = new GetBalanceEthTask(this, mAccountService.getEthAddress());
        mGetBalanceEthTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
            @Override
            public void onComplete(BigInteger result) {
                String ethBalance = String.format(Locale.getDefault(), "%.3f", Convert.fromWei(result.toString(), Convert.Unit.ETHER).floatValue());
                tvBalanceEth.setText(ethBalance);
            }
        });
        mGetBalanceEthTask.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                Toast.makeText(WalletActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        mGetBalanceEthTask.setPrepareListener(new ICallbackOnPrepare() {
            @Override
            public void onPrepare() {
                ltBalance.setVisibility(View.INVISIBLE);
                prBalance.setVisibility(View.VISIBLE);
            }
        });
        mGetBalanceEthTask.setFinishListener(new ICallbackOnFinish() {
            @Override
            public void onFinish() {
                ltBalance.setVisibility(View.VISIBLE);
                prBalance.setVisibility(View.INVISIBLE);
            }
        });
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void loadBalanceAsync() {
        mGetBalanceChgTask.executeAsync();
        mGetBalanceEthTask.executeAsync();
    }

    private void refreshUI() {
        String ethAddress = mAccountService.getEthAddress();
        if (ethAddress != null) {
            tvEthAddress.setText(ethAddress);
            initTasks();
            loadBalanceAsync();
        } else {
            tvEthAddress.setText(R.string.not_defined);
            tvBalanceChg.setText(R.string.not_defined);
            tvBalanceEth.setText(R.string.not_defined);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemRefresh = menu.add("Refresh");
        itemRefresh.setIcon(R.drawable.ic_cached);
        itemRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        itemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                refreshUI();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.btn_copy_clipboard)
    void onBtnCopyClipboardClicked() {
        try {
            ClipData clip = ClipData.newPlainText("address", mAccountService.getEthAddress());
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.eth_address_copied, Toast.LENGTH_SHORT).show();
            }
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
        startActivity(new Intent(this, BuyChargActivity.class));
    }

    @OnClick(R.id.btn_buy_eth)
    void onBtnBuyEthClicked() {
        Snackbar.make(btnChangeWallet, R.string.not_implemented_yet, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_send_charg)
    void onBtnSendChargClicked() {
        startActivity(new Intent(this, SendChargActivity.class));
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
}


