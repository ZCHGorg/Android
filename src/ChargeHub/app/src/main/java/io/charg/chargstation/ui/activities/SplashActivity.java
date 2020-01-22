package io.charg.chargstation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.charg.chargstation.ui.activities.mapActivity.MapActivity;
import io.fabric.sdk.android.Fabric;

import io.charg.chargstation.R;

/**
 * Created by worker on 10.11.2017.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        try {
            ((TextView) findViewById(R.id.tv_app_version)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
