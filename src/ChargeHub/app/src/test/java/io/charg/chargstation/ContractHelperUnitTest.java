package io.charg.chargstation;

import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import io.charg.chargstation.services.helpers.ContractHelper;

public class ContractHelperUnitTest {

    @Test
    public void isStatusOK() {

        TransactionReceipt tx = new TransactionReceipt();
        tx.setStatus("0x1");

        Assert.assertTrue(ContractHelper.isStatusOK(tx));

        tx.setStatus(null);

        Assert.assertTrue(ContractHelper.isStatusOK(tx));

        tx.setStatus("0x0");

        Assert.assertFalse(ContractHelper.isStatusOK(tx));
    }

    @Test
    public void getStatus() {

        TransactionReceipt tx = new TransactionReceipt();
        tx.setStatus("0x1");

        Assert.assertEquals(ContractHelper.getStatus(tx), "Success");

        tx.setStatus("0x0");

        Assert.assertEquals(ContractHelper.getStatus(tx), "Fail");

        tx.setStatus("0x3");

        Assert.assertEquals(ContractHelper.getStatus(tx), "Fail");
    }

    @Test
    public void getChgFromWei() {
        Assert.assertEquals(ContractHelper.getChgFromWei(BigInteger.ONE), 1E-18, 0.0);

        Assert.assertEquals(ContractHelper.getChgFromWei(BigInteger.ZERO), 0, 0.0);

        Assert.assertEquals(ContractHelper.getChgFromWei(BigInteger.TEN), 1E-17, 0.0);

        Assert.assertEquals(ContractHelper.getChgFromWei(null), 0, 0.0);
    }

    @Test
    public void getWeiFromChg() {
    }

    @Test
    public void getResult() {
    }
}
