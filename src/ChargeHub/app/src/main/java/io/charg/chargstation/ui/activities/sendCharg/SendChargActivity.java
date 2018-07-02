package io.charg.chargstation.ui.activities.sendCharg;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.ui.activities.BaseAuthActivity;
import io.charg.chargstation.ui.activities.sendCharg.fragments.ConfirmFrg;
import io.charg.chargstation.ui.activities.sendCharg.fragments.SelectAmountFrg;
import io.charg.chargstation.ui.activities.sendCharg.fragments.SelectDestinationFrg;
import io.charg.chargstation.ui.activities.sendCharg.fragments.SendingFrg;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SendChargActivity extends BaseAuthActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private List<BaseFragment> mFragments;
    private FragmentPagerAdapter mAdapter;

    private SelectDestinationFrg mDestFrg;
    private SelectAmountFrg mAmountFrg;
    private ConfirmFrg mConfirmFrg;
    private SendingFrg mSendingFrg;

    private String mDestEthAddress;
    private float mAmount;


    @Override
    public int getResourceId() {
        return R.layout.activity_send_charg;
    }

    @Override
    public void onActivate() {
        initToolbar();
        initViewPager();
    }

    private void initViewPager() {

        mFragments = new ArrayList<>();
        mFragments.add(mDestFrg = new SelectDestinationFrg());
        mFragments.add(mAmountFrg = new SelectAmountFrg());
        mFragments.add(mConfirmFrg = new ConfirmFrg());
        mFragments.add(mSendingFrg = new SendingFrg());

        mViewPager.setAdapter(mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
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

    @OnClick(R.id.btn_next)
    void onBtnNextClicked() {
        if (mAdapter.getItem(mViewPager.getCurrentItem()).equals(mDestFrg)) {
            if (!mDestFrg.isValid()) {
                return;
            }
            mDestEthAddress = mDestFrg.getDestinationEth();
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            return;
        }

        if (mAdapter.getItem(mViewPager.getCurrentItem()).equals(mAmountFrg)) {
            if (!mAmountFrg.isValid()) {
                return;
            }
            mAmount = mAmountFrg.getAmount();
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            return;
        }

        if (mAdapter.getItem(mViewPager.getCurrentItem()).equals(mConfirmFrg)) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            mSendingFrg.execute();
            return;
        }

        if (mAdapter.getItem(mViewPager.getCurrentItem()).equals(mSendingFrg)) {

            return;
        }
    }
}
