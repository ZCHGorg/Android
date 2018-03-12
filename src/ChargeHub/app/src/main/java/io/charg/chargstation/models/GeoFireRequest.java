package io.charg.chargstation.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by worker on 02.11.2017.
 */

public class GeoFireRequest {

    private LatLng mPosition;

    private double mRadius;

    public GeoFireRequest(LatLng position, double radius) {
        mPosition = position;
        mRadius = radius;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public double getRadius() {
        return mRadius;
    }
}
