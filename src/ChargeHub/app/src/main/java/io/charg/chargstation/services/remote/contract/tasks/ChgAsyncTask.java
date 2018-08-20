package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.local.SettingsProvider;
import io.charg.chargstation.services.remote.contract.ChargCoinContract;

public abstract class ChgAsyncTask<T> {

    ICallbackOnComplete<T> mCompleteListener;
    ICallbackOnError<String> mErrorListener;
    ICallbackOnPrepare mPrepareListener;
    ICallbackOnFinish mFinishListener;

    final Web3j mWeb3j;
    final ChargCoinContract mContract;

    Activity mActivity;

    public ChgAsyncTask(Activity activity) {
        SettingsProvider mSettings = new SettingsProvider(activity);
        AccountService mAccountService = new AccountService(activity);

        mActivity = activity;

        mWeb3j = Web3jFactory.build(new HttpService(mSettings.getEthConnectionUrl()));
        mContract = ChargCoinContract.load(
                mSettings.getContractAddress(),
                mWeb3j,
                Credentials.create(mAccountService.getPrivateKey()),
                mSettings.getGasPrice(), mSettings.getGasLimit());

    }

    public ChgAsyncTask<T> setCompleteListener(ICallbackOnComplete<T> completeListener) {
        mCompleteListener = completeListener;
        return this;
    }

    public ChgAsyncTask<T> setErrorListener(ICallbackOnError<String> errorListener) {
        mErrorListener = errorListener;
        return this;
    }

    public ChgAsyncTask<T> setPrepareListener(ICallbackOnPrepare prepareListener) {
        this.mPrepareListener = prepareListener;
        return this;
    }

    public ChgAsyncTask<T> setFinishListener(ICallbackOnFinish finishListener) {
        this.mFinishListener = finishListener;
        return this;
    }

    void invokeOnComplete(final T result) {
        if (mCompleteListener != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCompleteListener.onComplete(result);
                }
            });
        }
    }

    void invokeOnError(final String error) {
        if (mErrorListener != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mErrorListener.onError(error);
                }
            });
        }
    }

    void invokeOnPrepare() {
        if (mPrepareListener != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPrepareListener.onPrepare();
                }
            });
        }
    }

    void invokeOnFinish() {
        if (mFinishListener != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFinishListener.onFinish();
                }
            });
        }
    }

    public abstract void executeAsync();
}
