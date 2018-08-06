package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.web3j.tuples.generated.Tuple6;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import io.charg.chargstation.services.remote.contract.dto.SwitchesDto;

public class GetChargingSwitchesTask extends ChgAsyncTask<SwitchesDto> {

    private String mAddress;

    public GetChargingSwitchesTask(Activity activity, String address) {
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
                    Tuple6<String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger> result = mContract.chargingSwitches(mAddress).sendAsync().get();
                    invokeOnComplete(new SwitchesDto(result));
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
