package io.charg.chargstation.ui.activities.chargeCoinService;

import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Map;

import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.ConfigDto;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.FeesDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.LocationDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.RatesDto;
import io.charg.chargstation.ui.activities.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChargeCoinServiceActivity extends BaseActivity {

    private IChargCoinServiceApi mChargCoinServiceApi;

    @Override
    public int getResourceId() {
        return R.layout.activity_charg_coin_service;
    }

    @Override
    public void onActivate() {
        initServices();
        loadData();
    }

    private void initServices() {
        mChargCoinServiceApi = ApiProvider.getChargCoinServiceApi();
    }

    private void loadData() {
        loadConfigAsync();
        loadLocationAsync();
        loadFeesAsync();
        loadRates();
        loadNodes();
    }

    private void loadNodes() {
        mChargCoinServiceApi.getNodes(0.0, 0.0, 0).enqueue(new Callback<Map<String, NodeDto>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, NodeDto>> call, @NonNull Response<Map<String, NodeDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                Toast.makeText(ChargeCoinServiceActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(@NonNull Call<Map<String, NodeDto>> call, @NonNull Throwable t) {
                Toast.makeText(ChargeCoinServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRates() {
        mChargCoinServiceApi.getRates().enqueue(new Callback<RatesDto>() {
            @Override
            public void onResponse(@NonNull Call<RatesDto> call, @NonNull Response<RatesDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                Toast.makeText(ChargeCoinServiceActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<RatesDto> call, @NonNull Throwable t) {
                Toast.makeText(ChargeCoinServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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
