package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class SendChgTask extends BaseContractTask {

    public SendChgTask(Activity activity) {
        super(activity);
    }

    public void executeAsync(final String to, final BigInteger amount) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final TransactionReceipt result = mContract.transfer(to, amount).sendAsync().get();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCompleteListener.onComplete(result);
                        }
                    });
                } catch (final InterruptedException e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mErrorListener.onError(e.getMessage());
                        }
                    });
                    e.printStackTrace();
                } catch (final ExecutionException e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mErrorListener.onError(e.getMessage());
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }
}
