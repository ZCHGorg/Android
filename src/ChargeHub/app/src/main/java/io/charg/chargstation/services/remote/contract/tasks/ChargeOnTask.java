package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class ChargeOnTask extends ChgAsyncTask<TransactionReceipt> {

    private BigInteger mTime;
    private String mAddress;

    public ChargeOnTask(Activity activity, String address, BigInteger time) {
        super(activity);
        mTime = time;
        mAddress = address;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                invokeOnPrepare();
                try {
                    TransactionReceipt result = mContract.chargeOn(mAddress, mTime).sendAsync().get();
                    invokeOnComplete(result);
                } catch (InterruptedException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                }
                invokeOnFinish();
            }
        });
    }
}
