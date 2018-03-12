package io.charg.chargstation.ui.views;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

import io.charg.chargstation.models.ChargeStationMarker;
import io.charg.chargstation.root.ICameraChangeListener;

/**
 * Created by worker on 02.11.2017.
 */

public class ChargeClusterManager extends ClusterManager<ChargeStationMarker> {

    private ICameraChangeListener mCameraChangeListener;

    public ChargeClusterManager(Context context, GoogleMap map, ICameraChangeListener cameraChangeListener) {
        super(context, map);

        mCameraChangeListener = cameraChangeListener;
        setRenderer(new ChargeMarkerRenderer(context, map, this));
    }

    @Override
    public void onCameraIdle() {
        super.onCameraIdle();
        mCameraChangeListener.onCameraChangePosition();
    }
}

