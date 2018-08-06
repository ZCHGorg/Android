package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.dto.SwitchesDto;

public class ChargeOffForceTask extends ChgAsyncTask<TransactionReceipt> {

    private ChargeOffTask mChargeOffTask;
    private String mAddress;
    private AccountService mAccountService;

    public ChargeOffForceTask(Activity activity, String address) {
        super(activity);
        mAddress = address;
        mAccountService = new AccountService(activity);

        initChargeOffTask();
    }

    private void initChargeOffTask() {
        mChargeOffTask = new ChargeOffTask(mActivity, mAddress);
        mChargeOffTask.setPrepareListener(mPrepareListener);
        mChargeOffTask.setFinishListener(mFinishListener);
        mChargeOffTask.setErrorListener(mErrorListener);
        mChargeOffTask.setCompleteListener(mCompleteListener);
    }

    @Override
    public void executeAsync() {
        GetChargingSwitchesTask switchTask = new GetChargingSwitchesTask(mActivity, mAccountService.getEthAddress());
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
                    initChargeOffTask();
                    mChargeOffTask.executeAsync();
                }
            }
        });
        switchTask.executeAsync();
    }
}
