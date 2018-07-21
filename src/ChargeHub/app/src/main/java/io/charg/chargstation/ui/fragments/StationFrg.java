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

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.models.firebase.GeofireDto;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.IStationFrgListener;
import io.charg.chargstation.services.remote.api.ChargeHubService;
import io.charg.chargstation.services.local.FilteringService;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.activities.StationActivity;
import io.charg.chargstation.ui.activities.chargingActivity.ChargingActivity;

/**
 * Created by oleg on 04.11.2017.
 */

public class StationFrg extends BaseFragment {

    private IStationFrgListener mStationFrgListener;
    private ChargeHubService mChargeHubService;
    private FilteringService mFilterService;

    private String mNodeEthAddress;
    private StationDto mStation;

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
    protected void onShows() {
        readArgs();
        initServices();
        loadStationAsync();
    }

    @Override
    public CharSequence getTitle() {
        return "Station";
    }

    private void readArgs() {
        mNodeEthAddress = getArguments().getString(StationActivity.ARG_STATION_KEY);
    }

    private void initServices() {
        mChargeHubService = new ChargeHubService();
        mFilterService = new FilteringService(getContext());
    }

    private void loadStationAsync() {
        mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, StationDto>() {

            @Override
            public String getInputData() {
                return mNodeEthAddress;
            }

            @Override
            public void onPrepare() {
                mProgressBar.setVisibility(View.VISIBLE);
                mLayoutContent.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(StationDto result) {
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
            Toast.makeText(getContext(), String.format("Couldn't find charge station with eth address: %s", StringHelper.getShortEthAddress(mNodeEthAddress)), Toast.LENGTH_SHORT).show();
            return;
        }

        tvLocation.setText(mStation.getTitle());

        tvOnline.setText("N/A");

        tvAcceptsCharge.setText("N/A");

        tvTelephone.setText(mStation.getPhone());

        tvAddress.setText(mStation.getAddress());

        tvPorts.setText("N/A");

        tvConnectionType.setText(mFilterService.loadConnectorFilter(mStation.getConnector_type()).getTitle());

        tvPower.setText(String.format("%s KW", mStation.getPower()));

        tvNotes.setText(mStation.getComments());

        //TODO loading image
/*        if (mStation.getMediaItems().size() > 0) {
            Glide.with(this).load(mStation.getMediaItems().get(0).getItemThumbnailURL()).into(ivStation);
        }*/
    }

    @OnClick(R.id.btn_charge)
    void onBtnChargeClick() {
        startActivity(new Intent(getActivity(), ChargingActivity.class)
                .putExtra(ChargingActivity.KEY_ETH_ADDRESS, mNodeEthAddress));
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

        mChargeHubService.getLocationAsync(new IAsyncCommand<String, GeofireDto>() {
            @Override
            public String getInputData() {
                return mNodeEthAddress;
            }

            @Override
            public void onPrepare() {

            }

            @Override
            public void onComplete(GeofireDto result) {
                try {
                    String packageName = "com.google.android.apps.maps";
                    String query = String.format("google.navigation:q=%s,%s", String.valueOf(result.getLat()), String.valueOf(result.getLng()));
                    Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(query));
                    startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public static StationFrg newInstance(String stationKey) {
        StationFrg frg = new StationFrg();
        Bundle args = new Bundle();
        args.putString(StationActivity.ARG_STATION_KEY, stationKey);
        frg.setArguments(args);
        return frg;
    }
}
