package io.charg.chargstation.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

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

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), " %s; R: %.2f", mPosition, mRadius);
    }
}
