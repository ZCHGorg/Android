package io.charg.chargstation.services.remote.contract.tasks;

import android.app.Activity;
import android.content.Context;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.local.SettingsProvider;
import io.charg.chargstation.services.remote.contract.ChargCoinContract;

class BaseContractTask {

    private final SettingsProvider mSettings;
    private final Web3j web3j;
    private final AccountService mAccountService;

    ICallbackOnComplete<TransactionReceipt> mCompleteListener;
    ICallbackOnError<String> mErrorListener;

    ChargCoinContract mContract;

    Activity mActivity;

    BaseContractTask(Activity activity) {
        mSettings = new SettingsProvider(activity);
        mAccountService = new AccountService(activity);
        web3j = Web3jFactory.build(new HttpService(mSettings.getEthConnectionUrl()));
        mContract = ChargCoinContract.load(
                mSettings.getContractAddress(),
                web3j,
                Credentials.create(mAccountService.getPrivateKey()),
                mSettings.getGasPrice(), mSettings.getGasLimit());

        mActivity = activity;
    }

    public void setCompleteListener(ICallbackOnComplete<TransactionReceipt> completeListener) {
        mCompleteListener = completeListener;
    }

    public void setErrorListener(ICallbackOnError<String> errorListener) {
        mErrorListener = errorListener;
    }
}
