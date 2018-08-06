package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Calendar;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.dto.SwitchesDto;

public class ParkingOnForceTask extends ChgAsyncTask<TransactionReceipt> {

    private String mAddress;
    private BigInteger mTime;

    private AccountService mAccountService;

    private ParkingOnTask mParkingOnTask;
    private ParkingOffTask mParkingOffTask;

    public ParkingOnForceTask(Activity activity, String address, BigInteger time) {
        super(activity);

        mAccountService = new AccountService(activity);

        mAddress = address;
        mTime = time;
    }

    private void initParkingOnTask() {
        mParkingOnTask = new ParkingOnTask(mActivity, mAddress, mTime);
        mParkingOnTask.setPrepareListener(mPrepareListener);
        mParkingOnTask.setFinishListener(mFinishListener);
        mParkingOnTask.setErrorListener(mErrorListener);
        mParkingOnTask.setCompleteListener(mCompleteListener);
    }

    private void initParkingOffTask() {
        mParkingOffTask = new ParkingOffTask(mActivity, mAddress);
        mParkingOffTask.setPrepareListener(mPrepareListener);
        mParkingOffTask.setFinishListener(mFinishListener);
        mParkingOffTask.setErrorListener(mErrorListener);
        mParkingOffTask.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                mParkingOnTask.executeAsync();
            }
        });
    }

    @Override
    public void executeAsync() {
        initParkingOnTask();
        initParkingOffTask();

        GetParkingSwitchesTask switchTask = new GetParkingSwitchesTask(mActivity, mAccountService.getEthAddress());
        switchTask.setPrepareListener(mPrepareListener);
        switchTask.setFinishListener(mFinishListener);
        switchTask.setErrorListener(mErrorListener);
        switchTask.setCompleteListener(new ICallbackOnComplete<SwitchesDto>() {
            @Override
            public void onComplete(SwitchesDto result) {
                if (result.Initialized) {
                    TransactionReceipt tx = new TransactionReceipt();
                    tx.setStatus("0x1");
                    if (result.EndTime.longValue() > Calendar.getInstance().getTimeInMillis() / 1000L) {
                        invokeOnComplete(tx);
                    } else {
                        mParkingOffTask.executeAsync();
                    }
                } else {
                    mParkingOnTask.executeAsync();
                }
            }
        });
        switchTask.executeAsync();
    }
}
