package io.charg.chargstation.ui.activities.sendChargActivity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.ui.dialogs.EditNumberDialog;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SelectAmountFrg extends BaseFragment {

    private AccountService mAccountService;

    @BindView(R.id.tv_amount)
    TextView mTvAmount;

    private BigInteger mAmount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initServices();
    }

    private void initServices() {
        mAccountService = new AccountService(getContext());
    }

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_select_amount;
    }

    @Override
    protected void onShows() {
        refreshUI();
    }

    private void refreshUI() {
        mTvAmount.setText(StringHelper.getBalanceChgStr(ContractHelper.getChgFromWei(mAmount)));
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.amount);
    }

    public boolean isValid() {
        return true;
    }

    public BigInteger getAmount() {
        return mAmount;
    }

    public void setAmount(BigInteger value) {
        mAmount = value;
    }

    public void invalidate() {
        refreshUI();
    }

    @OnClick(R.id.btn_edit)
    void onBtnEditClicked() {
        GetBalanceChgTask balanceTask = new GetBalanceChgTask(getActivity(), mAccountService.getEthAddress());
        balanceTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
            @Override
            public void onComplete(BigInteger result) {
                EditNumberDialog dlg = new EditNumberDialog(getContext(), getString(R.string.amount), ContractHelper.getChgFromWei(mAmount));
                dlg.setNumberRange(result.divide(BigInteger.valueOf((long) 1E18)).intValue(), 0);
                dlg.setOnComplete(new ICallbackOnComplete<Double>() {
                    @Override
                    public void onComplete(Double result) {
                        mAmount = ContractHelper.getWeiFromChg(result);
                        refreshUI();
                    }
                });
                dlg.show();
            }
        });
        balanceTask.executeAsync();

    }
}
