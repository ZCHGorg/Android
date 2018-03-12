package io.charg.chargstation.ui.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.root.IStationFrgListener;
import io.charg.chargstation.ui.fragments.BaseFragment;
import io.charg.chargstation.ui.fragments.ChargeFrg;
import io.charg.chargstation.ui.fragments.StationFrg;

/**
 * Created by worker on 03.11.2017.
 */

public class StationActivity extends BaseActivity implements IStationFrgListener {

    public static final String ARG_STATION_KEY = "ARG_STATION_KEY";
    private String mStationKey;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public int getResourceId() {
        return R.layout.activity_station;
    }

    @Override
    public void onActivate() {
        initToolbar();
        readIntent();
        initTabLayout();
        initViewPager();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initViewPager() {
        final List<BaseFragment> fragments = new ArrayList<BaseFragment>() {{
            add(StationFrg.newInstance(mStationKey));
            add(ChargeFrg.newInstance(mStationKey));
        }};

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return fragments.get(position).getTitle();
            }
        });
    }

    private void readIntent() {
        mStationKey = getIntent().getStringExtra(ARG_STATION_KEY);
    }


    @Override
    public void onChargeBtnClick() {
        mViewPager.setCurrentItem(1, true);
    }
}
