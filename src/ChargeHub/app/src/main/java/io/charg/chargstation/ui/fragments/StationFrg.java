package io.charg.chargstation.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.models.firebase.ChargeStation;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.IStationFrgListener;
import io.charg.chargstation.services.ChargeHubService;
import io.charg.chargstation.services.FilteringService;
import io.charg.chargstation.ui.activities.StationActivity;

/**
 * Created by oleg on 04.11.2017.
 */

public class StationFrg extends BaseFragment {

    private IStationFrgListener mStationFrgListener;
    private ChargeHubService mChargeHubService;
    private FilteringService mFilterService;

    private String mStationKey;
    private ChargeStation mStation;

    @BindView(R.id.tv_location)
    TextView tvLocation;

    @BindView(R.id.tv_online)
    TextView tvOnline;

    @BindView(R.id.tv_accepts_charge)
    TextView tvAcceptsCharge;

    @BindView(R.id.tv_telephone)
    TextView tvTelephone;

    @BindView(R.id.tv_address)
    TextView tvAddress;

    @BindView(R.id.tv_ports)
    TextView tvPorts;

    @BindView(R.id.tv_power)
    TextView tvPower;

    @BindView(R.id.tv_notes)
    TextView tvNotes;

    @BindView(R.id.tv_connection_type)
    TextView tvConnectionType;

    @BindView(R.id.iv_station)
    ImageView ivStation;

    @BindView(R.id.progress)
    ProgressBar mProgressBar;

    @BindView(R.id.layout_content)
    ViewGroup mLayoutContent;

    @Override
    protected int getResourceId() {
        return R.layout.frg_station;
    }

    @Override
    protected void onActivate() {
        readArgs();
        initServices();
        loadStationAsync();
    }

    @Override
    public CharSequence getTitle() {
        return "Station";
    }

    private void readArgs() {
        mStationKey = getArguments().getString(StationActivity.ARG_STATION_KEY);
    }

    private void initServices() {
        mChargeHubService = new ChargeHubService();
        mFilterService = new FilteringService(getContext());
    }

    private void loadStationAsync() {
        mChargeHubService.getChargeStationAsync(new IAsyncCommand<String, ChargeStation>() {

            @Override
            public String getInputData() {
                return mStationKey;
            }

            @Override
            public void onPrepare() {
                mProgressBar.setVisibility(View.VISIBLE);
                mLayoutContent.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(ChargeStation result) {
                mStation = result;
                refreshUI();

                mProgressBar.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                mProgressBar.setVisibility(View.GONE);
                mLayoutContent.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUI() {

        if (mStation == null) {
            Toast.makeText(getContext(), "Couldn't find charge station", Toast.LENGTH_SHORT).show();
        }

        tvLocation.setText(mStation.getAddressInfo().getTitle());

        tvOnline.setText("N/A");

        tvAcceptsCharge.setText("N/A");

        String telephone = "";
        telephone += mStation.getAddressInfo().getContactTelephone1();
        if (!mStation.getAddressInfo().getContactTelephone2().isEmpty()) {
            telephone += "\n" + mStation.getAddressInfo().getContactTelephone2();
        }
        tvTelephone.setText(telephone);

        String address = "";
        address += mStation.getAddressInfo().getAddressLine1();
        if (!mStation.getAddressInfo().getAddressLine2().isEmpty()) {
            address += "\n" + mStation.getAddressInfo().getAddressLine2();
        }
        tvAddress.setText(address);

        tvPorts.setText(String.valueOf(mStation.getNumberOfPoints()));

        tvConnectionType.setText(mFilterService.loadConnectorFilter(mStation.getConnections().get(0).getConnectionTypeID()).getTitle());

        tvPower.setText(String.format("%s KW", mStation.getConnections().get(0).getPowerKW()));

        tvNotes.setText(mStation.getGeneralComments());

        if (mStation.getMediaItems().size() > 0) {
            Glide.with(this).load(mStation.getMediaItems().get(0).getItemThumbnailURL()).into(ivStation);
        }
    }

    @OnClick(R.id.btn_charge)
    void onBtnChargeClick() {
        mStationFrgListener.onChargeBtnClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mStationFrgListener = (IStationFrgListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IStationFrgListener");
        }
    }

    @OnClick(R.id.btn_directions)
    void onBtnDirectionsClick() {
        try {
            String packageName = "com.google.android.apps.maps";
            String query = String.format("google.navigation:q=%s,%s", mStation.getAddressInfo().getLatitude(), mStation.getAddressInfo().getLongitude());
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(query));
            startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static StationFrg newInstance(String stationKey) {
        StationFrg frg = new StationFrg();
        Bundle args = new Bundle();
        args.putString(StationActivity.ARG_STATION_KEY, stationKey);
        frg.setArguments(args);
        return frg;
    }
}
