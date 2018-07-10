package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class UpdateChargingRateTask extends ChgAsyncTask<TransactionReceipt> {

    private BigInteger mRate;

    public UpdateChargingRateTask(Activity activity, BigInteger rate) {
        super(activity);
        mRate = rate;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                invokeOnPrepare();

                try {
                    TransactionReceipt result = mContract.updateChargingRate(mRate).sendAsync().get();
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
