package io.charg.chargstation.services.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

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
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getString(key, defValue);
    }

    public Boolean putValue(String key, String value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public void clearDb() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public boolean putBoolean(String key, boolean value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public long getLong(String key, long value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getLong(key, value);
    }

    public void putLong(String key, long value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }
}
