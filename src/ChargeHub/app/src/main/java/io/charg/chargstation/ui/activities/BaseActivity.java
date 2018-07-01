package io.charg.chargstation.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by worker on 01.11.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public abstract int getResourceId();

    public abstract void onActivate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceId());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        onActivate();
    }
}
