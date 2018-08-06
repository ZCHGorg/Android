package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.dto.SwitchesDto;

public class ParkingOffForceTask extends ChgAsyncTask<TransactionReceipt> {

    private ParkingOffTask mParkingOffTask;
    private String mAddress;
    private AccountService mAccountService;

    public ParkingOffForceTask(Activity activity, String address) {
        super(activity);
        mAddress = address;
        mAccountService = new AccountService(activity);

        initParkingOffTask();
    }

    private void initParkingOffTask() {
        mParkingOffTask = new ParkingOffTask(mActivity, mAddress);
        mParkingOffTask.setPrepareListener(mPrepareListener);
        mParkingOffTask.setFinishListener(mFinishListener);
        mParkingOffTask.setErrorListener(mErrorListener);
        mParkingOffTask.setCompleteListener(mCompleteListener);
    }

    @Override
    public void executeAsync() {
        GetParkingSwitchesTask switchTask = new GetParkingSwitchesTask(mActivity, mAccountService.getEthAddress());
        switchTask.setPrepareListener(mPrepareListener);
        switchTask.setFinishListener(mFinishListener);
        switchTask.setErrorListener(mErrorListener);
        switchTask.setCompleteListener(new ICallbackOnComplete<SwitchesDto>() {
            @Override
            public void onComplete(SwitchesDto result) {
                if (!result.Initialized) {
                    TransactionReceipt tx = new TransactionReceipt();
                    tx.setStatus("0x1");
                    invokeOnComplete(tx);
                } else {
                    initParkingOffTask();
                    mParkingOffTask.executeAsync();
                }
            }
        });
        switchTask.executeAsync();
    }
}
