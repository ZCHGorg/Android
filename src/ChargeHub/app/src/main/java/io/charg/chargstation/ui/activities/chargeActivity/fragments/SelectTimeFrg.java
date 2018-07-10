package io.charg.chargstation.ui.activities.chargeActivity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

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
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.services.remote.contract.tasks.GetRateOfCharging;
import io.charg.chargstation.ui.dialogs.EditNumberDialog;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SelectTimeFrg extends BaseFragment {

    private static final String KEY_ADDRESS = "KEY_ADDRESS";
    private String mNodeAddress;

    private static final String KEY_TIME = "KEY_TIME";
    private BigInteger mTime;

    private BigInteger mCost;

    @BindView(R.id.tv_time)
    TextView mTvTime;

    @BindView(R.id.tv_cost)
    TextView mTvCost;

    @BindView(R.id.tv_balance_chg)
    TextView mTvBalanceChg;

    private AccountService mAccountService;

    @Override
    protected int getResourceId() {
        return R.layout.frg_select_time;
    }

    public static SelectTimeFrg newInstance(String address) {
        SelectTimeFrg frg = new SelectTimeFrg();
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
            mTime = BigInteger.valueOf(args.getLong(KEY_TIME, 0));
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

        final GetRateOfCharging getRateTask = new GetRateOfCharging(getActivity(), mNodeAddress);
        getRateTask.setPrepareListener(new ICallbackOnPrepare() {
            @Override
            public void onPrepare() {
                mTvCost.setText(R.string.loading);
            }
        });
        getRateTask.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                mTvCost.setText(message);
            }
        });
        getRateTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
            @Override
            public void onComplete(BigInteger result) {
                mTvCost.setText(StringHelper.getBalanceChgStr(ContractHelper.getChgFromWei(mCost = mTime.multiply(result))));
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
                mTvBalanceChg.setText(StringHelper.getBalanceChgStr(ContractHelper.getChgFromWei(result)));
                getRateTask.executeAsync();
            }
        });
        getBalanceTask.executeAsync();

    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    public boolean isValid() {
        boolean valid = mTime.doubleValue() > 0;
        if (!valid) {
            Toast.makeText(getActivity(), R.string.time_not_defined, Toast.LENGTH_SHORT).show();
        }
        return valid;
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
