package io.charg.chargstation.services.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;

import java.math.BigInteger;

import io.charg.chargstation.models.StationFilter;

/**
 * Created by worker on 13.11.2017.
 */

public class LocalDB {

    private Context mContext;

    public LocalDB(Context context) {
        mContext = context;
    }

    public String getString(String key) {
        return getSharedPreferences()
                .getString(key, "");
    }

    public boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences()
                .getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return getSharedPreferences()
                .getString(key, defValue);
    }

    public Boolean putValue(String key, String value) {
        return getSharedPreferences()
                .edit()
                .putString(key, value)
                .commit();
    }

    public void clearDb() {
        getSharedPreferences()
                .edit()
                .clear()
                .apply();
    }

    public boolean putBoolean(String key, boolean value) {
        return getSharedPreferences()
                .edit()
                .putBoolean(key, value)
                .commit();
    }

    public long getLong(String key, long value) {
        return getSharedPreferences()
                .getLong(key, value);
    }

    public void putLong(String key, long value) {
        getSharedPreferences()
                .edit()
                .putLong(key, value)
                .apply();
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
    }

}
