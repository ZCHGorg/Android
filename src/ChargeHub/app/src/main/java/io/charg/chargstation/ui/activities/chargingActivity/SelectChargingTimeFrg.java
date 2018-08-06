package io.charg.chargstation.ui.activities.chargingActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.tasks.GetAuthorizeTask;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.services.remote.contract.tasks.GetRateOfCharging;
import io.charg.chargstation.ui.dialogs.EditNumberDialog;
import io.charg.chargstation.ui.fragments.BaseNavFragment;

public class SelectChargingTimeFrg extends BaseNavFragment {

    private static final String KEY_ADDRESS = "KEY_ADDRESS";
    private String mNodeAddress;

    private static final String KEY_TIME = "KEY_TIME";
    private BigInteger mTime;

    private BigInteger mCost;
    private BigInteger mBalance;
    private boolean mValid;

    @BindView(R.id.tv_time)
    TextView mTvTime;

    @BindView(R.id.tv_cost)
    TextView mTvCost;

    @BindView(R.id.tv_balance_chg)
    TextView mTvBalanceChg;

    @BindView(R.id.tv_charging_rate)
    TextView mTvChargingRate;

    @BindView(R.id.iv_status)
    ImageView mIvStatus;

    @BindView(R.id.btn_get_more_chg)
    Button mBtnGetMoreChg;

    @BindView(R.id.tv_status)
    TextView mTvStatus;

    private AccountService mAccountService;

    @Override
    protected int getResourceId() {
        return R.layout.frg_select_charging_time;
    }

    public static SelectChargingTimeFrg newInstance(String address) {
        SelectChargingTimeFrg frg = new SelectChargingTimeFrg();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS, address);
        frg.setArguments(bundle);
        return frg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mNodeAddress = args.getString(KEY_ADDRESS);
            mTime = BigInteger.valueOf(args.getLong(KEY_TIME, 10));
        }
    }

    @Override
    protected void onShows() {
        initServices();
    }

    public void invalidate() {
        refreshUI();
    }

    private void initServices() {
        mAccountService = new AccountService(getContext());
    }

    private void refreshUI() {
        mTvTime.setText(StringHelper.getTimeStr(mTime.longValue()));
        mValid = false;

        GetAuthorizeTask getAuthTask = new GetAuthorizeTask(getActivity(), mNodeAddress);
        getAuthTask.setPrepareListener(new ICallbackOnPrepare() {
            @Override
            public void onPrepare() {
                mTvStatus.setText(R.string.loading);
            }
        });
        getAuthTask.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                mTvStatus.setText(message);
            }
        });
        getAuthTask.setCompleteListener(new ICallbackOnComplete<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                if (!result) {
                    mTvStatus.setText(R.string.node_is_not_auth);
                    mIvStatus.setImageResource(R.drawable.ic_error);
                    return;
                }

                final GetRateOfCharging getRateTask = new GetRateOfCharging(getActivity(), mNodeAddress);
                getRateTask.setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {
                        mTvCost.setText(R.string.loading);
                        mTvChargingRate.setText(R.string.loading);
                    }
                });
                getRateTask.setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                        mTvCost.setText(message);
                        mTvChargingRate.setText(message);
                    }
                });
                getRateTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
                    @Override
                    public void onComplete(BigInteger result) {
                        mTvCost.setText(StringHelper.getBalanceChgStr(ContractHelper.getChgFromWei(mCost = mTime.multiply(result))));
                        mTvChargingRate.setText(StringHelper.getRateChgStr(ContractHelper.getChgFromWei(result)));
                        if (mBalance.compareTo(mCost) < 0) {
                            mValid = false;
                            mTvStatus.setText(R.string.not_enough_chg);
                            mBtnGetMoreChg.setVisibility(View.VISIBLE);
                            mIvStatus.setImageResource(R.drawable.ic_error);
                        } else {
                            mValid = true;
                            mTvStatus.setText(R.string.success);
                            mBtnGetMoreChg.setVisibility(View.INVISIBLE);
                            mIvStatus.setImageResource(R.drawable.ic_ok);
                        }
                    }
                });

                GetBalanceChgTask getBalanceTask = new GetBalanceChgTask(getActivity(), mAccountService.getEthAddress());
                getBalanceTask.setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {
                        mTvBalanceChg.setText(R.string.loading);
                    }
                });
                getBalanceTask.setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                        mTvBalanceChg.setText(message);
                    }
                });
                getBalanceTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
                    @Override
                    public void onComplete(BigInteger result) {
                        mBalance = result;
                        mTvBalanceChg.setText(StringHelper.getBalanceChgStr(ContractHelper.getChgFromWei(result)));
                        getRateTask.executeAsync();
                    }
                });
                getBalanceTask.executeAsync();
            }
        });
        getAuthTask.executeAsync();
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public boolean canNext() {
        return true;
    }

    @Override
    public boolean isValid() {
        return mValid;
    }

    public BigInteger getTimeSeconds() {
        return mTime;
    }

    @OnClick(R.id.btn_edit)
    void onBtnEditClicked() {
        EditNumberDialog dlg = new EditNumberDialog(getContext(), getString(R.string.time), mTime.doubleValue());
        dlg.setOnComplete(new ICallbackOnComplete<Double>() {
            @Override
            public void onComplete(Double result) {
                mTime = BigInteger.valueOf(result.longValue());
                refreshUI();
            }
        });
        dlg.show();
    }

    public void setDestAddress(String destAddress) {
        mNodeAddress = destAddress;
    }
}
