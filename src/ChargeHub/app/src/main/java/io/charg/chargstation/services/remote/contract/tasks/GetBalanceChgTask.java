package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class GetBalanceChgTask extends BaseContractTask<BigInteger> {

    private String mAddress;

    public GetBalanceChgTask(Activity activity, String mAddress) {
        super(activity);
        this.mAddress = mAddress;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    invokeOnPrepare();
                    final BigInteger result = mContract.balanceOf(mAddress).sendAsync().get();
                    invokeOnComplete(result);
                } catch (final InterruptedException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                } catch (final ExecutionException e) {
                    invokeOnError(e.getMessage());
                    e.printStackTrace();
                } finally {
                    invokeOnFinish();
                }
            }
        });
    }
}
