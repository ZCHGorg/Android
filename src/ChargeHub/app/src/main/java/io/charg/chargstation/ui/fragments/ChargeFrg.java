package io.charg.chargstation.ui.fragments;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.api.ChargeHubService;
import io.charg.chargstation.services.helpers.DialogHelper;
import io.charg.chargstation.services.remote.contract.SmartContractManager;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.activities.StationActivity;
import io.charg.chargstation.ui.dialogs.SendChargDialog;

/**
 * Created by oleg on 04.11.2017.
 */

public class ChargeFrg extends BaseFragment {

    private ChargeHubService mChargeHubService;
    private SmartContractManager mSmartContractManager;
    private AccountService mAccountService;

    private static final int DEFAULT_CHARG_TICKS = 300000;

    private String mNodeEthAddress;
    private StationDto mChargeStation;

    @BindView(R.id.tv_location)
    TextView tvLocationName;

    @BindView(R.id.tv_station_id)
    TextView tvStationKey;

    @BindView(R.id.tv_power)
    TextView tvPower;

    @BindView(R.id.tv_cost_kwh)
    TextView tvCostKwh;

    @BindView(R.id.tv_cost_min_park)
    TextView tvCostMinPark;

    @BindView(R.id.layout_content)
    ViewGroup vgContent;

    @BindView(R.id.progress)
    ProgressBar mProgressBar;

    @BindView(R.id.tv_timer)
    TextView tvTimer;

    @BindView(R.id.btn_park)
    Button btnPark;

    @BindView(R.id.btn_stop_park)
    Button btnStopPark;

    @BindView(R.id.btn_charge)
    Button btnCharge;

    @BindView(R.id.btn_stop_charge)
    Button btnStopCharge;

    @BindView(R.id.tv_charged_time)
    TextView tvChargedTime;

    @BindView(R.id.tv_charged_price)
    TextView tvChargedPrice;

    @BindView(R.id.tv_station_state)
    TextView tvStationState;

    @BindView(R.id.tv_client_state)
    TextView tvClientState;

    @BindView(R.id.tv_client_address)
    TextView tvClientAddress;

    @BindView(R.id.btn_pay)
    Button btnPay;

    private long mChargeTicksEstimate = DEFAULT_CHARG_TICKS;
    private long mChargeTicksDone;
    private boolean mIsChargMode;

    private CountDownTimer countDownTimer;
    private ValueEventListener mNodeEventListener;

    @Override
    protected int getResourceId() {
        return R.layout.frg_charge;
    }

    @Override
    protected void onShows() {
        readArgs();
        initServices();
        initNodeAsync();
    }

    @Override
    public CharSequence getTitle() {
        return "Charg";
    }

    private void initNodeAsync() {
        mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, StationDto>() {
            @Override
            public String getInputData() {
                return mNodeEthAddress;
            }

            @Override
            public void onPrepare() {
                mProgressBar.setVisibility(View.VISIBLE);
                vgContent.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(StationDto result) {
                mChargeStation = result;
                refreshUI();

                mProgressBar.setVisibility(View.GONE);
                vgContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                vgContent.setVisibility(View.VISIBLE);
            }
        });
    }

    private void refreshUI() {
        if (mChargeStation == null) {
            Toast.makeText(getContext(), String.format("Couldn't find charge station with address: %s", StringHelper.getShortEthAddress(mNodeEthAddress)), Toast.LENGTH_SHORT).show();
            return;
        }

        tvLocationName.setText(mChargeStation.getTitle());
        tvStationKey.setText(StringHelper.getShortEthAddress(mChargeStation.getEth_address()));
        tvPower.setText(String.format("%s kW/h", mChargeStation.getPower()));
        tvCostKwh.setText(String.valueOf(mChargeStation.getCost_charge()));
        tvCostMinPark.setText(String.valueOf(mChargeStation.getCost_park()));
        tvStationState.setText(mChargeStation.getStation_state());

        if (mChargeStation.getClient_state() == null) {
            tvClientState.setText(CommonData.NOT_DEFINED);
        } else {
            tvClientState.setText(mChargeStation.getClient_state());
        }

        tvClientAddress.setText(StringHelper.getShortEthAddress(mChargeStation.getClient_address()));

        refreshTimerLabels();
    }

