package io.charg.chargstation;

import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

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
}
