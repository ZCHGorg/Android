package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class SendChgTask extends ChgAsyncTask<TransactionReceipt> {

    private String mTo;
    private BigInteger mAmount;

    public SendChgTask(Activity activity, String to, BigInteger amount) {
        super(activity);
        this.mTo = to;
        this.mAmount = amount;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                invokeOnPrepare();

                try {
                    final TransactionReceipt result = mContract.transfer(mTo, mAmount).sendAsync().get();
                    invokeOnComplete(result);
                } catch (final InterruptedException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                } catch (final ExecutionException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                }

                invokeOnFinish();
            }
        });
    }
}
