package io.charg.chargstation.ui.activities.chargeCoinService;

import android.support.annotation.NonNull;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.RatesDto;
import io.charg.chargstation.ui.fragments.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgRates extends BaseFragment {

    private IChargCoinServiceApi mChargCoinServiceApi;

    @BindView(R.id.tv_rate_btc)
    public TextView mTvRateBtc;

    @BindView(R.id.tv_rate_usd)
    public TextView mTvRateUsd;

    @BindView(R.id.tv_rate_ltc)
    public TextView mTvRateLtc;

    @BindView(R.id.tv_rate_eos)
    public TextView mTvRateEos;

    @Override
    protected int getResourceId() {
        return R.layout.frg_rates;
    }

    @Override
    protected void onShows() {
        initServices();
        loadRates();
    }

    private void initServices() {
        mChargCoinServiceApi = ApiProvider.getChargCoinServiceApi();
    }

    private void loadRates() {
        mChargCoinServiceApi.getRates().enqueue(new Callback<RatesDto>() {
            @Override
            public void onResponse(@NonNull Call<RatesDto> call, @NonNull Response<RatesDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                mTvRateBtc.setText(String.valueOf(response.body().Btc));
                mTvRateUsd.setText(String.valueOf(response.body().Usd));
                mTvRateLtc.setText(String.valueOf(response.body().Ltc));
                mTvRateEos.setText(String.valueOf(response.body().Eos));
            }

            @Override
            public void onFailure(@NonNull Call<RatesDto> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public CharSequence getTitle() {
        return "Rates";
    }
}
