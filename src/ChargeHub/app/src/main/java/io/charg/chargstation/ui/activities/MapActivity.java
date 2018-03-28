package io.charg.chargstation.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.models.ChargeStationMarker;
import io.charg.chargstation.models.GeoFireRequest;
import io.charg.chargstation.models.firebase.GeofireDto;
import io.charg.chargstation.models.firebase.NodeDto;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.Helpers;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.ICameraChangeListener;
import io.charg.chargstation.services.AccountService;
import io.charg.chargstation.services.ChargeHubService;
import io.charg.chargstation.services.DialogHelper;
import io.charg.chargstation.services.FilteringService;
import io.charg.chargstation.services.LocalDB;
import io.charg.chargstation.services.StringHelper;
import io.charg.chargstation.ui.views.ChargeClusterManager;

public class MapActivity extends BaseAuthActivity implements OnMapReadyCallback, ICameraChangeListener, ClusterManager.OnClusterItemClickListener<ChargeStationMarker> {

    private static final int INITIAL_ZOOM_LEVEL = 1;
    private static final int SEARCH_ZOOM_LEVEL = 13;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1000;
    private static final int FILTER_ACTIVITY_REQUEST_CODE = 1001;

    private GoogleMap mGoogleMap;
    private ClusterManager<ChargeStationMarker> mClusterManager;

    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private FilteringService mFilteringService;
    private ChargeHubService mChargeHubService;
    private AccountService mAccountService;
    private LocalDB mLocalDb;
    private SupportMapFragment mMapFragment;

    private List<String> mKeys = new ArrayList<>();

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    TextView tvEthAddress;

    @Override
    public int getResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public void onActivate() {
        Log.v(getClass().getName(), "onActivate");

        initServices();
        initToolbar();
        initNavigationView();
        initMap();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(getClass().getName(), "onRestoreInstanceState");
        Log.v(getClass().getName(), "onRestoreInstanceState bundle=" + String.valueOf(savedInstanceState == null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(getClass().getName(), "onResume");
        loadAccountData();
    }

    private void initServices() {
        initGeoFire();
        mFilteringService = new FilteringService(this);
        mChargeHubService = new ChargeHubService();
        mAccountService = new AccountService(this);
        mLocalDb = new LocalDB(this);
    }

    private void loadAccountData() {
        String ethAddress = mAccountService.getEthAddress();
        if (!ethAddress.isEmpty()) {
            tvEthAddress.setText(StringHelper.getReadableEthAddress(ethAddress));
        } else {
            tvEthAddress.setText(StringHelper.getValueOrNotDefine(ethAddress));
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    private void initNavigationView() {

        tvEthAddress = mNavigationView.getHeaderView(0).findViewById(R.id.tv_eth_address);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.menu_filter:
                        Intent intent = new Intent(MapActivity.this, FilterActivity.class);
                        startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);
                        return true;
                    case R.id.menu_wallet:
                        startActivity(new Intent(MapActivity.this, WalletActivity.class));
                        return true;
                    case R.id.menu_qr_code:
                        startActivity(new Intent(MapActivity.this, ChangeWalletActivity.class));
                        return true;
                    case R.id.menu_log_out:
                        DialogHelper.showQuestion(MapActivity.this, R.string.ask_delete_wallet, new Runnable() {
                            @Override
                            public void run() {
                                mLocalDb.clearDb();
                                loadAccountData();
                            }
                        });
                        return true;
                    case R.id.menu_exit:
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    private void initMap() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude), INITIAL_ZOOM_LEVEL));

        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        mClusterManager = new ChargeClusterManager(this, mGoogleMap, this);
        mClusterManager.setOnClusterItemClickListener(this);

        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        getLocationAsync();
    }

    private void initGeoFire() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_GEOFIRE);
        mGeoFire = new GeoFire(dbRef);
    }

    private void searchChargeStations(GeoFireRequest request) {

        if (mGeoQuery != null) {
            mGeoQuery.setRadius(request.getRadius());
            mGeoQuery.setCenter(new GeoLocation(request.getPosition().latitude, request.getPosition().longitude));
            return;
        }

        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(request.getPosition().latitude, request.getPosition().longitude), request.getRadius());
        mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                if (mKeys.contains(key)) {
                    return;
                }

                mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, NodeDto>() {
                    @Override
                    public String getInputData() {
                        return key;
                    }

                    @Override
                    public void onPrepare() {

                    }

                    @Override
                    public void onComplete(NodeDto result) {
                        if (mFilteringService.isValid(result)) {
                            mKeys.add(key);
                            mClusterManager.addItem(new ChargeStationMarker(location.latitude, location.longitude, key));
                            mClusterManager.cluster();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(MapActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocationAsync();
    }

    private void getLocationAsync() {

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("app", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), mGoogleMap.getCameraPosition().zoom));
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Latitude", "disable");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Latitude", "enable");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Latitude", "status");
            }
        });

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), mGoogleMap.getCameraPosition().zoom));
        }
    }

    @Override
    public void onCameraChangePosition() {
        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
        double radius = Helpers.zoomLevelToRadius(cameraPosition.zoom) / 1000;
        radius = radius > CommonData.RADIUS_LIMIT ? CommonData.RADIUS_LIMIT : radius;
        searchChargeStations(new GeoFireRequest(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude), radius));
    }

    @Override
    public boolean onClusterItemClick(ChargeStationMarker chargeStationMarker) {

        if (!checkWallet()) {
            return true;
        }

        Intent intent = new Intent(MapActivity.this, StationActivity.class);
        intent.putExtra(StationActivity.ARG_STATION_KEY, chargeStationMarker.getKey());
        startActivity(intent);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        operateGooglePlacesResult(requestCode, resultCode, data);
        operateQrCodeResult(requestCode, resultCode, data);
        operateFilterResult(requestCode, resultCode, data);
    }

    private void operateFilterResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILTER_ACTIVITY_REQUEST_CODE) {
            return;
        }

        mClusterManager.clearItems();
        mClusterManager.cluster();

        for (final String item : mKeys) {
            mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, NodeDto>() {
                @Override
                public String getInputData() {
                    return item;
                }

                @Override
                public void onPrepare() {

                }

                @Override
                public void onComplete(final NodeDto result) {
                    if (mFilteringService.isValid(result)) {

                        mChargeHubService.getLocationAsync(new IAsyncCommand<String, GeofireDto>() {
                            @Override
                            public String getInputData() {
                                return result.getEth_address();
                            }

                            @Override
                            public void onPrepare() {

                            }

                            @Override
                            public void onComplete(GeofireDto result) {
                                mClusterManager.addItem(new ChargeStationMarker(result.getLat(), result.getLng(), item));
                                mClusterManager.cluster();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(MapActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MapActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void operateQrCodeResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void operateGooglePlacesResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), SEARCH_ZOOM_LEVEL));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}