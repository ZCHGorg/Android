package io.charg.chargstation.ui.activities.sendChargActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.shuhart.stepview.StepView;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.ui.activities.BaseAuthActivity;
import io.charg.chargstation.ui.activities.sendChargActivity.fragments.ConfirmFrg;
import io.charg.chargstation.ui.activities.sendChargActivity.fragments.ResultFrg;
import io.charg.chargstation.ui.activities.sendChargActivity.fragments.SelectAmountFrg;
import io.charg.chargstation.ui.fragments.SelectDestinationFrg;
import io.charg.chargstation.ui.activities.sendChargActivity.fragments.SendingFrg;
import io.charg.chargstation.ui.fragments.BaseFragment;

public class SendChargActivity extends BaseAuthActivity {

    private AccountService mAccountService;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.step_view)
    StepView mStepView;

    private List<BaseFragment> mFragments;
    private FragmentPagerAdapter mAdapter;

    private SelectDestinationFrg mDestFrg;
    private SelectAmountFrg mAmountFrg;
    private ConfirmFrg mConfirmFrg;
    private SendingFrg mSendingFrg;
    private ResultFrg mResultFrg;

    private String mDestEthAddress;
    private BigInteger mAmount;

    @Override
    public int getResourceId() {
        return R.layout.activity_send_charg;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
        initStepView();
        initViewPager();
    }

    private void initServices() {
        mAccountService = new AccountService(this);
    }

    private void initStepView() {
        mStepView.setSteps(new ArrayList<String>() {{
            add(getString(R.string.destination));
            add(getString(R.string.amount));
            add(getString(R.string.confirm));
            add(getString(R.string.sending));
            add(getString(R.string.result));
        }});
    }

    private void navigateTo(Fragment frg) {
        int position = mAdapter.getItemPosition(frg);
        mViewPager.setCurrentItem(position, true);
        mStepView.go(position, true);
    }

    private void initViewPager() {

        mFragments = new ArrayList<>();

        mDestFrg = SelectDestinationFrg.newInstance(mDestEthAddress);
        mFragments.add(mDestFrg);

        mAmountFrg = new SelectAmountFrg();
        mFragments.add(mAmountFrg);

        mConfirmFrg = new ConfirmFrg();
        mFragments.add(mConfirmFrg);

        mSendingFrg = new SendingFrg();
        mSendingFrg.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                mResultFrg.setResult(result);
                mResultFrg.invalidate();
                navigateTo(mResultFrg);
            }
        });
        mSendingFrg.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                mResultFrg.setError(message);
                mResultFrg.invalidate();
                navigateTo(mResultFrg);
            }
        });
        mFragments.add(mSendingFrg);

        mResultFrg = new ResultFrg();
        mFragments.add(mResultFrg);

        mViewPager.setAdapter(mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public int getItemPosition(Object object) {
                return mFragments.indexOf(object);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_next)
    void onBtnNextClicked() {
        Fragment mCurrentFrg = mAdapter.getItem(mViewPager.getCurrentItem());
        if (mCurrentFrg.equals(mDestFrg)) {
            if (!mDestFrg.isValid()) {
                return;
            }
            mDestEthAddress = mDestFrg.getDestinationEth();
            mAmountFrg.setAmount(mAmount);
            mAmountFrg.invalidate();
            navigateTo(mAmountFrg);
        } else if (mCurrentFrg.equals(mAmountFrg)) {
            if (!mAmountFrg.isValid()) {
                return;
            }
            mAmount = mAmountFrg.getAmount();
            mConfirmFrg.setFrom(mAccountService.getEthAddress());
            mConfirmFrg.setTo(mDestEthAddress);
            mConfirmFrg.setAmount(mAmount);
            mConfirmFrg.invalidate();
            navigateTo(mConfirmFrg);
        } else if (mCurrentFrg.equals(mConfirmFrg)) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            mSendingFrg.setAddress(mDestEthAddress);
            mSendingFrg.setAmount(mAmount);
            mSendingFrg.executeAsync();
            navigateTo(mSendingFrg);
        }
    }
}
