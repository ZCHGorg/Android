package io.charg.chargstation.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import io.charg.chargstation.models.StationFilter;

/**
 * Created by worker on 13.11.2017.
 */

public class LocalDB {

    private static final String DB_NAME = "charg";

    private Context mContext;

    public LocalDB(Context context) {
        mContext = context;
    }

    public String getValue(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public Boolean putValue(String key, String value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public void clearDb() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }
}
