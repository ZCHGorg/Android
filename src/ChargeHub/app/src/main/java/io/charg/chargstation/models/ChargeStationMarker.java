package io.charg.chargstation.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by worker on 01.11.2017.
 */

public class ChargeStationMarker implements ClusterItem {

    private final String mKey;
    private final String mSnippet;
    private LatLng mPosition;

   public static final String SNIPPET_CHARG = "SNIPPET_CHARG";
   public static final String SNIPPET_UNKNOWN = "SNIPPET_UNKNOWN";

    public ChargeStationMarker(double lat, double lng, String key, String snippet) {
        mKey = key;
        mPosition = new LatLng(lat, lng);
        mSnippet = snippet;
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
        return mSnippet;
    }

    public String getKey() {
        return mKey;
    }

    public boolean isCharg() {
        return mSnippet.equals(SNIPPET_CHARG);
    }


}
