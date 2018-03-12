package io.charg.chargstation.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by worker on 01.11.2017.
 */

public class ChargeStationMarker implements ClusterItem {

    private final String mKey;
    private LatLng mPosition;

    public ChargeStationMarker(double lat, double lng, String key) {
        mKey = key;
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mKey;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getKey() {
        return mKey;
    }
}
