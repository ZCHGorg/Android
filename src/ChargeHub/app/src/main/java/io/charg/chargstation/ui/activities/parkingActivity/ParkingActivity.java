package io.charg.chargstation.ui.activities.parkingActivity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shuhart.stepview.StepView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.contract.tasks.ChargeOffForceTask;
import io.charg.chargstation.services.remote.contract.tasks.ChargeOnForceTask;
import io.charg.chargstation.ui.activities.BaseActivity;
import io.charg.chargstation.ui.fragments.BaseNavFragment;
import io.charg.chargstation.ui.fragments.ExecuteContractFuncFrg;
import io.charg.chargstation.ui.fragments.SelectDestinationFrg;

public class ParkingActivity extends BaseActivity {

    public static final String KEY_ETH_ADDRESS = "KEY_ETH_ADDRESS";

    @BindView(R.id.step_view)
    StepView mStepView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private List<BaseNavFragment> mFragments;
    private FragmentPagerAdapter mAdapter;

    private String mDestAddress;
    private BigInteger mTime = BigInteger.valueOf(0L);

    private SelectDestinationFrg mDestFrg;
    private SelectParkingTimeFrg mTimeFrg;
    private ExecuteContractFuncFrg mParkOnFrg;
    private ParkingFrg mParkingFrg;
    private ExecuteContractFuncFrg mParkOffFrg;

    @Override
    public int getResourceId() {
        return R.layout.activity_parking;
    }

    @Override
    public void onActivate() {
        readIntent();
        initServices();
        initToolbar();
        initStepView();
        initViewPager();
        navigateTo(mDestFrg);
    }

    private void navigateTo(BaseNavFragment frg) {
        int position = mAdapter.getItemPosition(frg);
        mViewPager.setCurrentItem(position, true);
        mStepView.go(position, true);
        findViewById(R.id.btn_next).setVisibility(frg.canNext() ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_back).setVisibility(frg.canBack() ? View.VISIBLE : View.GONE);
    }

    private void initServices() {

    }

    private void readIntent() {
        mDestAddress = getIntent().getStringExtra(KEY_ETH_ADDRESS);
    }

    private void initViewPager() {
        mFragments = new ArrayList<>();

        mDestFrg = SelectDestinationFrg.newInstance(mDestAddress);
        mFragments.add(mDestFrg);

        mTimeFrg = SelectParkingTimeFrg.newInstance(mDestAddress);
        mFragments.add(mTimeFrg);

        mParkOnFrg = new ExecuteContractFuncFrg();
        mFragments.add(mParkOnFrg);

        mParkingFrg = new ParkingFrg();
        mFragments.add(mParkingFrg);

        mParkOffFrg = new ExecuteContractFuncFrg();
        mFragments.add(mParkOffFrg);

        mViewPager.setAdapter(mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                BaseNavFragment frg = (BaseNavFragment) object;
                return mFragments.indexOf(frg);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initStepView() {
        mStepView.setSteps(new ArrayList<String>() {{
            add(getString(R.string.station));
            add(getString(R.string.time));
            add(getString(R.string.on));
            add(getString(R.string.parking));
            add(getString(R.string.off));
        }});
    }

    @OnClick(R.id.btn_back)
    void onBtnBackClicked() {
        int position = mViewPager.getCurrentItem() - 1;
        if (position >= 0) {
            navigateTo((BaseNavFragment) mAdapter.getItem(position));
        }
    }

    @OnClick(R.id.btn_next)
    void onBtnNextClicked() {
        Fragment mCurrentFrg = mAdapter.getItem(mViewPager.getCurrentItem());

        if (mCurrentFrg.equals(mDestFrg)) {
            if (!mDestFrg.isValid()) {
                return;
            }
            mDestAddress = mDestFrg.getDestinationEth();
            mTimeFrg.setDestAddress(mDestAddress);
            mTimeFrg.invalidate();
            navigateTo(mTimeFrg);
        } else if (mCurrentFrg.equals(mTimeFrg)) {
            if (!mTimeFrg.isValid()) {
                return;
            }
            mTime = mTimeFrg.getTimeSeconds();

            mParkOnFrg.setOperationName(getString(R.string.on));
            mParkOnFrg.setTask(new ChargeOnForceTask(ParkingActivity.this, mDestAddress, mTime));
            mParkOnFrg.invalidate();
            navigateTo(mParkOnFrg);
        } else if (mCurrentFrg.equals(mParkOnFrg)) {
            if (!mParkOnFrg.isValid()) {
                return;
            }
            mParkingFrg.invalidate();
            navigateTo(mParkingFrg);
        } else if (mCurrentFrg.equals(mParkingFrg)) {
            if (!mParkingFrg.isValid()) {
                return;
            }

            mParkOffFrg.setOperationName(getString(R.string.charge_off));
            mParkOffFrg.setTask(new ChargeOffForceTask(ParkingActivity.this, mDestAddress));
            mParkOffFrg.invalidate();
            navigateTo(mParkOffFrg);
        }
    }

}