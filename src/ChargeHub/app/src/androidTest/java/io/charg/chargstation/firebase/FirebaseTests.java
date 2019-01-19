package io.charg.chargstation.firebase;

import com.google.firebase.database.DatabaseError;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import androidx.test.runner.AndroidJUnit4;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.services.remote.firebase.tasks.GetStationDtoTask;

@RunWith(AndroidJUnit4.class)
public class FirebaseTests {

    @Test
    public void getStation() throws InterruptedException {
        final StationDto[] station = new StationDto[1];

        GetStationDtoTask task = new GetStationDtoTask("0x3b10ed00b2b4fe3fcc7af838c9a5dade294dbd89");
        task.setCompleteCallback(new ICallbackOnComplete<StationDto>() {
            @Override
            public void onComplete(StationDto result) {
                station[0] = result;
            }
        });
        task.setPrepareCallback(new ICallbackOnPrepare() {
            @Override
            public void onPrepare() {

            }
        });
        task.setErrorCallback(new ICallbackOnError<DatabaseError>() {
            @Override
            public void onError(DatabaseError message) {

            }
        });
        task.setFinishCallback(new ICallbackOnFinish() {
            @Override
            public void onFinish() {

            }
        });
        task.executeAsync();

        TimeUnit.SECONDS.sleep(3);

        Assert.assertNotNull(station[0]);

        task.setFinishCallback(null);
        task.setErrorCallback(null);
        task.setPrepareCallback(null);
        task.setCompleteCallback(null);
        task.executeAsync();


    }


}
