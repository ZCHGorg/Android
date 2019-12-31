package io.charg.chargstation.ui.activities.chargeCoinService;

import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Map;

import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;
import io.charg.chargstation.ui.fragments.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgNodes extends BaseFragment {

    private IChargCoinServiceApi mChargCoinServiceApi;

    @Override
    protected int getResourceId() {
        return R.layout.frg_nodes;
    }

    @Override
    protected void onShows() {
        initServices();
        loadNodes();
    }

    private void initServices() {
        mChargCoinServiceApi = ApiProvider.getChargCoinServiceApi();
    }

    @Override
    public CharSequence getTitle() {
        return "Nodes";
    }

    private void loadNodes() {
        mChargCoinServiceApi.getNodes(0.0, 0.0, 0).enqueue(new Callback<Map<String, NodeDto>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, NodeDto>> call, @NonNull Response<Map<String, NodeDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                Toast.makeText(getContext(), response.body().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(@NonNull Call<Map<String, NodeDto>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
