package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.concurrent.ExecutionException;

public class ParkingOffTask extends ChgAsyncTask<TransactionReceipt> {

    private String mAddress;

    public ParkingOffTask(Activity activity, String address) {
        super(activity);
        mAddress = address;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                invokeOnPrepare();
                try {
                    TransactionReceipt result = mContract.parkingOff(mAddress).sendAsync().get();
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
