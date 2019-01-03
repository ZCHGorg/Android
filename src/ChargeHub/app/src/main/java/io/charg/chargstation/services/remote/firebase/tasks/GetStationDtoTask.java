package io.charg.chargstation.services.remote.firebase.tasks;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;

public class GetStationDtoTask {

    private String mEthAddress;

    private ICallbackOnPrepare mPrepareCallback;
    private ICallbackOnFinish mFinishCallback;
    private ICallbackOnComplete<StationDto> mCompleteCallback;
    private ICallbackOnError<DatabaseError> mErrorCallback;

    public GetStationDtoTask(@NonNull String ethAddress) {
        mEthAddress = ethAddress.toLowerCase();
    }

    public void executeAsync() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);

        invokeOnPrepare();

        dbRef.child(mEthAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                invokeOnFinish();
                invokeOnComplete(dataSnapshot.getValue(StationDto.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                invokeOnFinish();
                invokeOnError(databaseError);
            }
        });
    }

    private void invokeOnError(DatabaseError databaseError) {
        if (mErrorCallback != null) {
            mErrorCallback.onError(databaseError);
        }
    }

    private void invokeOnComplete(StationDto value) {
        if (mCompleteCallback != null) {
            mCompleteCallback.onComplete(value);
        }
    }

    private void invokeOnFinish() {
        if (mFinishCallback != null) {
            mFinishCallback.onFinish();
        }
    }

    private void invokeOnPrepare() {
        if (mPrepareCallback != null) {
            mPrepareCallback.onPrepare();
        }
    }

    public GetStationDtoTask setPrepareCallback(ICallbackOnPrepare prepareCallback) {
        mPrepareCallback = prepareCallback;
        return this;
    }

    public GetStationDtoTask setFinishCallback(ICallbackOnFinish finishCallback) {
        mFinishCallback = finishCallback;
        return this;
    }

    public GetStationDtoTask setCompleteCallback(ICallbackOnComplete<StationDto> completeCallback) {
        mCompleteCallback = completeCallback;
        return this;
    }

    public GetStationDtoTask setErrorCallback(ICallbackOnError<DatabaseError> errorCallback) {
        mErrorCallback = errorCallback;
        return this;
    }
}
