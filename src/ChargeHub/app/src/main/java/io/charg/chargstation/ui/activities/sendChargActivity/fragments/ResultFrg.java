package io.charg.chargstation.ui.activities.sendChargActivity.fragments;

import android.widget.ImageView;
import android.widget.TextView;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class ResultFrg extends BaseFragment {

    private String mError;
    private TransactionReceipt mResult;

    @BindView(R.id.tv_status)
    TextView mTvStatus;

    @BindView(R.id.iv_status)
    ImageView mIvStatus;

    @BindView(R.id.tv_txhash)
    TextView mTvTxHash;

    @BindView(R.id.tv_from)
    TextView mTvFrom;

    @BindView(R.id.tv_to)
    TextView mTvTo;

    @BindView(R.id.tv_fee)
    TextView mTvFee;

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_result;
    }

    @Override
    protected void onShows() {

    }

    private void refreshUI() {

        mIvStatus.setImageResource(isStatusOK() ? R.drawable.ic_ok : R.drawable.ic_error);

        if (mError != null) {
            mTvStatus.setText(mError);
        } else {
            mTvStatus.setText(isStatusOK() ? "Success" : "Fail");
            mTvTxHash.setText(StringHelper.getShortEthAddress(mResult.getTransactionHash()));
            mTvFrom.setText(StringHelper.getShortEthAddress(mResult.getFrom()));
            mTvTo.setText(StringHelper.getShortEthAddress(mResult.getTo()));
            mTvFee.setText(mResult.getCumulativeGasUsed().toString());
        }
    }

    private boolean isStatusOK() {
        if (mError != null) {
            return false;
        }

        if (mResult == null) {
            return false;
        }

        String status = mResult.getStatus();

        if (null == status) {
            return true;
        }

        BigInteger statusQuantity = Numeric.decodeQuantity(status);
        return BigInteger.ONE.equals(statusQuantity);
    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.result);
    }

    public void setError(String error) {
        this.mError = error;
    }

    public void setResult(TransactionReceipt result) {
        this.mResult = result;
    }

    public void invalidate() {
        refreshUI();
    }
}
