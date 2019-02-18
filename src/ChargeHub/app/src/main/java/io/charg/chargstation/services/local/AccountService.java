package io.charg.chargstation.services.local;

import android.content.Context;
import android.text.TextUtils;

import org.web3j.crypto.Credentials;

import io.charg.chargstation.services.helpers.ContractHelper;

/**
 * Created by worker on 23.01.2018.
 */

public class AccountService {

    private final static String ARG_PRIVATE_KEY = "KEY_ETH_PRIVATE_KEY";

    private LocalDB mLocalDB;

    public AccountService(Context context) {
        mLocalDB = new LocalDB(context);
    }

    public Boolean putPrivateKey(String privateKey) {
        return mLocalDB.putValue(ARG_PRIVATE_KEY, privateKey);
    }

    public String getPrivateKey() {
        return mLocalDB.getString(ARG_PRIVATE_KEY, "");
    }

    public String getEthAddress() {
        String privateKey = getPrivateKey();
        if (privateKey == null || privateKey.isEmpty()) {
            return null;
        }

        Credentials credentials = Credentials.create(privateKey);
        return credentials.getAddress().toLowerCase();
    }

    public boolean isValidAccountExist() {
        return !TextUtils.isEmpty(getEthAddress()) && !TextUtils.isEmpty(getPrivateKey());
    }

    public void initRandomAccount() {
        String privateKey = ContractHelper.generatePrivateKey();
        putPrivateKey(privateKey);
    }
}
