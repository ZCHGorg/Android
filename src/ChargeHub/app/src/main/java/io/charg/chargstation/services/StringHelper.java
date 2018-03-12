package io.charg.chargstation.services;

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

    public static String getReadableEthAddress(String ethAddress) {
        return String.format("%s...%s", ethAddress.substring(0, 7), ethAddress.substring(ethAddress.length() - 7));
    }
}
