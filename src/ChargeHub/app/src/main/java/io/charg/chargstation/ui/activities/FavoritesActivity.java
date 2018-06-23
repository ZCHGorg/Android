package io.charg.chargstation.ui.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.services.FavouriteService;
import io.charg.chargstation.ui.adapters.FavoriteAdapter;

public class FavoritesActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_items)
    RecyclerView mRvItems;

    private FavouriteService mFavoriteService;
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
        mFavoriteService = new FavouriteService(this);
    }

    private void loadItems() {
        mAdapter.setItems(mFavoriteService.getIds());
    }

    private void initRecyclerView() {
        mRvItems.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new FavoriteAdapter();
        mRvItems.setAdapter(mAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
