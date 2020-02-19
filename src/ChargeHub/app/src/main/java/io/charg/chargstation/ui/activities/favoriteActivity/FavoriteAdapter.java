package io.charg.chargstation.ui.activities.favoriteActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.charg.chargstation.R;
import io.charg.chargstation.models.firebase.GeofireDto;
import io.charg.chargstation.models.firebase.StationDto;
import io.charg.chargstation.root.IAsyncCommand;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;
import io.charg.chargstation.services.remote.firebase.ChargeHubService;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.activities.mapActivity.MapActivity;
import io.charg.chargstation.ui.activities.nodeServiceActivity.NodeServiceActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private final IChargCoinServiceApi mApi;
    private final List<String> mItems;
    private final ChargeHubService mChargeHubService;

    public FavoriteAdapter(Context context) {
        mItems = new ArrayList<>();
        mChargeHubService = new ChargeHubService();
        mApi = ApiProvider.getChargCoinServiceApi(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String item = mItems.get(position);
        holder.mTvEthAddress.setText(StringHelper.getShortEthAddress(item));

        mApi.getNodeStatus(item).enqueue(new Callback<NodeDto>() {
            @Override
            public void onResponse(@NonNull Call<NodeDto> call, @NonNull Response<NodeDto> response) {
                NodeDto body = response.body();
                if (body == null) {
                    return;
                }

                holder.mTvLoading.setText(new StringBuilder()
                        .append(body.Name));

            }

            @Override
            public void onFailure(@NonNull Call<NodeDto> call, @NonNull Throwable t) {
                Toast.makeText(holder.itemView.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), NodeServiceActivity.class)
                        .putExtra(NodeServiceActivity.EXTRA_NODE_ADDRESS, item));
            }
        });

        holder.mBtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mChargeHubService.getLocationAsync(new IAsyncCommand<String, GeofireDto>() {
                    @Override
                    public String getInputData() {
                        return item;
                    }

                    @Override
                    public void onPrepare() {

                    }

                    @Override
                    public void onComplete(GeofireDto result) {
                        if (result == null) {
                            Toast.makeText(holder.itemView.getContext(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), MapActivity.class)
                                .putExtra(MapActivity.EXTRA_LAT, result.getLat())
                                .putExtra(MapActivity.EXTRA_LNG, result.getLng())
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<String> ids) {
        mItems.clear();
        mItems.addAll(ids);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_eth_address)
        TextView mTvEthAddress;

        @BindView(R.id.tv_loading)
        TextView mTvLoading;

        @BindView(R.id.btn_map)
        View mBtnMap;

        @BindView(R.id.btn_open)
        View mBtnOpen;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
