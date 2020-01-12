package io.charg.chargstation.ui.activities.chargeCoinService;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
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
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.BestSellOrderDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.PaymentDataDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {

    private final List<NodeVM> mItems;

    private IOnItemClickListener mOnItemClickListener;

    public NodesAdapter() {
        mItems = new ArrayList<>();
    }

    @SuppressLint("InflateParams")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NodeVM item = mItems.get(position);

        holder.mTvEthAddress.setText(item.getNodeAddress());
        holder.mTvName.setText(item.getNodeDto().Name);
        holder.mTvLocation.setText(item.getNodeDto().Location);
        holder.mTvPhone.setText(item.getNodeDto().Phone);
        holder.mTvConnector.setText(item.getNodeDto().Connector);
        holder.mTvPower.setText(item.getNodeDto().PowerStr);
        holder.mTvLatitude.setText(String.valueOf(item.getNodeDto().Latitude));
        holder.mTvLongitude.setText(String.valueOf(item.getNodeDto().Longitude));
        holder.mTvConnected.setText(String.valueOf(item.getNodeDto().Connected));
        holder.mTvIp.setText(item.getNodeDto().Ip);

        holder.mBtnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mBtnHide.setVisibility(View.VISIBLE);
                holder.mBtnShow.setVisibility(View.GONE);
                holder.mLayoutCollapsed.setVisibility(View.VISIBLE);
            }
        });

        holder.mBtnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mBtnHide.setVisibility(View.GONE);
                holder.mBtnShow.setVisibility(View.VISIBLE);
                holder.mLayoutCollapsed.setVisibility(View.GONE);
            }
        });

        if (item.getNodeDto().Assets != null) {
            NodeAssetAdapter mAssetAdapter = new NodeAssetAdapter();
            holder.mRecyclerViewAssets.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            holder.mRecyclerViewAssets.setAdapter(mAssetAdapter);
            mAssetAdapter.updateItems(new ArrayList<>(item.getNodeDto().Assets.values()));
        }

        holder.mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClicked(item);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateItems(List<NodeVM> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnBtnPayClickListener(IOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    interface IOnItemClickListener {

        void onItemClicked(NodeVM nodeDto);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_collapsed)
        ViewGroup mLayoutCollapsed;

        @BindView(R.id.btn_hide)
        View mBtnHide;

        @BindView(R.id.btn_show)
        View mBtnShow;

        @BindView(R.id.btn_pay)
        View mBtnPay;

        @BindView(R.id.tv_node_eth_address)
        TextView mTvEthAddress;

        @BindView(R.id.tv_node_name)
        TextView mTvName;

        @BindView(R.id.tv_node_location)
        TextView mTvLocation;

        @BindView(R.id.tv_node_phone)
        TextView mTvPhone;

        @BindView(R.id.tv_node_connector)
        TextView mTvConnector;

        @BindView(R.id.tv_node_power)
        TextView mTvPower;

        @BindView(R.id.tv_node_latitude)
        TextView mTvLatitude;

        @BindView(R.id.tv_node_longitude)
        TextView mTvLongitude;

        @BindView(R.id.tv_node_connected)
        TextView mTvConnected;

        @BindView(R.id.tv_node_ip)
        TextView mTvIp;

        @BindView(R.id.recycler_view_assets)
        RecyclerView mRecyclerViewAssets;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
