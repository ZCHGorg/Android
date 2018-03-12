package io.charg.chargstation.ui.activities;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.charg.chargstation.R;
import io.charg.chargstation.models.StationFilter;
import io.charg.chargstation.services.FilteringService;

/**
 * Created by worker on 19.12.2017.
 */

public class FilterActivity extends BaseActivity {

    @BindView(R.id.rv_connector_filters)
    RecyclerView rvConnectorViews;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    private FilteringService mFilteringService;

    @Override
    public int getResourceId() {
        return R.layout.activity_filter;
    }

    @Override
    public void onActivate() {
        initToolbar();
        mFilteringService = new FilteringService(this);
        initRecyclerViewConnectorFilters();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initRecyclerViewConnectorFilters() {
        rvConnectorViews.setLayoutManager(new LinearLayoutManager(this));
        rvConnectorViews.setAdapter(new FilterAdapter(mFilteringService.getStaticConnectorFilters()));
        rvConnectorViews.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

        private List<StationFilter> mItems = new ArrayList<>();

        public FilterAdapter(List<StationFilter> items) {
            mItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final StationFilter item = mFilteringService.loadConnectorFilter(mItems.get(position).getIndex());

            holder.tvFilterName.setText(item.getTitle());
            holder.mSwitch.setChecked(item.isChecked());

            holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setChecked(isChecked);
                    mFilteringService.saveConnectorFilter(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_filter_name)
            TextView tvFilterName;

            @BindView(R.id.switch_filter_level)
            Switch mSwitch;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
