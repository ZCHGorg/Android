package io.charg.chargstation.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.models.ChargeStationMarker;
import io.charg.chargstation.models.GeoFireRequest;
import io.charg.chargstation.models.firebase.GeofireDto;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.Helpers;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.root.ICameraChangeListener;
import io.charg.chargstation.services.helpers.DialogHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.local.FilteringService;
import io.charg.chargstation.services.local.LocalDB;
import io.charg.chargstation.services.local.LogService;
import io.charg.chargstation.services.remote.firebase.ChargeDbApi;
import io.charg.chargstation.services.remote.firebase.ChargeHubService;
import io.charg.chargstation.services.remote.firebase.tasks.GetStationDtoTask;
import io.charg.chargstation.ui.activities.becomeOwner.BecomeOwnerActivity;
import io.charg.chargstation.ui.activities.chargeCoinService.ChargeCoinServiceActivity;
import io.charg.chargstation.ui.activities.chargingActivity.ChargingActivity;
import io.charg.chargstation.ui.activities.parkingActivity.ParkingActivity;
import io.charg.chargstation.ui.activities.stationActivity.StationActivity;
import io.charg.chargstation.ui.dialogs.StationEthAddressDialog;
import io.charg.chargstation.ui.dialogs.TxWaitDialog;
import io.charg.chargstation.ui.views.ChargeClusterManager;

