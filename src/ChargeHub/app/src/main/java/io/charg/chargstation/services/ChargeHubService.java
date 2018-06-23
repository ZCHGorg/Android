package io.charg.chargstation.services;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.charg.chargstation.models.firebase.GeofireDto;
import io.charg.chargstation.models.firebase.NodeDto;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.IAsyncCommand;

/**
 * Created by worker on 02.11.2017.
 */

public class ChargeHubService {

    public void getChargeNodeAsync(final IAsyncCommand<String, NodeDto> command) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);

        if (command.getInputData() == null) {
            command.onError("Node's address is empty");
            return;
        }

        final DatabaseReference dbNodeRef = dbRef.child(command.getInputData());
        dbNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NodeDto nodeDto = dataSnapshot.getValue(NodeDto.class);
                if (nodeDto == null) {
                    command.onError("Error while loading station " + command.getInputData());
                } else {
                    command.onComplete(nodeDto);
                }
                dbNodeRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                command.onError(databaseError.getMessage());
            }
        });
    }

    public void saveNode(String key, NodeDto nodeDto) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES);
        dbRef.child(key).setValue(nodeDto);
    }

    public DatabaseReference getChargeNodeDbRef(String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_NODES).child(key);
        return dbRef;
    }

    public void getLocationAsync(final IAsyncCommand<String, GeofireDto> command) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_GEOFIRE);

        String ethAddress = command.getInputData();
        if (ethAddress == null) {
            return;
        }

        final DatabaseReference dbNodeRef = dbRef.child(ethAddress);
        dbNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GeofireDto location = dataSnapshot.getValue(GeofireDto.class);
                command.onComplete(location);
                dbNodeRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                command.onError(databaseError.getMessage());
            }
        });
    }
}
