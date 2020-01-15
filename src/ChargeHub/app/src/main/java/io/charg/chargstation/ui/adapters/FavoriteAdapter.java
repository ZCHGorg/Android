package io.charg.chargstation.ui.adapters;

import android.content.Intent;
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
import io.charg.chargstation.services.remote.firebase.ChargeHubService;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.ui.activities.mapActivity.MapActivity;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<String> mItems;

    private ChargeHubService mChargeHubService;

    public FavoriteAdapter() {
        mItems = new ArrayList<>();
        mChargeHubService = new ChargeHubService();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String id = mItems.get(position);
        holder.mTvEthAddress.setText(StringHelper.getShortEthAddress(id));

        mChargeHubService.getChargeNodeAsync(new IAsyncCommand<String, StationDto>() {
            @Override
            public String getInputData() {
                return id;
            }

            @Override
            public void onPrepare() {
                holder.mTvLoading.setText(R.string.loading_details);
            }

            @Override
            public void onComplete(StationDto result) {
                holder.mTvLoading.setText(new StringBuilder()
                        .append(result.getTitle())
                        .append("; ")
                        .append(result.getAddress())
                        .append("; ")
                        .append(StringHelper.getCostStr(result.getCost_charge()))
                );
            }

            @Override
            public void onError(String error) {
                holder.mTvLoading.setText(error);
            }
        });

        holder.mBtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mChargeHubService.getLocationAsync(new IAsyncCommand<String, GeofireDto>() {
                    @Override
                    public String getInputData() {
                        return id;
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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
