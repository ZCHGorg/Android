package io.charg.chargstation.services.helpers;

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

    public static String getCostStr(double value) {
        return String.format(Locale.getDefault(), "%.2f $/MIN", value);
    }
}
