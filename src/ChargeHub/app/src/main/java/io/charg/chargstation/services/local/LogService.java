package io.charg.chargstation.services.local;

import android.util.Log;

public class LogService {

    private static final String TAG = "Charge";

    public static void info(String message) {
        Log.i(TAG, message);
    }

}
