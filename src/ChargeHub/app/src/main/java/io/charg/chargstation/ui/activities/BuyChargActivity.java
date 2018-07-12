package io.charg.chargstation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;

public class BuyChargActivity extends BaseAuthActivity {

    private static final int REQUEST_CODE_BRAINTREE = 1001;

    @BindView(R.id.et_amount_chg)
    EditText mEtAmountChg;

    @BindView(R.id.tv_final_price)
    TextView mTvFinalPrice;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public int getResourceId() {
        return R.layout.activity_buy_charg;
    }

    @Override
    public void onActivate() {
        initToolbar();
        initEtAmountChg();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initEtAmountChg() {
        mEtAmountChg.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                refreshFinalPrice();

                if (mEtAmountChg.getText().toString().isEmpty()) {
                    mEtAmountChg.setError("Amount cannot be empty");
                }

                return false;
            }
        });
    }

    private void refreshFinalPrice() {
        try {
            int amount = Integer.parseInt(mEtAmountChg.getText().toString());
            float finalPrice = amount * CommonData.PRICE_CHG_USD;

            mTvFinalPrice.setText(String.valueOf(finalPrice));
        } catch (Exception ex) {
            mTvFinalPrice.setText("");
        }
    }

    @OnClick(R.id.btn_credit)
    void onBtnCreditClicked() {
        invokeBraintree();
    }

    public void invokeBraintree() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiI5OGE1ZWQxZDBiNDgzOTUzZGU4MDIzZTdjODM5ZTdkMTNjYWQ0M2U0NzIwMjZkMTI0Y2NiODVjODU4ZGFhZDAyfGNyZWF0ZWRfYXQ9MjAxOC0wNy0xMFQxMzowMjoxNi40NjExNDQ3NjErMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vb3JpZ2luLWFuYWx5dGljcy1zYW5kLnNhbmRib3guYnJhaW50cmVlLWFwaS5jb20vMzQ4cGs5Y2dmM2JneXcyYiJ9LCJ0aHJlZURTZWN1cmVFbmFibGVkIjp0cnVlLCJwYXlwYWxFbmFibGVkIjp0cnVlLCJwYXlwYWwiOnsiZGlzcGxheU5hbWUiOiJBY21lIFdpZGdldHMsIEx0ZC4gKFNhbmRib3gpIiwiY2xpZW50SWQiOm51bGwsInByaXZhY3lVcmwiOiJodHRwOi8vZXhhbXBsZS5jb20vcHAiLCJ1c2VyQWdyZWVtZW50VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3RvcyIsImJhc2VVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFzc2V0c1VybCI6Imh0dHBzOi8vY2hlY2tvdXQucGF5cGFsLmNvbSIsImRpcmVjdEJhc2VVcmwiOm51bGwsImFsbG93SHR0cCI6dHJ1ZSwiZW52aXJvbm1lbnROb05ldHdvcmsiOnRydWUsImVudmlyb25tZW50Ijoib2ZmbGluZSIsInVudmV0dGVkTWVyY2hhbnQiOmZhbHNlLCJicmFpbnRyZWVDbGllbnRJZCI6Im1hc3RlcmNsaWVudDMiLCJiaWxsaW5nQWdyZWVtZW50c0VuYWJsZWQiOnRydWUsIm1lcmNoYW50QWNjb3VudElkIjoiYWNtZXdpZGdldHNsdGRzYW5kYm94IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sIm1lcmNoYW50SWQiOiIzNDhwazljZ2YzYmd5dzJiIiwidmVubW8iOiJvZmYifQ==");
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE_BRAINTREE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BRAINTREE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                mTvResult.setText(result.getPaymentMethodNonce().getNonce());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mTvResult.setText(R.string.cancel_user);
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                mTvResult.setText(error.getMessage());
            }
        }
    }
}
