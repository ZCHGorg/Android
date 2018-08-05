package io.charg.chargstation.services.remote.contract.dto;

import org.web3j.tuples.generated.Tuple6;

import java.math.BigInteger;
import java.util.Date;

import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.helpers.StringHelper;

public class ChargingSwitchesDto {

    public String Node;

    public BigInteger StartTime;

    public BigInteger EndTime;

    public BigInteger FixedRate;

    public boolean Initialized;

    public BigInteger PredefinedAmount;

    public ChargingSwitchesDto(Tuple6<String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger> tuple) {
        Node = tuple.getValue1();
        StartTime = tuple.getValue2();
        EndTime = tuple.getValue3();
        FixedRate = tuple.getValue4();
        Initialized = tuple.getValue5();
        PredefinedAmount = tuple.getValue6();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Node: ")
                .append(StringHelper.getShortEthAddress(Node))
                .append("\n")
                .append("Start Time: ")
                .append(new Date(StartTime.longValue() * 1000L).toString())
                .append("\n")
                .append("End Time: ")
                .append(new Date(EndTime.longValue() * 1000L).toString())
                .append("\n")
                .append("Fixed rate: ")
                .append(StringHelper.getRateChgStr(ContractHelper.getChgFromWei(FixedRate)))
                .append("\n")
                .append("Initialized: ")
                .append(Initialized)
                .append("\n")
                .append("Predefined amount: ")
                .append(ContractHelper.getChgFromWei(PredefinedAmount))
                .append(" CHG")
                .append("\n")
                .toString();
    }
}
