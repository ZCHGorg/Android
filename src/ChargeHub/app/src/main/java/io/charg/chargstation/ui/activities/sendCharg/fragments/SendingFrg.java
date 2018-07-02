package io.charg.chargstation.ui.activities.sendCharg.fragments;

import android.widget.Toast;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.services.remote.contract.tasks.SendChgTask;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SendingFrg extends BaseFragment {

    @Override
    protected int getResourceId() {
        return R.layout.frg_send_charg_sending;
    }

    @Override
    protected void onExecute() {

    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    public void execute() {
        SendChgTask task = new SendChgTask(getActivity());
        task.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                Toast.makeText(getContext(), result.getGasUsed().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        task.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        task.executeAsync("0x08Ad6872F01C309330d997e3A3D0248681C6783a", BigInteger.valueOf((long) (10 * 1E18)));
    }
}
