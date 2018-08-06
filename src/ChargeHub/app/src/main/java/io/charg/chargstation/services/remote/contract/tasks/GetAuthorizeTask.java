package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import io.charg.chargstation.services.helpers.ContractHelper;

public class GetAuthorizeTask extends ChgAsyncTask<Boolean> {

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
                invokeOnPrepare();
                try {
                    BigInteger result = mContract.authorized(mAddress).sendAsync().get();
                    invokeOnComplete(ContractHelper.getResult(result));
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
