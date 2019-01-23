package io.charg.chargstation.services.helpers;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ContractHelper {

    public static boolean isStatusOK(TransactionReceipt transaction) {

        String status = transaction.getStatus();

        if (status == null) {
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

    public static String generatePrivateKey() {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            return keyPair.getPrivateKey().toString(16);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
