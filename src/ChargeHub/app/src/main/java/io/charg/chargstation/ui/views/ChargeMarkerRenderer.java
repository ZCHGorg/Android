package io.charg.chargstation.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.charg.chargstation.R;
import io.charg.chargstation.models.ChargeStationMarker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by worker on 02.11.2017.
 */
public class ChargeMarkerRenderer extends DefaultClusterRenderer<ChargeStationMarker> {

    private Context mContext;

    public ChargeMarkerRenderer(Context context, GoogleMap map,
                                ClusterManager<ChargeStationMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(ChargeStationMarker item, MarkerOptions markerOptions) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_station_marker);

        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}