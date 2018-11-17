package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Calendar;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.dto.SwitchesDto;

public class ChargeOnForceTask extends ChgAsyncTask<TransactionReceipt> {

    private String mAddress;
    private BigInteger mTime;

    private AccountService mAccountService;

    private ChargeOnTask mChargeOnTask;
    private ChargeOffTask mChargeOffTask;

    public ChargeOnForceTask(Activity activity, String address, BigInteger time) {
        super(activity);

        mAccountService = new AccountService(activity);

        mAddress = address;
        mTime = time;
    }

    private void initChargeOnTask() {
        mChargeOnTask = new ChargeOnTask(mActivity, mAddress, mTime);
        mChargeOnTask.setPrepareListener(mPrepareListener);
        mChargeOnTask.setFinishListener(mFinishListener);
        mChargeOnTask.setErrorListener(mErrorListener);
        mChargeOnTask.setCompleteListener(mCompleteListener);
    }

    private void initChargeOffTask() {
        mChargeOffTask = new ChargeOffTask(mActivity, mAddress);
        mChargeOffTask.setPrepareListener(mPrepareListener);
        mChargeOffTask.setFinishListener(mFinishListener);
        mChargeOffTask.setErrorListener(mErrorListener);
        mChargeOffTask.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                mChargeOnTask.executeAsync();
            }
        });
    }

    @Override
    public void executeAsync() {
        initChargeOnTask();
        initChargeOffTask();

        GetChargingSwitchesTask switchTask = new GetChargingSwitchesTask(mActivity, mAccountService.getEthAddress());
        switchTask.setPrepareListener(mPrepareListener);
        switchTask.setFinishListener(mFinishListener);
        switchTask.setErrorListener(mErrorListener);
        switchTask.setCompleteListener(new ICallbackOnComplete<SwitchesDto>() {
            @Override
            public void onComplete(SwitchesDto result) {
                if (result.Initialized) {
                    TransactionReceipt tx = new TransactionReceipt();
                    tx.setStatus("0x1");
                    if (result.EndTime.longValue() > Calendar.getInstance().getTimeInMillis() / 1000L && result.Node.equals(mAddress)) {
                        invokeOnComplete(tx);
                    } else {
                        mChargeOffTask.executeAsync();
                    }
                } else {
                    mChargeOnTask.executeAsync();
                }
            }
        });
        switchTask.executeAsync();
    }
}
