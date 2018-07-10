package io.charg.chargstation.ui.activities.chargeActivity.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.models.ChargingSwitchesDto;
import io.charg.chargstation.services.remote.contract.tasks.GetChargingSwitchesTask;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class ChargingFrg extends BaseFragment {

    private boolean mEnded;

    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;

    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;

    @BindView(R.id.lt_charging)
    View mCharging;

    @BindView(R.id.lt_charged)
    View mCharged;

    @BindView(R.id.tv_time_left)
    TextView mTvTimeLeft;

    @BindView(R.id.tv_time_left_label)
    TextView mTvTimeLeftLabel;

    @BindView(R.id.btn_stop_charge)
    Button mBtnStopCharge;

    private AccountService mAccountService;

    private ChargingSwitchesDto mSwitchesDto;

    @Override
    protected int getResourceId() {
        return R.layout.frg_charging;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onShows() {
        initServices();
    }

    private void initServices() {
        mAccountService = new AccountService(getContext());
    }

    private void startChargeUI() {
        mCharging.setVisibility(View.VISIBLE);
        mCharged.setVisibility(View.INVISIBLE);
        mBtnStopCharge.setVisibility(View.VISIBLE);
        mTvTimeLeft.setVisibility(View.VISIBLE);
        mTvTimeLeftLabel.setVisibility(View.VISIBLE);

        showCharging();
    }

    private void showCharging() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (mEnded) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                endChargeUI();
                            }
                        });
                        break;
                    }
                    final long curTime = Calendar.getInstance().getTimeInMillis();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvTimeLeft.setText(String.format(Locale.getDefault(), "%d seconds",
                                    mSwitchesDto.EndTime.longValue() - curTime / 1000));
                        }
                    });

                    if (curTime > mSwitchesDto.EndTime.longValue() * 1000L) {
                        mEnded = true;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void endChargeUI() {
        mCharging.setVisibility(View.INVISIBLE);
        mCharged.setVisibility(View.VISIBLE);
        mBtnStopCharge.setVisibility(View.GONE);
        mTvTimeLeft.setVisibility(View.GONE);
        mTvTimeLeftLabel.setVisibility(View.GONE);
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @OnClick(R.id.btn_stop_charge)
    void onBtnChargeStop() {
        mEnded = true;
    }

    public void invalidate() {
        loadDataAsync();
    }

    private void loadDataAsync() {
        GetChargingSwitchesTask task = new GetChargingSwitchesTask(getActivity(), mAccountService.getEthAddress());
        task.setCompleteListener(new ICallbackOnComplete<ChargingSwitchesDto>() {
            @Override
            public void onComplete(ChargingSwitchesDto result) {
                mSwitchesDto = result;
                mTvStartTime.setText(new Date(result.StartTime.longValue() * 1000L).toString());
                mTvEndTime.setText(new Date(result.EndTime.longValue() * 1000L).toString());

                startChargeUI();
            }
        });
        task.executeAsync();
    }

    public boolean isValid() {
        if (!mEnded) {
            Toast.makeText(getActivity(), R.string.you_must_stop_charging, Toast.LENGTH_SHORT).show();
        }
        return mEnded;
    }
}
