package io.charg.chargstation.services.helpers;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ContractHelper {

    public static boolean isStatusOK(TransactionReceipt transaction) {

        String status = transaction.getStatus();

        if (null == status) {
            return true;
        }

        BigInteger statusQuantity = Numeric.decodeQuantity(status);
        return BigInteger.ONE.equals(statusQuantity);
    }

    public static String getStatus(TransactionReceipt transaction) {
        return isStatusOK(transaction) ? "Success" : "Fail";
    }

    public static double getChgFromWei(BigInteger result) {
        if (result == null) {
            return 0;
        }
        return (result.doubleValue() / 1E18);
    }

    public static BigInteger getWeiFromChg(double value) {
        return BigDecimal.valueOf(value * 1E18).toBigInteger();
    }

    public static Boolean getResult(BigInteger result) {
        return BigInteger.ONE.equals(result);
    }
}
