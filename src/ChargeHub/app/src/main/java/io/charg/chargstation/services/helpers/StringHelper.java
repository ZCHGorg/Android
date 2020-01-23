package io.charg.chargstation.services.helpers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import io.charg.chargstation.root.CommonData;

/**
 * Created by worker on 24.11.2017.
 */

public class StringHelper {

    public static String getValueOrNotDefine(String value) {
        if (value == null || value.isEmpty()) {
            return "not defined";
        } else {
            return value;
        }
    }

    public static String getShortEthAddress(String ethAddress) {

        if (ethAddress == null) {
            return CommonData.NOT_DEFINED;
        }

        if (ethAddress.length() < 14) {
            return ethAddress;
        }
        return String.format("%s...%s", ethAddress.substring(0, 7), ethAddress.substring(ethAddress.length() - 7));
    }

    public static String getDateTimeFromEpoch(long epochTime) {
        Date date = new Date(epochTime);
        return date.toString();
    }

    public static String getCostStr(double value) {
        return String.format(Locale.getDefault(), "%.2f $/MIN", value);
    }

    public static String getRateChgStr(double value) {
        return String.format(Locale.getDefault(), "%.2f CHG/sec", value);
    }

    public static String getTimeStr(long value) {
        return String.format(Locale.getDefault(), "%d seconds", value);
    }

    public static String getBalanceChgStr(double value) {
        return String.format(Locale.getDefault(), "%.3f CHG", value);
    }

    public static String getTimeStr(Date date) {
        return new SimpleDateFormat("d-MMM-yyyy HH:mm:ss", Locale.getDefault()).format(date);
    }
}
