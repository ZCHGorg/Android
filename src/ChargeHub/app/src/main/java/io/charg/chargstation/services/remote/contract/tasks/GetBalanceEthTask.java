package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.protocol.core.DefaultBlockParameterName;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class GetBalanceEthTask extends ChgAsyncTask<BigInteger> {

    private final String mAddress;

    public GetBalanceEthTask(Activity activity, String mAddress) {
        super(activity);
        this.mAddress = mAddress;
    }

    @Override
    public void executeAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                invokeOnPrepare();

                try {
                    BigInteger result = mWeb3j.ethGetBalance(mAddress, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
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
