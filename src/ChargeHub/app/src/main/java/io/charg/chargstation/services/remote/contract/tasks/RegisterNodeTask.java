package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class RegisterNodeTask extends ChgAsyncTask<TransactionReceipt> {

    private BigInteger mChargingRate;
    private BigInteger mParkingRate;

    public RegisterNodeTask(Activity activity, BigInteger chargingRate, BigInteger parkingRate) {
        super(activity);
        mChargingRate = chargingRate;
        mParkingRate = parkingRate;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                invokeOnPrepare();

                try {
                    TransactionReceipt result = mContract.registerNode(mChargingRate, mParkingRate).sendAsync().get();
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
