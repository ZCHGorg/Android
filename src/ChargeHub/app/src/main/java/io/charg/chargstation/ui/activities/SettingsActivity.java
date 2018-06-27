package io.charg.chargstation.ui.activities;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.LocalDB;
import io.charg.chargstation.services.StringHelper;
import io.charg.chargstation.ui.dialogs.ChangeAddressDialog;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_smart_contract_address)
    TextView mTvScAddress;

    private LocalDB mLocalDb;

    @Override
    public int getResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
        loadSettings();
    }

    private void initServices() {
        mLocalDb = new LocalDB(this);
    }

    private void loadSettings() {
        mTvScAddress.setText(StringHelper.getShortEthAddress(mLocalDb.getValue(LocalDB.KEY_SMART_CONTRACT_ADDRESS, CommonData.SMART_CONTRACT_ADDRESS)));
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

    @OnClick(R.id.btn_edit)
    void onBtnEditClicked() {
        new ChangeAddressDialog(this).show();
    }
}