public class MapActivity
        extends BaseAuthActivity
        implements
        OnMapReadyCallback,
        ICameraChangeListener,
        ClusterManager.OnClusterItemClickListener<ChargeStationMarker> {

    private static final int INITIAL_ZOOM_LEVEL = 8;
    private static final int SEARCH_ZOOM_LEVEL = 12;
    private static final int STATION_ZOOM_LEVEL = 15;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1000;
    private static final int FILTER_ACTIVITY_REQUEST_CODE = 1001;

    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LNG = "EXTRA_LNG";

    private GoogleMap mGoogleMap;
    private ChargeClusterManager mClusterManager;

    private ChargeDbApi mChargeCoinApi;
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private FilteringService mFilteringService;
    private ChargeHubService mChargeHubService;
    private AccountService mAccountService;
    private LocalDB mLocalDb;
    private SupportMapFragment mMapFragment;

    private List<String> mStationKeys;
    private LatLng mInitLocation;
    private long mLastBackPressedTime;

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
        Log.v(getClass().getName(), "onShows");

        initServices();
        initIntent();
        initToolbar();
        initNavigationView();
        initMapAsync();
    }

    private void initIntent() {
        double lat = getIntent().getDoubleExtra(EXTRA_LAT, -1);
        double lng = getIntent().getDoubleExtra(EXTRA_LNG, -1);
        if (lat != -1 && lng != -1) {
            mInitLocation = new LatLng(lat, lng);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(getClass().getName(), "onResume");
        loadAccountData();
    }

    private void initServices() {

        mStationKeys = new ArrayList<>();

        initGeoFire();
        mChargeCoinApi = new ChargeDbApi();
        mFilteringService = new FilteringService(this);
        mChargeHubService = new ChargeHubService();
        mAccountService = new AccountService(this);
        mLocalDb = new LocalDB(this);
    }

    private void loadAccountData() {
        String ethAddress = mAccountService.getEthAddress();
        if (ethAddress == null) {
            tvEthAddress.setText(R.string.not_defined);
        } else {
            tvEthAddress.setText(StringHelper.getShortEthAddress(ethAddress));
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mLastBackPressedTime + 3000 > new Date().getTime() && mLastBackPressedTime != 0) {
            super.onBackPressed();
        } else {
            mLastBackPressedTime = new Date().getTime();
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    private void initNavigationView() {

        tvEthAddress = mNavigationView.getHeaderView(0).findViewById(R.id.tv_eth_address);

        mNavigationView.getHeaderView(0).findViewById(R.id.btn_show_qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showQrCode(MapActivity.this, mAccountService.getEthAddress());
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.menu_favorites:
                        startActivity(new Intent(MapActivity.this, FavoritesActivity.class));
                        return true;
                    case R.id.menu_charge_coin_service:
                        startActivity(new Intent(MapActivity.this, ChargeCoinServiceActivity.class));
                        return true;
                    case R.id.menu_filter:
                        Intent intent = new Intent(MapActivity.this, FilterActivity.class);
                        startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);
                        return true;
                    case R.id.menu_wallet:
                        if (checkWalletWithDialog()) {
                            startActivity(new Intent(MapActivity.this, WalletActivity.class));
                        }
                        return true;
                    case R.id.menu_contract:
                        startActivity(new Intent(MapActivity.this, ContractActivity.class));
                        return true;
                    case R.id.menu_become_station_owner:
                        startActivity(new Intent(MapActivity.this, BecomeOwnerActivity.class));
                        return true;
                    case R.id.menu_charge:
                        startActivity(new Intent(MapActivity.this, ChargingActivity.class));
                        return true;
                    case R.id.menu_parking:
                        startActivity(new Intent(MapActivity.this, ParkingActivity.class));
                        return true;
                    case R.id.menu_qr_code:
                        startActivity(new Intent(MapActivity.this, ChangeWalletActivity.class));
                        return true;
                    case R.id.menu_settings:
                        startActivity(new Intent(MapActivity.this, SettingsActivity.class));
                        return true;
                    case R.id.menu_socketio:
                        startActivity(new Intent(MapActivity.this, SocketIOActivity.class));
                        return true;
                    case R.id.menu_log_out:
                        requestLogOut();
                        return true;
                    case R.id.menu_exit:
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void requestLogOut() {
        DialogHelper.showQuestion(MapActivity.this, getString(R.string.ask_delete_wallet), new Runnable() {
            @Override
            public void run() {
                mLocalDb.clearDb();
                loadAccountData();
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
                showRequestGoogleSearch();
                return true;
            case R.id.menu_station_eth:
                showRequestStationEthAddressDialog();
                return true;
            default:
                return false;
        }
    }

    private void showRequestGoogleSearch() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRequestStationEthAddressDialog() {
        StationEthAddressDialog dialog = new StationEthAddressDialog(this);
        dialog.setOnComplete(new ICallbackOnComplete<String>() {
            @Override
            public void onComplete(String result) {
                findStationByEthAddressAsync(result);
            }
        });
        dialog.show();
    }

    private void initMapAsync() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mGoogleMap.getCameraPosition().target.latitude,
                        mGoogleMap.getCameraPosition().target.longitude),
                INITIAL_ZOOM_LEVEL));

        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        mClusterManager = new ChargeClusterManager(this, mGoogleMap, this);
        mClusterManager.setOnClusterItemClickListener(this);

        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);

        if (mInitLocation != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mInitLocation, STATION_ZOOM_LEVEL));
        } else {
            getCurrentLocationAsync();
        }
    }

    private void initGeoFire() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(CommonData.FIREBASE_PATH_GEOFIRE);
        mGeoFire = new GeoFire(dbRef);
    }

    private void searchChargeStations(GeoFireRequest request) {

        LogService.info("Search: " + request.toString());

        if (mGeoQuery != null) {
            mGeoQuery.setRadius(request.getRadius());
            mGeoQuery.setCenter(new GeoLocation(request.getPosition().latitude, request.getPosition().longitude));
            return;
        }

        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(request.getPosition().latitude, request.getPosition().longitude), request.getRadius());
        mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {

                LogService.info(String.format(Locale.getDefault(), "Station: %s at [%.2f,%.2f]", key, location.latitude, location.longitude));

                if (mStationKeys.contains(key)) {
                    return;
                }

                onMapKeyEntered(key, location);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format(Locale.getDefault(), "Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
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

    private void onMapKeyEntered(final String key, final GeoLocation location) {
        GetStationDtoTask task = new GetStationDtoTask(key);
        task.setCompleteCallback(new ICallbackOnComplete<StationDto>() {
            @Override
            public void onComplete(StationDto result) {

                if (result == null) {

                    mStationKeys.add(key);
                    mClusterManager.addItem(new ChargeStationMarker(location.latitude, location.longitude, key, ChargeStationMarker.SNIPPET_UNKNOWN));
                    mClusterManager.cluster();

                    return;
                }

                if (!mFilteringService.isValid(result)) {
                    return;
                }

                mStationKeys.add(key);
                mClusterManager.addItem(new ChargeStationMarker(location.latitude, location.longitude, key, ChargeStationMarker.SNIPPET_CHARG));
                mClusterManager.cluster();
            }
        });
        task.executeAsync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getCurrentLocationAsync();
    }

    private void getCurrentLocationAsync() {

        LogService.info("Get current location started");

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            return;
        }

        int gpsState = 1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                gpsState = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (gpsState == 0) {

            DialogHelper.showQuestion(this, "You must enable GPS first", new Runnable() {
                @Override
                public void run() {
                    Intent intentEnableGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intentEnableGps);
                }
            });

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                drawCurrentLocationMarker(location);
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        });
    }

    private void drawCurrentLocationMarker(Location location) {

        LogService.info("Current location: " + location.toString());

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location))));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                STATION_ZOOM_LEVEL));
    }

    @Override
    public void onCameraChangePosition() {
        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
        double radius = Helpers.zoomLevelToRadius(cameraPosition.zoom) / 1000;
        radius = radius > CommonData.RADIUS_LIMIT ? CommonData.RADIUS_LIMIT : radius;
        searchChargeStations(new GeoFireRequest(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude), radius));
    }

    @Override
    public boolean onClusterItemClick(final ChargeStationMarker marker) {

        if (!checkWalletWithDialog()) {
            return true;
        }

        findStationByEthAddressAsync(marker.getKey());
        return true;
    }

    private void findStationByEthAddressAsync(final String ethAddress) {
        final TxWaitDialog mDialog = new TxWaitDialog(MapActivity.this, getString(R.string.loading));

        new GetStationDtoTask(ethAddress)
                .setPrepareCallback(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {
                        mDialog.show();
                    }
                })
                .setFinishCallback(new ICallbackOnFinish() {
                    @Override
                    public void onFinish() {
                        mDialog.dismiss();
                    }
                })
                .setErrorCallback(new ICallbackOnError<DatabaseError>() {
                    @Override
                    public void onError(DatabaseError message) {
                        Toast.makeText(MapActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setCompleteCallback(new ICallbackOnComplete<StationDto>() {
                    @Override
                    public void onComplete(StationDto result) {
                        if (result == null) {
                            Toast.makeText(MapActivity.this, "Couldn't find station " + ethAddress, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startActivity(new Intent(MapActivity.this, StationActivity.class)
                                .putExtra(StationActivity.ARG_STATION_KEY, ethAddress));
                    }
                })
                .executeAsync();
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

        if (mClusterManager == null) {
            return;
        }

        mClusterManager.clearItems();
        mClusterManager.cluster();

        for (final String item : mStationKeys) {
            mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, StationDto>() {
                @Override
                public String getInputData() {
                    return item;
                }

                @Override
                public void onPrepare() {

                }

                @Override
                public void onComplete(final StationDto result) {
                    if (mFilteringService.isValid(result)) {

                        mChargeHubService.getLocationAsync(new IAsyncCommand<String, GeofireDto>() {
                            @Override
                            public String getInputData() {
                                if (result == null) {
                                    return null;
                                }
                                return result.getEth_address();
                            }

                            @Override
                            public void onPrepare() {

                            }

                            @Override
                            public void onComplete(GeofireDto result) {
                                mClusterManager.addItem(new ChargeStationMarker(result.getLat(), result.getLng(), item, ChargeStationMarker.SNIPPET_CHARG));
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
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        final String scannedEthAddress = result.getContents();

        if (scannedEthAddress == null) {
            return;
        }

        findStationByEthAddressAsync(scannedEthAddress);
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

    @OnClick(R.id.btn_scan_qr_code)
    void onBtnScanQrCodeClicked() {
        new IntentIntegrator(this)
                .setPrompt("Scan charge station's QR code")
                .setBeepEnabled(false)
                .setBarcodeImageEnabled(true)
                .initiateScan();
    }

    @OnClick(R.id.btn_get_my_location)
    void onBtnGetMyLocationClicked() {
        getCurrentLocationAsync();
    }

}