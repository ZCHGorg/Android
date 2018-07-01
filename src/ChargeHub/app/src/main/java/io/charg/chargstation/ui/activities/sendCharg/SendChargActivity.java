package io.charg.chargstation.ui.activities.sendCharg;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.ui.activities.BaseAuthActivity;
import io.charg.chargstation.ui.activities.sendCharg.fragments.SelectDestinationFrg;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SendChargActivity extends BaseAuthActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private List<BaseFragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private SelectDestinationFrg mDestFrg;

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
            Toast.makeText(this, "getting destination", Toast.LENGTH_SHORT).show();
        }
    }
}
