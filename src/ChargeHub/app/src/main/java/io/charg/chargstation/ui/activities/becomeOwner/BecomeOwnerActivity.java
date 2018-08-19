package io.charg.chargstation.ui.activities.becomeOwner;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.ui.activities.BaseActivity;

public class BecomeOwnerActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public int getResourceId() {
        return R.layout.activity_become_owner;
    }

    @Override
    public void onActivate() {
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @OnClick(R.id.btn_show_family_plan)
    void onBtnShowFamilyPlanClicked() {
        startActivity(new Intent(this, FamilyPlanActivity.class));
    }

    @OnClick(R.id.btn_show_demo_video)
    void onBtnShowDemoVideoClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=_tDKp1fo5HA")));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
