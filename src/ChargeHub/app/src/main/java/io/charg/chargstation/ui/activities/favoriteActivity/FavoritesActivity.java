package io.charg.chargstation.ui.activities.favoriteActivity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.local.FavouriteStationsRepository;
import io.charg.chargstation.ui.activities.BaseActivity;

public class FavoritesActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_items)
    RecyclerView mRvItems;

    @BindView(R.id.tv_no_data)
    TextView mTvNoData;

    private FavouriteStationsRepository mFavoriteService;
    private FavoriteAdapter mAdapter;

    @Override
    public int getResourceId() {
        return R.layout.activity_favorites;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
        initRecyclerView();
        loadItems();
    }

    private void initServices() {
        mFavoriteService = new FavouriteStationsRepository(this);
    }

    private void loadItems() {

        List<String> items = mFavoriteService.getItems();
        mAdapter.setItems(items);

        if (items.size() > 0) {
            mRvItems.setVisibility(View.VISIBLE);
            mTvNoData.setVisibility(View.GONE);
        } else {
            mRvItems.setVisibility(View.GONE);
            mTvNoData.setVisibility(View.VISIBLE);
        }

    }

    private void initRecyclerView() {
        mRvItems.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new FavoriteAdapter(this);
        mRvItems.setAdapter(mAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
