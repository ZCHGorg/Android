package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class GetAuthorizeTask extends ChgAsyncTask<BigInteger> {

    private String mAddress;

    public GetAuthorizeTask(Activity activity, String address) {
        super(activity);
        this.mAddress = address;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BigInteger result = mContract.authorized(mAddress).sendAsync().get();
                    invokeOnComplete(result);
                } catch (InterruptedException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
