package io.charg.chargstation.ui.activities.sendChargActivity.fragments;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.services.remote.contract.tasks.SendChgTask;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SendingFrg extends BaseFragment {

    private String mAddress;
    private BigInteger mAmount;
    private ICallbackOnComplete<TransactionReceipt> mCompleteListener;
    private ICallbackOnError<String> mErrorListener;

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_sending;
    }

    @Override
    protected void onShows() {

    }

    @Override
    public CharSequence getTitle() {
        return getString(R.string.sending);
    }

    public void executeAsync() {
        SendChgTask task = new SendChgTask(getActivity(), mAddress, mAmount);
        task.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                invokeOnComplete(result);
            }
        });
        task.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                invokeOnError(message);
            }
        });
        task.executeAsync();
    }

    private void invokeOnComplete(TransactionReceipt result) {
        if (mCompleteListener != null) {
            mCompleteListener.onComplete(result);
        }
    }

    private void invokeOnError(String error) {
        if (mErrorListener != null) {
            mErrorListener.onError(error);
        }
    }

    public void setCompleteListener(ICallbackOnComplete<TransactionReceipt> completeListener) {
        this.mCompleteListener = completeListener;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public void setAmount(BigInteger amount) {
        this.mAmount = amount;
    }

    public void setErrorListener(ICallbackOnError<String> errorListener) {
        this.mErrorListener = errorListener;
    }
}
