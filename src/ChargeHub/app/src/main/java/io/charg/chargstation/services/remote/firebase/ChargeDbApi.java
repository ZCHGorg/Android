package io.charg.chargstation.services.remote.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.CommonData;

public class ChargeDbApi {

    public void saveStation(String key, StationDto nodeDto) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);
        dbRef.child(key).setValue(nodeDto);
    }
}
