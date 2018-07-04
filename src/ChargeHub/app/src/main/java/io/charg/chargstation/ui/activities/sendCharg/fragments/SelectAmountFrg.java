package io.charg.chargstation.ui.activities.sendCharg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.ui.dialogs.EditTextDialog;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SelectAmountFrg extends BaseFragment {

    private AccountService mAccountService;

    private static final String KEY_AMOUNT = "KEY_AMOUNT";
    @BindView(R.id.tv_amount)
    TextView mTvAmount;

    private BigInteger mAmount;

    public static SelectAmountFrg newInstance(BigInteger amount) {
        SelectAmountFrg frg = new SelectAmountFrg();
        Bundle args = new Bundle();
        args.putString(KEY_AMOUNT, amount.toString());
        frg.setArguments(args);
        return frg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initServices();

        Bundle args = getArguments();
        if (args != null) {
            mAmount = new BigInteger(args.getString(KEY_AMOUNT));
        }
    }

    private void initServices() {
        mAccountService = new AccountService(getContext());
    }

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_select_amount;
    }

    @Override
    protected void onExecute() {
        refreshUI();
    }

    private void refreshUI() {
        mTvAmount.setText(String.valueOf(mAmount.divide(BigInteger.valueOf((long) 1E18))));
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

    @OnClick(R.id.btn_edit)
    void onBtnEditClicked() {
        GetBalanceChgTask balanceTask = new GetBalanceChgTask(getActivity(), mAccountService.getEthAddress());
        balanceTask.setCompleteListener(new ICallbackOnComplete<BigInteger>() {
            @Override
            public void onComplete(BigInteger result) {
                EditTextDialog dlg = new EditTextDialog(getContext(),
                        getString(R.string.amount),
                        String.valueOf(mAmount.divide(BigInteger.valueOf((long) 1E18)).intValue()));
                dlg.setNumberRange(result.divide(BigInteger.valueOf((long) 1E18)).intValue(), 0);
                dlg.setOnComplete(new ICallbackOnComplete<String>() {
                    @Override
                    public void onComplete(String result) {
                        mAmount = new BigInteger(result).multiply(BigInteger.valueOf((long) 1E18));
                        refreshUI();
                    }
                });
                dlg.show();
            }
        });
        balanceTask.executeAsync();

    }
}
