package io.charg.chargstation.services.remote.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.ICallbackOnComplete;

public class ChargeDbApi {

    public void getStationAsync(String ethAddress, final ICallbackOnComplete<StationDto> completeCallback) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);
        dbRef.child(ethAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                completeCallback.onComplete(dataSnapshot.getValue(StationDto.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                completeCallback.onComplete(null);
            }
        });
    }

    public void saveStation(String key, StationDto nodeDto) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);
        dbRef.child(key).setValue(nodeDto);
    }
}
