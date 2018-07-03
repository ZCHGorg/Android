package io.charg.chargstation.ui.activities.sendCharg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.services.remote.contract.tasks.SendChgTask;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SendingFrg extends BaseFragment {

    private static final String KEY_ADDRESS = "KEY_ADDRESS";
    private static final String KEY_AMOUNT = "KEY_AMOUNT";

    private String mAddress;
    private BigInteger mAmount;
    private ICallbackOnComplete<TransactionReceipt> mCompleteListener;

    public static SendingFrg newInstance(String address, long amount) {
        SendingFrg frg = new SendingFrg();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS, address);
        bundle.putLong(KEY_AMOUNT, amount);
        frg.setArguments(bundle);
        return frg;
    }

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_sending;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        mAddress = args.getString(KEY_ADDRESS);
        mAmount = BigInteger.valueOf(args.getLong(KEY_AMOUNT));
    }

    @Override
    protected void onExecute() {

    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    public void execute() {
        SendChgTask task = new SendChgTask(getActivity(), mAddress, mAmount);
        task.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                invokeOnComplete(result);
                Toast.makeText(getContext(), result.getGasUsed().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        task.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        task.executeAsync();
    }

    private void invokeOnComplete(TransactionReceipt result) {
        if (mCompleteListener != null) {
            mCompleteListener.onComplete(result);
        }
    }

    public void setCompleteListener(ICallbackOnComplete<TransactionReceipt> completeListener) {
        this.mCompleteListener = completeListener;
    }
}
