package io.charg.chargstation.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.support.design.widget.NavigationView;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.charg.chargstation.R;
import io.charg.chargstation.ui.activities.ContractActivity;
import io.charg.chargstation.ui.activities.FavoritesActivity;
import io.charg.chargstation.ui.activities.FilterActivity;
import io.charg.chargstation.ui.activities.mapActivity.MapActivity;
import io.charg.chargstation.ui.activities.becomeOwner.BecomeOwnerActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MapActivityTests {

    private MapActivity mMainActivity;
    private NavigationView mNavigationView;

    @Rule
    public ActivityTestRule<MapActivity> mRule = new ActivityTestRule<>(MapActivity.class);

    @Rule
    public GrantPermissionRule permissionsRule = GrantPermissionRule.grant(new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION}
    );

    @Before
    public void init() {
        mMainActivity = mRule.getActivity();

        onView(withText(R.string.ask_login))
                .check(matches(isDisplayed()))
        .check(matches(withText(R.string.yes)));

        mNavigationView = mRule.getActivity().findViewById(R.id.nav_view);
        openDrawer();
    }

    @After
    public void release() {

    }

    private void openDrawer() {
        final DrawerLayout drawerLayout = mMainActivity.findViewById(R.id.drawer_layout);
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    @Test
    public void navigateOnActivityFavoriteTest() {
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(FavoritesActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(activityMonitor);

        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.menu_favorites);

        onView(withText(menuItem.getTitle().toString())).perform(click());

        Activity expectActivity = activityMonitor.waitForActivityWithTimeout(5000);

        Assert.assertEquals(expectActivity.getClass().getName(), FavoritesActivity.class.getName());
    }

    @Test
    public void navigateOnActivityFilterTest() {
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(FilterActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(activityMonitor);

        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.menu_filter);

        onView(withText(menuItem.getTitle().toString())).perform(click());

        Activity expectActivity = activityMonitor.waitForActivityWithTimeout(5000);

        Assert.assertEquals(expectActivity.getClass().getName(), FilterActivity.class.getName());
    }

    @Test
    public void navigateOnActivityContractTest() {
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(ContractActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(activityMonitor);

        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.menu_contract);

        onView(withText(menuItem.getTitle().toString())).perform(click());

        Activity expectActivity = activityMonitor.waitForActivityWithTimeout(5000);

        Assert.assertEquals(expectActivity.getClass().getName(), ContractActivity.class.getName());
    }

    @Test
    public void navigateOnActivityBecomeOwnerTest() {
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(BecomeOwnerActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(activityMonitor);

        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.menu_become_station_owner);

        onView(withText(menuItem.getTitle().toString())).perform(click());

        Activity expectActivity = activityMonitor.waitForActivityWithTimeout(5000);

        Assert.assertEquals(expectActivity.getClass().getName(), BecomeOwnerActivity.class.getName());
    }


}
