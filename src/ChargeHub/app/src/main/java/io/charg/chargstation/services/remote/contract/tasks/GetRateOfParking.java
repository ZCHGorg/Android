package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.math.BigInteger;

public class GetRateOfParking extends ChgAsyncTask<BigInteger> {

    private String mAddress;

    public GetRateOfParking(Activity activity, String address) {
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
                    BigInteger result = mContract.rateOfParking(mAddress).sendAsync().get();
                    invokeOnComplete(result);
                } catch (Exception e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                }

                invokeOnFinish();
            }
        });
    }
}