    private void refreshTimerLabels() {
        tvTimer.setText(getTickTime(mChargeTicksEstimate));
        tvChargedTime.setText(getTickTime(mChargeTicksDone));
        tvChargedPrice.setText(String.format("%.6f CHG", calcAmount()));
    }

    private double calcAmount() {
        return mChargeTicksDone * (mIsChargMode ? mChargeStation.getCost_charge() : mChargeStation.getCost_park()) / 60000;
    }

    private void initServices() {
        mChargeHubService = new ChargeHubService();
        mSmartContractManager = SmartContractManager.getInstance(getContext());
        mAccountService = new AccountService(getContext());
    }

    private void readArgs() {
        mNodeEthAddress = getArguments().getString(StationActivity.ARG_STATION_KEY);
    }

    public static ChargeFrg newInstance(String stationKey) {
        ChargeFrg frg = new ChargeFrg();
        Bundle args = new Bundle();
        args.putString(StationActivity.ARG_STATION_KEY, stationKey);
        frg.setArguments(args);
        return frg;
    }

    @OnClick(R.id.btn_terms)
    void onBtnTermsClick() {
        new AlertDialog.Builder(getContext())
                .setMessage(String.format("Terms and conditions:\n%s", mChargeStation.getComments()))
                .setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void enableButtons() {
        btnStopPark.setVisibility(View.GONE);
        btnStopCharge.setVisibility(View.GONE);
        btnPark.setVisibility(View.VISIBLE);
        btnCharge.setVisibility(View.VISIBLE);
        btnPay.setVisibility(View.VISIBLE);
    }

    private void disableButtons() {
        btnStopPark.setVisibility(View.GONE);
        btnStopCharge.setVisibility(View.GONE);
        btnPark.setVisibility(View.GONE);
        btnCharge.setVisibility(View.GONE);
        btnPay.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_park)
    void onBtnParkClick() {

        boolean isNowParking = mSmartContractManager.isNowParking();
        if (isNowParking) {
            DialogHelper.showQuestion(getContext(), getString(R.string.ask_parking_off), new Runnable() {
                @Override
                public void run() {
                    mSmartContractManager.parkingOffAsync(mChargeStation.getEth_address());
                }
            });
            return;
        }

        mSmartContractManager.parkingOnAsync(mChargeStation.getEth_address(), BigInteger.valueOf(mChargeTicksEstimate / 1000));

        disableButtons();
        btnStopPark.setVisibility(View.VISIBLE);
        mIsChargMode = false;

        startCounter(new Runnable() {
            @Override
            public void run() {
                enableButtons();
                if (mSmartContractManager.isNowParking()) {
                    mSmartContractManager.parkingOffAsync(mChargeStation.getEth_address());
                }
            }
        });
    }

    @OnClick(R.id.btn_stop_park)
    void onBtnStopParkClick() {
        mSmartContractManager.parkingOffAsync(mChargeStation.getEth_address());

        countDownTimer.cancel();
        enableButtons();

        mChargeTicksEstimate = DEFAULT_CHARG_TICKS;
        refreshTimerLabels();
    }

    @OnClick(R.id.btn_charge)
    void onBtnChargeClick() {

        boolean isNowCharging = mSmartContractManager.isNowCharging();
        if (isNowCharging) {
            DialogHelper.showQuestion(getContext(), getString(R.string.ask_charging_off), new Runnable() {
                @Override
                public void run() {
                    mSmartContractManager.chargeOffAsync(mChargeStation.getEth_address());
                }
            });
            return;
        }

        mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, StationDto>() {
            @Override
            public String getInputData() {
                return mChargeStation.getEth_address();
            }

            @Override
            public void onPrepare() {

            }

            @Override
            public void onComplete(StationDto result) {
                if (result.getStation_state().equals(StationDto.STATION_STATE_IDLE)) {

                    if (result.getClient_state() != null && result.getClient_state().equals(StationDto.CLIENT_STATE_REQUEST) &&
                            (result.getClient_address() != null) && result.getClient_address().equals(mAccountService.getEthAddress())) {
                        Toast.makeText(getContext(), "You allready send request station. Waiting for ready station...", Toast.LENGTH_SHORT).show();
                    } else {
                        result.setClient_state(StationDto.CLIENT_STATE_REQUEST);
                        result.setClient_address(mAccountService.getEthAddress());
                        mChargeHubService.saveNode(result.getEth_address(), result);
                        mSmartContractManager.chargeOnAsync(result.getEth_address(), BigInteger.valueOf(mChargeTicksEstimate / 1000));
                        Toast.makeText(getContext(), "Sending request to station...", Toast.LENGTH_SHORT).show();
                    }

                    refreshUI();
                    listenChargeStationEvents(result.getEth_address());

                } else {
                    Toast.makeText(getContext(), getContext().getText(R.string.is_charging) + "\r\nCurrent state is: " + result.getStation_state(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenChargeStationEvents(String nodeAddress) {
        final DatabaseReference dbRef = mChargeHubService.getChargeNodeDbRef(nodeAddress);

        if (mNodeEventListener != null) {
            dbRef.removeEventListener(mNodeEventListener);
        }

        dbRef.addValueEventListener(mNodeEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final StationDto nodeDto = dataSnapshot.getValue(StationDto.class);

                if (nodeDto.getStation_state().equals(StationDto.STATION_STATE_READY)) {
                    if (nodeDto.getClient_state().equals(StationDto.CLIENT_STATE_READY)) {
                        disableButtons();
                        btnStopCharge.setVisibility(View.VISIBLE);
                        mIsChargMode = true;

                        startCounter(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                            }
                        });
                    } else {
                        DialogHelper.showQuestion(getContext(), getString(R.string.ask_user_approvement_charge), new Runnable() {
                            @Override
                            public void run() {
                                nodeDto.setClient_state(StationDto.CLIENT_STATE_READY);
                                nodeDto.setClient_address(mAccountService.getEthAddress());
                                mChargeHubService.saveNode(mChargeStation.getEth_address(), nodeDto);
                            }
                        });
                    }
                } else if (nodeDto.getStation_state().equals(StationDto.STATION_STATE_CHARGED)) {
                    onBtnStopChargeClick();
                    nodeDto.setClient_state(StationDto.CLIENT_STATE_CHARGED);
                    nodeDto.setClient_address(mAccountService.getEthAddress());
                    mChargeHubService.saveNode(mChargeStation.getEth_address(), nodeDto);
                    dbRef.removeEventListener(mNodeEventListener);
                }

                refreshUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_stop_charge)
    void onBtnStopChargeClick() {
        mSmartContractManager.chargeOffAsync(mChargeStation.getEth_address());

        countDownTimer.cancel();
        enableButtons();

        mChargeTicksEstimate = DEFAULT_CHARG_TICKS;
        refreshTimerLabels();
    }

    private void startCounter(final Runnable runnable) {
        mChargeTicksDone = 0;

        countDownTimer = new CountDownTimer(mChargeTicksEstimate, 1000) {
            public void onTick(long millisUntilFinished) {
                mChargeTicksDone += 1000;
                mChargeTicksEstimate = millisUntilFinished;
                refreshTimerLabels();
            }

            public void onFinish() {
                mChargeTicksEstimate = 0;
                refreshTimerLabels();
                runnable.run();
            }
        }.start();
    }

    private String getTickTime(long ticks) {
        long hours = ticks / 3600000;
        long minutes = (ticks - hours * 3600000) / 60000;
        long seconds = (ticks - hours * 3600000 - minutes * 60000) / 1000;

        return String.format("%02d : %02d : %02d", hours, minutes, seconds);
    }

    @OnClick(R.id.tv_timer)
    void onTimerClick() {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mChargeTicksEstimate = hourOfDay * 3600000 + minute * 60000;
                tvTimer.setText(getTickTime(mChargeTicksEstimate));
            }
        }, 0, 0, true).show();
    }

    @OnClick(R.id.btn_pay)
    void onBtnPayClicked() {
        SendChargDialog dlg = new SendChargDialog(getContext());
        dlg.sendCharg(mChargeStation.getEth_address(), 1);
    }
}
