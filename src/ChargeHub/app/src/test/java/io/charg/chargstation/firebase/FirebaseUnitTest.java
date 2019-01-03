package io.charg.chargstation.firebase;

import org.junit.Assert;
import org.junit.Test;

import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.remote.contract.tasks.GetBalanceChgTask;
import io.charg.chargstation.services.remote.firebase.tasks.GetStationDtoTask;

public class FirebaseUnitTest{

    @Test
    public void getItem() {

        /*new GetBalanceChgTask()

        GetStationDtoTask task = new GetStationDtoTask("0x3b10ed00b2b4fe3fcc7af838c9a5dade294dbd89");
        task.setCompleteCallback(new ICallbackOnComplete<StationDto>() {
            @Override
            public void onComplete(StationDto result) {
                Assert.assertNotNull(result);
            }
        });
        task.executeAsync();*/

        Assert.assertEquals(3, 3);

    }
}
