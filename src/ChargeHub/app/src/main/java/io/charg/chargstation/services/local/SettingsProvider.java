package io.charg.chargstation.services.local;

import android.content.Context;

import java.math.BigInteger;
import java.util.Locale;

import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.local.LocalDB;

public class SettingsProvider {

    public static final long DEFAULT_GAS_LIMIT = 70000L;
    public static final long DEFAULT_GAS_PRICE = 48000000000L;

    private static final String KEY_SMART_CONTRACT_ADDRESS = "KEY_SMART_CONTRACT_ADDRESS";
    private static final String KEY_NETWORK_TYPE = "KEY_NETWORK_TYPE";
    private static final String KEY_GAS_LIMIT = "KEY_GAS_LIMIT";
    private static final String KEY_GAS_PRICE = "KEY_GAS_PRICE";

    private LocalDB mLocalDb;

    public SettingsProvider(Context context) {
        mLocalDb = new LocalDB(context);
    }

    public String getContractAddress() {
        return mLocalDb.getString(KEY_SMART_CONTRACT_ADDRESS, CommonData.SMART_CONTRACT_ADDRESS);
    }

    public void setContractAddress(String value) {
        mLocalDb.putValue(KEY_SMART_CONTRACT_ADDRESS, value);
    }

    public boolean getNetwork() {
        return mLocalDb.getBoolean(KEY_NETWORK_TYPE, true);
    }

    public boolean setNetwork(boolean value) {
        return mLocalDb.putBoolean(KEY_NETWORK_TYPE, value);
    }

    public BigInteger getGasLimit() {
        return BigInteger.valueOf(mLocalDb.getLong(KEY_GAS_LIMIT, DEFAULT_GAS_LIMIT));
    }

    public BigInteger getGasPrice() {
        return BigInteger.valueOf(mLocalDb.getLong(KEY_GAS_PRICE, DEFAULT_GAS_PRICE));
    }

    public void setGasLimit(Long value) {
        mLocalDb.putLong(KEY_GAS_LIMIT, value);
    }

    public void setGasPrice(Long value) {
        mLocalDb.putLong(KEY_GAS_PRICE, value);
    }

    public String getEthConnectionUrl() {
        return String.format(Locale.getDefault(), "https://%s.infura.io/Zp6evGImttk7WOe95WcW",
                getNetwork() ? "mainnet" : "rinkeby");
    }
}
