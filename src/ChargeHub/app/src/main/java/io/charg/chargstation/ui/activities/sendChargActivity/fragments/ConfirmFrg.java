package io.charg.chargstation.ui.activities.sendChargActivity.fragments;

import android.widget.TextView;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.SettingsProvider;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class ConfirmFrg extends BaseFragment {

    private String mFrom;
    private String mTo;
    private BigInteger mAmount;
    private SettingsProvider mSettingsProvider;

    @BindView(R.id.tv_from)
    TextView mTvFrom;

    @BindView(R.id.tv_to)
    TextView mTvTo;

    @BindView(R.id.tv_amount)
    TextView mTvAmount;

    @BindView(R.id.tv_gas_limit)
    TextView mTvGasLimit;

    @BindView(R.id.tv_gas_price)
    TextView mTvGasPrice;

    @BindView(R.id.tv_max_fee)
    TextView mTvMaxFee;


    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_confirm;
    }

    @Override
    protected void onShows() {
        mSettingsProvider = new SettingsProvider(getContext());
        refreshUI();
    }

    public void invalidate() {
        refreshUI();
    }

    private void refreshUI() {
        mTvGasLimit.setText(String.format(Locale.getDefault(), "%d units", mSettingsProvider.getGasLimit().longValue()));
        mTvGasPrice.setText(String.format(Locale.getDefault(), "%d WEI per unit", mSettingsProvider.getGasPrice()));
        mTvMaxFee.setText(String.format(Locale.getDefault(), "%f ETH",
                Convert.fromWei(new BigDecimal(mSettingsProvider.getGasLimit().multiply(mSettingsProvider.getGasPrice())), Convert.Unit.ETHER)));
        mTvFrom.setText(StringHelper.getShortEthAddress(mFrom));
        mTvTo.setText(StringHelper.getShortEthAddress(mTo));
        mTvAmount.setText(StringHelper.getBalanceChgStr(ContractHelper.getChgFromWei(mAmount)));
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.confirm);
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public void setAmount(BigInteger amount) {
        mAmount = amount;
    }
}
