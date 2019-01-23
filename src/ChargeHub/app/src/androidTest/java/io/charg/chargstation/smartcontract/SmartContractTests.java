package io.charg.chargstation.smartcontract;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.tasks.GetAuthorizeTask;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceEthTask;
import io.charg.chargstation.ui.activities.ChangeWalletActivity;

@RunWith(AndroidJUnit4.class)
public class SmartContractTests {

    @Rule
    public ActivityTestRule<ChangeWalletActivity> rule = new ActivityTestRule<>(ChangeWalletActivity.class);

    private AccountService mAccountService;

    @Before
    public void init() {
        mAccountService = new AccountService(rule.getActivity());

        mAccountService.putPrivateKey(ContractHelper.generatePrivateKey());

        Assert.assertFalse(TextUtils.isEmpty(mAccountService.getEthAddress()));
    }

    @Test
    public void getBalanceEthTest() throws InterruptedException {
        final double[] balance = new double[1];
        balance[0] = -1;

        new GetBalanceEthTask(rule.getActivity(), mAccountService.getEthAddress())
                .setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                    }
                })
                .setCompleteListener(new ICallbackOnComplete<BigInteger>() {
                    @Override
                    public void onComplete(BigInteger result) {
                        balance[0] = ContractHelper.getChgFromWei(result);
                    }
                })
                .setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {

                    }
                })
                .setFinishListener(new ICallbackOnFinish() {
                    @Override
                    public void onFinish() {

                    }
                })
                .executeAsync();

        Assert.assertNotEquals(balance, -1);

        Thread.sleep(3000);
    }

    @Test
    public void getBalanceChgTest() throws InterruptedException {

        final double[] balance = new double[1];
        balance[0] = -1;

        new GetBalanceChgTask(rule.getActivity(), mAccountService.getEthAddress())
                .setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                    }
                })
                .setCompleteListener(new ICallbackOnComplete<BigInteger>() {
                    @Override
                    public void onComplete(BigInteger result) {
                        balance[0] = ContractHelper.getChgFromWei(result);
                    }
                })
                .setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {

                    }
                })
                .setFinishListener(new ICallbackOnFinish() {
                    @Override
                    public void onFinish() {

                    }
                })
                .executeAsync();

        Assert.assertNotEquals(balance, -1);

        Thread.sleep(3000);

    }

    @Test
    public void getAuthorizeTest() {

        mAccountService.putPrivateKey(ContractHelper.generatePrivateKey());

        GetAuthorizeTask task = new GetAuthorizeTask(rule.getActivity(), mAccountService.getEthAddress());
    }

}
