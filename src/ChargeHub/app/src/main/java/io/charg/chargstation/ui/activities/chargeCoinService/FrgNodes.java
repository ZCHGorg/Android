package io.charg.chargstation.ui.activities.chargeCoinService;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;
import io.charg.chargstation.ui.activities.stationServiceActivity.NodeServiceActivity;
import io.charg.chargstation.ui.fragments.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgNodes extends BaseFragment {

    private IChargCoinServiceApi mChargCoinServiceApi;

    private NodesAdapter mAdapter;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @BindView(R.id.tv_count_items)
    public TextView mTvCountItems;

    @Override
    protected int getResourceId() {
        return R.layout.frg_nodes;
    }

    @Override
    protected void onShows() {
        initServices();
        initViews();
        loadNodes();
    }

    private void initViews() {
        mAdapter = new NodesAdapter();
        mAdapter.setOnBtnPayClickListener(new NodesAdapter.IOnItemClickListener() {
            @Override
            public void onItemClicked(NodeVM nodeDto) {
                startActivity(new Intent(getContext(), NodeServiceActivity.class)
                        .putExtra(NodeServiceActivity.EXTRA_NODE_ADDRESS, nodeDto.getNodeAddress()));
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
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

                Map<String, NodeDto> content = response.body();

                mTvCountItems.setText(String.valueOf(content.size()));

                List<NodeVM> items = new ArrayList<>();
                for (String key : content.keySet()) {
                    items.add(new NodeVM(content.get(key), key));
                }
                mAdapter.updateItems(items);
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, NodeDto>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
