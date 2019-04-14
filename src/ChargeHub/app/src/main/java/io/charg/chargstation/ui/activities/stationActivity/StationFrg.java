package io.charg.chargstation.ui.activities.stationActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.models.firebase.GeofireDto;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.root.IStationFrgListener;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.FilteringService;
import io.charg.chargstation.services.remote.contract.dto.SwitchesDto;
import io.charg.chargstation.services.remote.contract.tasks.GetAuthorizeTask;
import io.charg.chargstation.services.remote.contract.tasks.GetChargingSwitchesTask;
import io.charg.chargstation.services.remote.contract.tasks.GetParkingSwitchesTask;
import io.charg.chargstation.services.remote.firebase.ChargeHubService;
import io.charg.chargstation.services.remote.firebase.tasks.GetStationDtoTask;
import io.charg.chargstation.ui.activities.chargingActivity.ChargingActivity;
import io.charg.chargstation.ui.activities.chargingCCActivity.ChargingCCActivity;
import io.charg.chargstation.ui.activities.parkingActivity.ParkingActivity;
import io.charg.chargstation.ui.dialogs.SelectPaymentDialog;
import io.charg.chargstation.ui.dialogs.TxWaitDialog;
import io.charg.chargstation.ui.fragments.BaseFragment;

/**
 * Created by oleg on 04.11.2017.
 */

public class StationFrg extends BaseFragment {

    private IStationFrgListener mStationFrgListener;
    private ChargeHubService mChargeHubService;
    private FilteringService mFilterService;

    private String mStationEthAddress;
    private StationDto mStation;

    @BindView(R.id.tv_eth_address)
    TextView mTvEthAddress;

    @BindView(R.id.tv_auth_status)
    TextView mTvAuthStatus;

    @BindView(R.id.tv_charge_status)
    TextView mTvChargeStatus;

    @BindView(R.id.tv_parking_status)
    TextView mTvParkingStatus;

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

    @Override
    protected int getResourceId() {
        return R.layout.frg_station;
    }

    @Override
    protected void onShows() {
        readArgs();
        initServices();
        loadStationDtoAsync();
        loadSmartContractAsync();
    }

    private void loadSmartContractAsync() {

        new GetAuthorizeTask(getActivity(), mStationEthAddress)
                .setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {
                        mTvAuthStatus.setText(getText(R.string.loading));
                    }
                })
                .setCompleteListener(new ICallbackOnComplete<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        mTvAuthStatus.setText(result ? "Authorized" : "Not authorized");
                        mTvAuthStatus.setCompoundDrawablesWithIntrinsicBounds(result ? R.drawable.ic_ok : R.drawable.ic_error, 0, 0, 0);

                    }
                })
                .setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                        mTvAuthStatus.setText(message);
                        mTvAuthStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
                    }
                })
                .executeAsync();

        new GetChargingSwitchesTask(getActivity(), mStationEthAddress)
                .setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {
                        mTvChargeStatus.setText(R.string.loading);
                    }
                })
                .setCompleteListener(new ICallbackOnComplete<SwitchesDto>() {
                    @Override
                    public void onComplete(SwitchesDto result) {
                        if (!result.Initialized) {
                            mTvChargeStatus.setText(R.string.free);
                            mTvChargeStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        } else {
                            mTvChargeStatus.setText(String.format(Locale.getDefault(), "%s\nby %s\ntill %s",
                                    getString(R.string.busy),
                                    StringHelper.getShortEthAddress(result.Node),
                                    StringHelper.getTimeStr(new Date(result.EndTime.longValue() * 1000))));
                            mTvChargeStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
                        }
                    }
                })
                .setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                        mTvChargeStatus.setText(message);
                        mTvChargeStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
                    }
                })
                .executeAsync();

        new GetParkingSwitchesTask(getActivity(), mStationEthAddress)
                .setPrepareListener(new ICallbackOnPrepare() {
                    @Override
                    public void onPrepare() {
                        mTvParkingStatus.setText(R.string.loading);
                    }
                })
                .setCompleteListener(new ICallbackOnComplete<SwitchesDto>() {
                    @Override
                    public void onComplete(SwitchesDto result) {
                        if (!result.Initialized) {
                            mTvParkingStatus.setText(R.string.free);
                            mTvParkingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        } else {
                            mTvParkingStatus.setText(String.format(Locale.getDefault(), "%s\nby %s\ntill %s",
                                    getString(R.string.busy),
                                    StringHelper.getShortEthAddress(result.Node),
                                    StringHelper.getTimeStr(new Date(result.EndTime.longValue() * 1000))));
                            mTvParkingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
                        }
                    }
                })
                .setErrorListener(new ICallbackOnError<String>() {
                    @Override
                    public void onError(String message) {
                        mTvParkingStatus.setText(message);
                        mTvParkingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
                    }
                })
                .executeAsync();
    }

    @Override
    public CharSequence getTitle() {
        return "Station";
    }

    private void readArgs() {
        mStationEthAddress = getArguments().getString(StationActivity.ARG_STATION_KEY);
    }

    private void initServices() {
        mChargeHubService = new ChargeHubService();
        mFilterService = new FilteringService(getContext());
    }

    private void loadStationDtoAsync() {
        final TxWaitDialog mDialog = new TxWaitDialog(getContext());

        new GetStationDtoTask(mStationEthAddress)
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
                .setCompleteCallback(new ICallbackOnComplete<StationDto>() {
                    @Override
                    public void onComplete(StationDto result) {
                        mStation = result;
                        refreshUI();
                    }
                })
                .setErrorCallback(new ICallbackOnError<DatabaseError>() {
                    @Override
                    public void onError(DatabaseError message) {
                        Toast.makeText(getContext(), message.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .executeAsync();
    }

    private void refreshUI() {

        mTvEthAddress.setText(StringHelper.getShortEthAddress(mStationEthAddress));

        if (mStation == null) {
            Toast.makeText(getContext(), String.format("Couldn't find charge station with eth address: %s", StringHelper.getShortEthAddress(mStationEthAddress)), Toast.LENGTH_SHORT).show();
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

        SelectPaymentDialog dialog = new SelectPaymentDialog(getContext());
        dialog.setCreditCardClickListener(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getActivity(), ChargingCCActivity.class)
                        .putExtra(ChargingCCActivity.KEY_ETH_ADDRESS, mStationEthAddress));

            }
        });
        dialog.setChargCoinClickListener(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getActivity(), ChargingActivity.class)
                        .putExtra(ChargingActivity.KEY_ETH_ADDRESS, mStationEthAddress));
            }
        });
        dialog.show();

    }

    @OnClick(R.id.btn_park)
    void onBtnParkClick() {
        startActivity(new Intent(getActivity(), ParkingActivity.class)
                .putExtra(ParkingActivity.KEY_ETH_ADDRESS, mStationEthAddress));
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
                return mStationEthAddress;
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
