package io.charg.chargstation.services;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.charg.chargstation.models.ChargeStationMarker;
import io.charg.chargstation.models.GeoFireRequest;
import io.charg.chargstation.models.firebase.ChargeStation;
import io.charg.chargstation.models.firebase.Node;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.IAsyncCommand;

/**
 * Created by worker on 02.11.2017.
 */

public class ChargeHubService {

    public void getChargeLocationsAsync(IAsyncCommand<GeoFireRequest, ChargeStationMarker> command) {

    }

    public void getChargeStationAsync(final IAsyncCommand<String, ChargeStation> command) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.LOCATIONS_PATH);
        dbRef.child(command.getInputData()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChargeStation chargeStation = dataSnapshot.getValue(ChargeStation.class);
                command.onComplete(chargeStation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                command.onError(databaseError.getMessage());
            }
        });
    }

    public void getChargeNodeAsync(final IAsyncCommand<String, Node> command) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);

        final DatabaseReference dbNodeRef = dbRef.child(command.getInputData());
        dbNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Node node = dataSnapshot.getValue(Node.class);
                command.onComplete(node);
                dbNodeRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                command.onError(databaseError.getMessage());
            }
        });
    }

    public void saveNode(String key, Node node) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);
        dbRef.child(key).setValue(node);
    }

    public DatabaseReference getChargeNodeDbRef(String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES).child(key);
        return dbRef;
    }
}
