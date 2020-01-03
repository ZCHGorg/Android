package io.charg.chargstation.ui.activities.chargeCoinService;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.ConfigDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.FeesDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.LocationDto;
import io.charg.chargstation.ui.activities.BaseActivity;
import io.charg.chargstation.ui.fragments.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChargeCoinServiceActivity extends BaseActivity {

    @BindView(R.id.tab_layout)
    public TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;

    private IChargCoinServiceApi mChargCoinServiceApi;

    @Override
    public int getResourceId() {
        return R.layout.activity_charg_coin_service;
    }

    @Override
    public void onActivate() {
        initServices();
        initView();
        loadData();
    }

    private void initView() {
        final List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new FrgRates());
        fragments.add(new FrgNodes());

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return fragments.get(position).getTitle();
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        };

        mViewPager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initServices() {
        mChargCoinServiceApi = ApiProvider.getChargCoinServiceApi();
    }

    private void loadData() {
        //loadConfigAsync();
        //loadLocationAsync();
        //loadFeesAsync();
    }

    private void loadLocationAsync() {
        mChargCoinServiceApi.getLocation().enqueue(new Callback<LocationDto>() {
            @Override
            public void onResponse(@NonNull Call<LocationDto> call, @NonNull Response<LocationDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                Toast.makeText(ChargeCoinServiceActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<LocationDto> call, @NonNull Throwable t) {
                Toast.makeText(ChargeCoinServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFeesAsync() {
        mChargCoinServiceApi.getFees().enqueue(new Callback<Map<String, FeesDto>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, FeesDto>> call, @NonNull Response<Map<String, FeesDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                Toast.makeText(ChargeCoinServiceActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, FeesDto>> call, @NonNull Throwable t) {
                Toast.makeText(ChargeCoinServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadConfigAsync() {
        ApiProvider.getChargCoinServiceApi().getConfig().enqueue(new Callback<ConfigDto>() {
            @Override
            public void onResponse(@NonNull Call<ConfigDto> call, @NonNull Response<ConfigDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                Toast.makeText(ChargeCoinServiceActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ConfigDto> call, @NonNull Throwable t) {
                Toast.makeText(ChargeCoinServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
