package io.charg.chargstation.ui.activities;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

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
import io.charg.chargstation.services.remote.contract.models.ChargingSwitchesDto;
import io.charg.chargstation.services.remote.contract.tasks.ChargeOffTask;
import io.charg.chargstation.services.remote.contract.tasks.ChargeOnTask;
import io.charg.chargstation.services.remote.contract.tasks.GetAuthorizeTask;
import io.charg.chargstation.services.remote.contract.tasks.GetChargingSwitchesTask;
import io.charg.chargstation.services.remote.contract.tasks.GetRateOfCharging;
import io.charg.chargstation.services.remote.contract.tasks.RegisterNodeTask;
import io.charg.chargstation.services.remote.contract.tasks.UpdateChargingRateTask;
import io.charg.chargstation.ui.dialogs.EditNumberDialog;
import io.charg.chargstation.ui.dialogs.EditTextDialog;
import io.charg.chargstation.ui.dialogs.TxWaitDialog;

public class ContractActivity extends BaseAuthActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_status)
    TextView mTvStatus;

    private AccountService mAccountService;

    private TxWaitDialog mDlgLoading;

    private ICallbackOnPrepare mPrepareListener = new ICallbackOnPrepare() {
        @Override
        public void onPrepare() {
            mDlgLoading.show();
        }
    };
    private ICallbackOnFinish mFinishListener = new ICallbackOnFinish() {
        @Override
        public void onFinish() {
            mDlgLoading.dismiss();
        }
    };
    private ICallbackOnError<String> mErrorListener = new ICallbackOnError<String>() {
        @Override
        public void onError(String message) {
            mTvStatus.setText(message);
        }
    };

    @Override
    public int getResourceId() {
        return R.layout.activity_contract;
    }

    @Override
    public void onActivate() {
        initServices();
        initUI();
    }

    private void initUI() {
        initToolbar();

        mDlgLoading = new TxWaitDialog(this);
    }

    private void initServices() {
        mAccountService = new AccountService(this);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_register_node)
    void onBtnRegisterNodeClicked() {
        EditNumberDialog chargingRateDlg = new EditNumberDialog(this, "Charging rate", 150.5);
        chargingRateDlg.setNumberRange(300, 1);
        chargingRateDlg.setOnComplete(new ICallbackOnComplete<Double>() {
            @Override
            public void onComplete(final Double chargeRate) {
                EditNumberDialog parkingRateDialog = new EditNumberDialog(ContractActivity.this, "Parking rate", 100.5);
                parkingRateDialog.setNumberRange(200, 1);
                parkingRateDialog.setOnComplete(new ICallbackOnComplete<Double>() {
                    @Override
                    public void onComplete(Double parkRate) {
                        RegisterNodeTask task = new RegisterNodeTask(
                                ContractActivity.this,
                                ContractHelper.getWeiFromChg(chargeRate),
                                ContractHelper.getWeiFromChg(parkRate));

                        task.setPrepareListener(mPrepareListener);
                        task.setFinishListener(mFinishListener);
                        task.setErrorListener(mErrorListener);
                        task.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
                            @Override
                            public void onComplete(TransactionReceipt result) {
                                mTvStatus.setText(ContractHelper.getStatus(result));
                            }
                        });
                        task.executeAsync();
                    }
                });
                parkingRateDialog.show();
            }
        });
        chargingRateDlg.show();
    }

    @OnClick(R.id.btn_check_authorize)
    void onBtnCheckAuthorizeClicked() {
        EditTextDialog dlg = new EditTextDialog(this, "Address", mAccountService.getEthAddress());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(final String address) {
                GetAuthorizeTask task = new GetAuthorizeTask(ContractActivity.this, address);
                task.setPrepareListener(mPrepareListener);
                task.setFinishListener(mFinishListener);
                task.setErrorListener(mErrorListener);
                task.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
                    @Override
                    public void onComplete(BigInteger result) {
                        String res = StringHelper.getShortEthAddress(address) + " - " + (result.intValue() == 1 ? "Authorized" : "Not authorized");
                        mTvStatus.setText(res);
                    }
                });
                task.executeAsync();
            }
        });
        dlg.show();
    }

    @OnClick(R.id.btn_charging_switches)
    void onBtnChargingSwitchesClicked() {
        EditTextDialog dlg = new EditTextDialog(this, getString(R.string.eth_address), mAccountService.getEthAddress());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                GetChargingSwitchesTask task = new GetChargingSwitchesTask(ContractActivity.this, result);
                task.setPrepareListener(mPrepareListener);
                task.setFinishListener(mFinishListener);
                task.setErrorListener(mErrorListener);
                task.setCompleteListener(new ICallbackOnComplete<ChargingSwitchesDto>() {
                    @Override
                    public void onComplete(ChargingSwitchesDto result) {
                        mTvStatus.setText(result.toString());
                    }
                });
                task.executeAsync();
            }
        });
        dlg.show();
    }

    @OnClick(R.id.btn_charge_on)
    void onBtnChargeOnClicked() {
        final EditTextDialog dlg = new EditTextDialog(this, getString(R.string.eth_address), mAccountService.getEthAddress());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                ChargeOnTask task = new ChargeOnTask(ContractActivity.this, result, BigInteger.valueOf(10));
                task.setPrepareListener(mPrepareListener);
                task.setFinishListener(mFinishListener);
                task.setErrorListener(mErrorListener);
                task.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
                    @Override
                    public void onComplete(TransactionReceipt result) {
                        mTvStatus.setText(ContractHelper.getStatus(result));
                    }
                });
                task.executeAsync();
            }
        });
        dlg.show();
    }

    @OnClick(R.id.btn_charge_off)
    void onBtnChargeOffClicked() {
        final EditTextDialog dlg = new EditTextDialog(this, getString(R.string.eth_address), mAccountService.getEthAddress());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                ChargeOffTask task = new ChargeOffTask(ContractActivity.this, result);
                task.setPrepareListener(mPrepareListener);
                task.setFinishListener(mFinishListener);
                task.setErrorListener(mErrorListener);
                task.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
                    @Override
                    public void onComplete(TransactionReceipt result) {
                        mTvStatus.setText(ContractHelper.getStatus(result));
                    }
                });
                task.executeAsync();
            }
        });
        dlg.show();
    }

    @OnClick(R.id.btn_get_rate_charging)
    void onBtnRateChargingClicked() {
        EditTextDialog dlg = new EditTextDialog(this, getString(R.string.eth_address), mAccountService.getEthAddress());
        dlg.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String address) {
                GetRateOfCharging task = new GetRateOfCharging(ContractActivity.this, address);
                task.setPrepareListener(mPrepareListener);
                task.setFinishListener(mFinishListener);
                task.setErrorListener(mErrorListener);
                task.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
                    @Override
                    public void onComplete(BigInteger result) {
                        mTvStatus.setText(StringHelper.getRateChgStr(ContractHelper.getChgFromWei(result)));
                    }
                });
                task.executeAsync();
            }
        });
        dlg.show();
    }

    @OnClick(R.id.btn_update_rate_charging)
    void onBtnUpdateRateChargingClicked() {
        GetRateOfCharging chargingRateTask = new GetRateOfCharging(this, mAccountService.getEthAddress());
        chargingRateTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
            @Override
            public void onComplete(BigInteger result) {
                EditNumberDialog dlg = new EditNumberDialog(ContractActivity.this, getString(R.string.amount), ContractHelper.getChgFromWei(result));
                dlg.setNumberRange(100, 0);
                dlg.setOnComplete(new ICallbackOnComplete<Double>() {
                    @Override
                    public void onComplete(Double result) {
                        UpdateChargingRateTask updateTask = new UpdateChargingRateTask(ContractActivity.this, ContractHelper.getWeiFromChg(result));
                        updateTask.setPrepareListener(mPrepareListener);
                        updateTask.setFinishListener(mFinishListener);
                        updateTask.setErrorListener(mErrorListener);
                        updateTask.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
                            @Override
                            public void onComplete(TransactionReceipt result) {
                                mTvStatus.setText(ContractHelper.getStatus(result));
                            }
                        });
                        updateTask.executeAsync();
                    }
                });
                dlg.show();
            }
        });
        chargingRateTask.executeAsync();

    }
}
