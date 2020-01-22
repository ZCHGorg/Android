package io.charg.chargstation.ui.activities.chargeCoinService;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.charg.chargstation.R;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;

public class NodeAssetAdapter extends RecyclerView.Adapter<NodeAssetAdapter.ViewHolder> {

    private final List<NodeDto.AssetDto> mItems;

    public NodeAssetAdapter() {
        mItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node_asset, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NodeDto.AssetDto item = mItems.get(position);

        holder.mTvAssetName.setText(item.Name);
        holder.mTvAssetSlots.setText(String.valueOf(item.Slots));
        holder.mTvAssetFree.setText(String.valueOf(item.Free));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateItems(List<NodeDto.AssetDto> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_asset_name)
        TextView mTvAssetName;

        @BindView(R.id.tv_asset_slots)
        TextView mTvAssetSlots;

        @BindView(R.id.tv_asset_free)
        TextView mTvAssetFree;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
