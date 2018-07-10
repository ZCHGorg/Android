package io.charg.chargstation.ui.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.root.IStationFrgListener;
import io.charg.chargstation.services.remote.api.ChargeHubService;
import io.charg.chargstation.services.local.FavouriteStationsRepository;
import io.charg.chargstation.ui.fragments.BaseFragment;
import io.charg.chargstation.ui.fragments.ChargeFrg;
import io.charg.chargstation.ui.fragments.StationFrg;

/**
 * Created by worker on 03.11.2017.
 */

public class StationActivity extends BaseActivity implements IStationFrgListener {

    public static final String ARG_STATION_KEY = "ARG_STATION_KEY";
    private StationDto mStation;
    private String mEthAddress;

    private ChargeHubService mChargeHubService;
    private FavouriteStationsRepository mFavouriteService;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private MenuItem menuFavourite;

    @Override
    public int getResourceId() {
        return R.layout.activity_station;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
        readIntentAsync();
        initTabLayout();
    }

    private void refreshMenu() {
        if (menuFavourite == null) {
            return;
        }
        menuFavourite.setIcon(mFavouriteService.isFavourite(mEthAddress)
                ? R.drawable.ic_favorite
                : R.drawable.ic_favorite_border);
    }

    private void initServices() {
        mChargeHubService = new ChargeHubService();
        mFavouriteService = new FavouriteStationsRepository(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuFavourite = menu.add(null);
        menuFavourite.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuFavourite.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (mFavouriteService.isFavourite(mEthAddress)) {
                    mFavouriteService.removeFromFavorites(mEthAddress);
                } else {
                    mFavouriteService.addToFavorites(mEthAddress);
                }
                refreshMenu();
                return false;
            }
        });
        refreshMenu();

        return super.onCreateOptionsMenu(menu);
    }

    private void initTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initViewPager() {
        if (mStation == null) {
            return;
        }

        final List<BaseFragment> fragments = new ArrayList<BaseFragment>() {{
            add(StationFrg.newInstance(mEthAddress));
            add(ChargeFrg.newInstance(mEthAddress));
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

    private void readIntentAsync() {
        mEthAddress = getIntent().getStringExtra(ARG_STATION_KEY);

        mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, StationDto>() {

            @Override
            public String getInputData() {
                return mEthAddress;
            }

            @Override
            public void onPrepare() {

            }

            @Override
            public void onComplete(StationDto result) {
                if (result == null) {
                    finish();
                    return;
                }
                mStation = result;
                initViewPager();
                refreshMenu();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(StationActivity.this, error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    @Override
    public void onChargeBtnClick() {
        mViewPager.setCurrentItem(1, true);
    }
}
